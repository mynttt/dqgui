package de.hshannover.dqgui.core.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.io.api.ProjectHandler;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback;
import de.hshannover.dqgui.core.ui.NotificationService;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.remote.RemoteResult;
import de.hshannover.dqgui.framework.api.Destructible;
import de.hshannover.dqgui.framework.api.Recoverable;
import de.hshannover.dqgui.framework.model.Pointer;
import de.mvise.iqm4hd.api.ExecutionReport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Allows management and collection of finished tasks.
 *
 * @author Marc Herschel
 *
 */
public final class CompletedTasks implements Recoverable, Destructible {
    private List<Iqm4hdMetaData> results = Collections.synchronizedList(new ArrayList<>());
    private transient ObservableList<Iqm4hdMetaData> uiUpdateList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private transient Pointer<NotificationService> service;
    private transient ProjectHandler handler;

    public void registerDependencies(Pointer<NotificationService> service, ProjectHandler handler) {
        this.service = service;
        this.handler = handler;
    }
    
    /**
     * Add a listener to observe finished tasks.
     * @param listener to add
     */
    public void addCompletedListener(ListChangeListener<? super Iqm4hdMetaData> listener) {
        uiUpdateList.addListener(listener);
    }

    /**
     * Remove a listener that listens to finished tasks.
     * @param listener to remove
     */
    public void removeCompletedListener(ListChangeListener<? super Iqm4hdMetaData> listener) {
        uiUpdateList.removeListener(listener);
    }
    
    public void removeAll(List<Iqm4hdMetaData> reports) {
        if(reports == null || reports.isEmpty()) return;
        removeInternal(reports);
    }

    /**
     * Remove all failed results.
     */
    public void pruneFailed() {
        pruneInternal();
    }

    /**
     * @return all results.
     */
    public List<Iqm4hdMetaData> getAllReports() {
        return Collections.unmodifiableList(new ArrayList<>(results));
    }
    
    /**
     * Remove for a specific identifier (if the identifier exists)
     * @param identifier to remove from the completed tasks list
     */
    public void removeForIdentifier(String identifier) {
        Optional<Iqm4hdMetaData> toRemove = results.stream()
                .filter(f -> f.getIdentifier().equals(identifier))
                .findFirst();
        toRemove.ifPresent(this::removeInternal);
    }
    
    public void complete(RemoteResult r) {
        addInternal(r.getMeta(), r.getReport());
        sharedComplete(r.getMeta());
    }

    void complete(Iqm4hdMetaData completed, ExecutionReport report) {
        completed.registerHumanReadableFinished();
        addInternal(completed, report);
        sharedComplete(completed);
    }
    
    private void sharedComplete(Iqm4hdMetaData m) {
        if(m.isError()) {
            service.safeGet().ifPresent(s -> s.notifyError(m));
        } else {
            service.safeGet().ifPresent(s -> s.notifySuccess(m));
        }
    }

    private void addInternal(Iqm4hdMetaData result, ExecutionReport report) {
        results.add(result);
        handler.dumpReport(result, report, new Iqm4hdFeedback(), this);
        Platform.runLater(() -> uiUpdateList.add(result));
    }

    private void removeInternal(List<Iqm4hdMetaData> results) {
        CompletableFuture.supplyAsync(() -> {
            this.results.removeAll(results);
            handler.removeReports(results, this);
            return true;
        }, Executors.SERVICE)
            .exceptionally(f -> {
                Logger.error(f);
                return false;
            })
            .thenRun(() -> Platform.runLater(() -> uiUpdateList.removeAll(results)));
    }
    
    private void removeInternal(Iqm4hdMetaData result) {
        results.remove(result);
        handler.removeReport(result, this);
        Platform.runLater(() -> uiUpdateList.remove(result));
    }

    private void pruneInternal() {
        results.stream()
            .filter(Iqm4hdMetaData::isError)
            .collect(Collectors.toList())
            .forEach(this::removeInternal);
    }
    
    public void downloadFromRepo(List<Iqm4hdMetaData> data) {
        results.addAll(data);
    }
    
    public void removeFromLocalCopy(String hash) {
        Optional<Iqm4hdMetaData> result = results.stream().filter(r -> r.getHash().equals(hash)).findFirst();
        if(result.isPresent())
            results.remove(result.get());
    }
    
    public void syncReports(List<Iqm4hdMetaData> reports) {
        Set<Iqm4hdMetaData> cur = new HashSet<>(results), is = new HashSet<>(reports), joined = new HashSet<>(results);
        joined.addAll(is);
        List<Iqm4hdMetaData> added = new ArrayList<>(), removed = new ArrayList<>();
        joined.forEach(r -> {
            if(cur.contains(r) && is.contains(r))
                return;
            if(cur.contains(r) && !is.contains(r)) {
                removed.add(r);
            } else {
                added.add(r);
            }
        });
        Platform.runLater(() -> {
            results.removeAll(removed);
            results.addAll(added);
            Platform.runLater(() -> uiUpdateList.removeAll(removed));
            Platform.runLater(() -> uiUpdateList.addAll(added));
        });
    }

    @Override
    public void recoverHook() {
        results = Collections.synchronizedList(results);
        uiUpdateList.addAll(results);
    }
    
    @Override
    public void destruct() {
        service = null;
        handler = null;
    }
}
