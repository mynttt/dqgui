package de.hshannover.dqgui.core.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.execution.model.RepoType;
import de.hshannover.dqgui.framework.api.Recoverable;
import de.hshannover.dqgui.framework.signal.Signal;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * @author Julian Sender
 * @author Marc Herschel
 */
public final class ApplicationProperties implements Recoverable {
    private RepoType type = RepoType.FILE_SYSTEM;
    private String globalChecks, rPath, rScriptPath, rArgs, remoteHost, remoteKey;
    private boolean rServeAutoStart, showWelcome = true, autoSuggest, autoIndent;
    private DatabaseConnection selected;
    private Project selectedProject;
    private final List<DatabaseConnection> dbRepositories = new ArrayList<>();
    private final Set<Project> fileSystemProjects = new HashSet<>();
    private final transient ReadOnlyObjectWrapper<RepoType> typeProperty = new ReadOnlyObjectWrapper<>();
    private final transient ReadOnlyObjectWrapper<String> hostProperty = new ReadOnlyObjectWrapper<>(),
                                                          keyProperty  = new ReadOnlyObjectWrapper<>();
    private final transient BooleanBinding remoteIsSetProperty = hostProperty.isNotNull().and(keyProperty.isNotNull());
    private final transient Signal rServeAutoStartSignal = new Signal(), rServeLocationChangeSignal = new Signal();
    
    public ReadOnlyObjectProperty<RepoType> repoTypeProperty() {
        return typeProperty.getReadOnlyProperty();
    }
    
    public RepoType getRepoType() {
        return type;
    }

    public void setRepoType(RepoType type) {
        if(this.type != type) {
            this.type = type;
            typeProperty.set(type);
        }
    }

    public Optional<DatabaseConnection> getSelectedDbRepo() {
        return Optional.ofNullable(selected);
    }

    public List<DatabaseConnection> getDbRepositories() {
        return dbRepositories;
    }
    
    public void setDbRepository(DatabaseConnection selected, List<DatabaseConnection> dbRepositories) {
        if(!Objects.equals(selected, this.selected) || !Objects.equals(dbRepositories, this.dbRepositories)) {
            this.selected = selected;
            this.dbRepositories.clear();
            this.dbRepositories.addAll(dbRepositories);
        }
    }

    public String getGlobalChecks() {
        return globalChecks;
    }

    public void setGlobalChecks(String globalChecks) {
        if(this.globalChecks == null || !this.globalChecks.equals(globalChecks)) {
            this.globalChecks = globalChecks;
        }
    }

    public boolean isShowWelcome() {
        return showWelcome;
    }

    public void setShowWelcome(boolean showWelcome) {
        this.showWelcome = showWelcome;
        dump();
    }

    public void setRSettings(String rPath, String rScriptPath, String rArgs) {
        this.rPath = rPath;
        this.rScriptPath = rScriptPath;
        this.rArgs = rArgs;
        rServeLocationChangeSignal.fire();
        dump();
    }
    
    public void setRServeAutoStart(boolean rAutostart) {
        this.rServeAutoStart = rAutostart;
        rServeAutoStartSignal.fire();
        dump();
    }
    
    public String getrArgs() {
        return rArgs;
    }

    public String getrScriptPath() {
        return rScriptPath;
    }
    
    public boolean isRServeAutoStart() {
        return rServeAutoStart;
    }
    
    public String getrPath() {
        return rPath;
    }

    @Override
    public void recoverHook() {
        keyProperty.set(remoteKey);
        hostProperty.set(remoteHost);
        
        Path p = globalChecks == null ? null : Paths.get(globalChecks);
        Path fallback = Config.configPathFor("globalChecks");
        
        try {
            if(p == null || !Files.isDirectory(p)) {
                Files.createDirectories(fallback);
                this.globalChecks = fallback.toAbsolutePath().toString();
            }
        } catch(Exception e) {
            Logger.error(e);
        }
        
        fileSystemProjects.removeIf(prj -> !Files.isDirectory(Paths.get(prj.getIdentifier())));
        
        if(selectedProject != null && selectedProject.getType() == RepoType.FILE_SYSTEM && !Files.isDirectory(Paths.get(selectedProject.getIdentifier())))
            selectedProject = null;
        
        typeProperty.set(type);
        try {
            if(selected != null && !selected.engineIdentifierIsRegistered())
                selected = null;
            
            Iterator<DatabaseConnection> it = dbRepositories.iterator();
            while(it.hasNext()) {
                if(!it.next().engineIdentifierIsRegistered())
                    it.remove();
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public void resetDbRepo() {
        this.dbRepositories.clear();
        this.selected = null;
        dump();
    }

    public void setAutoSuggest(Boolean autosuggest) {
        this.autoSuggest = autosuggest;
        dump();
    }
    
    public boolean isAutoSuggest() {
        return autoSuggest;
    }

    public void setAutoIndent(Boolean autoIndent) {
        this.autoIndent = autoIndent;
        dump();
    }
    
    public boolean isAutoIndent() {
        return autoIndent;
    }

    public Signal getrServeAutoStartSignal() {
        return rServeAutoStartSignal;
    }

    public Signal getrServeLocationChangeSignal() {
        return rServeLocationChangeSignal;
    }

    public Set<Project> getFileSystemProjectsReadOnlyCopy() {
        return Collections.unmodifiableSet(new HashSet<>(fileSystemProjects));
    }
    
    public void removeFilesystemProject(Project p) {
        fileSystemProjects.remove(p);
        dump();
    }
    
    public void addFilesystemProject(Project p) {
        fileSystemProjects.add(p);
        dump();
    }

    public Optional<Project> getSelectedProject() {
        return Optional.ofNullable(selectedProject);
    }
    
    public void setSelectedProject(Project prj) {
        this.selectedProject = prj;
        dump();
    }

    public void setRemote(String remoteHost, String remoteKey) {
        this.remoteHost = Utility.formatHost(remoteHost);
        this.remoteKey = remoteKey;
        keyProperty.set(remoteKey);
        hostProperty.set(remoteHost);
        dump();
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getRemoteKey() {
        return remoteKey;
    }

    public BooleanBinding serverSetProperty() {
        return remoteIsSetProperty;
    }
}