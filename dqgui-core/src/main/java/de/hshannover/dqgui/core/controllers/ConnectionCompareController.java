package de.hshannover.dqgui.core.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.CollectionChange;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.util.TableGenerator;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseEngine;
import de.hshannover.dqgui.execution.database.gui.IconSupport;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.signal.SignalHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class ConnectionCompareController extends AbstractWindowController {
    private static final int DIMENSION = 22;
    private static final Image ENV_ICON = new Image(
            ConnectionCompareController.class.getResourceAsStream(
                    Config.APPLICATION_PATH_ASSETS_IMAGE_DB_TREE+"_DATABASE.png"), DIMENSION, DIMENSION, true, false);
    
    /*
     * Injected by ApplicationContext
     */
    private final ProjectHandle projectHandle = null;
    
    private final SignalHandler suicideHandler = () -> unregister();
    
    @FXML
    ComboBox<DatabaseEnvironment> environmentsFrom, environmentsTo;
    @FXML
    ComboBox<ConEnv> connectionsFrom, connectionsTo;
    @FXML
    Button compareConnections, compareEnvironments;
    @FXML
    TextArea environmentsDiff, connectionsDiff;
    
    private static class ConEnv {
        private final String identifier;
        private final DatabaseConnection con;
        
        private ConEnv(String identifier, DatabaseConnection con) {
            this.identifier = identifier;
            this.con = con;
        }

        @Override
        public String toString() {
            return identifier + " -> " + con.getIdentifier();
        }
    }
    
    private static class EnvCmpDTO {
        
        private static class ConnectionWrapper {
            DatabaseConnection con;
            
            private ConnectionWrapper(DatabaseConnection con) {
                this.con = con;
            }
            
            @Override
            public int hashCode() {
                return Objects.hash(con.getCredential(), con.getCustomAttributes(), con.getCustomParameters(), con.getEngine().uniqueIdentifier().toString(), con.getIdentifier(), con.getSocket());
            }

            @Override
            public boolean equals(Object obj) {
                if(obj == null)
                    return false;
                if(getClass() != obj.getClass())
                    return false;
                DatabaseConnection other = ((ConnectionWrapper) obj).con;
                return Objects.equals(con.getCredential(), other.getCredential()) && Objects.equals(con.getCustomAttributes(), other.getCustomAttributes())
                        && Objects.equals(con.getCustomParameters(), other.getCustomParameters())
                        && Objects.equals(con.getEngine().uniqueIdentifier().toString(), other.getEngine().uniqueIdentifier().toString())
                        && Objects.equals(con.getIdentifier(), other.getIdentifier()) && Objects.equals(con.getSocket(), other.getSocket());
            }
            
            @Override
            public String toString() {
                return con.getIdentifier();
            }
        }
        
        private final String identifier;
        private final Set<ConnectionWrapper> con;
        
        private EnvCmpDTO(DatabaseEnvironment e) {
            identifier = e.getIdentifier();
            con = e.getConnections().stream().map(ConnectionWrapper::new).collect(Collectors.toSet());
        }
    }
    
    @FXML
    void initialize() {
        projectHandle.projectChangedSignal().register(suicideHandler);
        ObservableList<DatabaseEnvironment> env = FXCollections.observableArrayList();
        ObservableList<ConEnv> con = FXCollections.observableArrayList();
        env.addAll(projectHandle.getCurrentEnvironment().unsafeGet().getEnvironments());
        Collections.sort(env, (e1, e2) -> e1.getIdentifier().compareToIgnoreCase(e2.getIdentifier()));
        env.forEach(e -> e.getConnections().forEach(c -> con.add(new ConEnv(e.getIdentifier(), c))));
        Collections.sort(con, (c1, c2) -> c1.toString().compareToIgnoreCase(c2.toString()));
        
        Supplier<ListCell<DatabaseEnvironment>> dbEnvCell = () -> new ListCell<DatabaseEnvironment>() {

            @Override
            protected void updateItem(DatabaseEnvironment item, boolean empty) {
                super.updateItem(item, empty);
                
                if(item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                
                setText(item.getIdentifier());
                setGraphic(new ImageView(ENV_ICON));
            }
            
        };
        
        Supplier<ListCell<ConEnv>> conEnvCell = () -> new ListCell<ConEnv>() {

            @Override
            protected void updateItem(ConEnv item, boolean empty) {
                super.updateItem(item, empty);
                
                if(item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                
                setText(item.toString());
                
                ImageView view = new ImageView(IconSupport.requestListIcon(item.con.getEngine().guiConfiguration()));
                view.setFitHeight(DIMENSION);
                view.setFitWidth(DIMENSION);
                setGraphic(view);
            }
            
        };
        
        environmentsFrom.setItems(env);
        environmentsFrom.setCellFactory(call -> dbEnvCell.get());
        environmentsFrom.setButtonCell(dbEnvCell.get());
        environmentsFrom.getSelectionModel().selectFirst();
        
        environmentsTo.setItems(env);
        environmentsTo.setCellFactory(call -> dbEnvCell.get());
        environmentsTo.setButtonCell(dbEnvCell.get());
        environmentsTo.getSelectionModel().selectFirst();
        
        connectionsFrom.setItems(con);
        connectionsFrom.setCellFactory(call -> conEnvCell.get());
        connectionsFrom.setButtonCell(conEnvCell.get());
        connectionsFrom.getSelectionModel().selectFirst();

        connectionsTo.setItems(con);
        connectionsTo.setCellFactory(call -> conEnvCell.get());
        connectionsTo.setButtonCell(conEnvCell.get());
        connectionsTo.getSelectionModel().selectFirst();

        compareConnections.disableProperty().bind(connectionsFrom.getSelectionModel().selectedItemProperty().isNull().
                or(connectionsTo.getSelectionModel().selectedItemProperty().isNull()));
        compareEnvironments.disableProperty().bind(environmentsFrom.getSelectionModel().selectedItemProperty().isNull().
                or(environmentsTo.getSelectionModel().selectedItemProperty().isNull()));
    }
    
    @FXML
    void compareConnections() {
        DatabaseConnection from = connectionsFrom.getSelectionModel().getSelectedItem().con;
        DatabaseConnection to = connectionsTo.getSelectionModel().getSelectedItem().con;
        Diff d = javers().compare(from, to);
        connectionsDiff.setText(d.getChanges().isEmpty() ? "Connections are equal." : formatChangesConnections(d));
    }
    
    @FXML
    void compareEnvironments() {
        DatabaseEnvironment from = environmentsFrom.getSelectionModel().getSelectedItem();
        DatabaseEnvironment to = environmentsTo.getSelectionModel().getSelectedItem();
        EnvCmpDTO e1 = new EnvCmpDTO(from), e2 = new EnvCmpDTO(to);
        Diff d = javers().compare(e1, e2);
        environmentsDiff.setText(d.getChanges().isEmpty() ? "Environments are equal." : formatChangesEnvironments(d, e1, e2));
    }

    private String formatChangesEnvironments(Diff d, EnvCmpDTO e1, EnvCmpDTO e2) {
        StringBuilder sb = new StringBuilder("Changes detected: \n\n");
        
        List<CollectionChange> collectionChanges = new ArrayList<>();
        
        d.getChanges()
            .stream()
            .forEach(c -> {
                if(c instanceof CollectionChange)
                    collectionChanges.add(CollectionChange.class.cast(c));
            });
        
        for(CollectionChange c : collectionChanges) {
            if(!c.getRemovedValues().isEmpty()) {
                sb.append("Exist only in '" + e1.identifier + "': \n\n");
                e1.con.stream().filter(ch -> !e2.con.contains(ch)).forEach(ch -> sb.append(represetAsTable(ch.con)));
            }
            
            if(!c.getAddedValues().isEmpty()) {
                sb.append("Exist only in '" + e2.identifier + "': \n\n");
                e2.con.stream().filter(ch -> !e1.con.contains(ch)).forEach(ch -> sb.append(represetAsTable(ch.con)));
            }
        }
        
        return sb.toString();
    }
    
    private String represetAsTable(DatabaseConnection con) {
        TableGenerator infoGenerator = new TableGenerator();
        
        List<List<String>> rowsList = new ArrayList<>();
        
        String[][] infoArray = new String[][] {
            {"Identifier", con.getIdentifier()},
            {"Engine", con.getEngine().uniqueIdentifier().toString()},
            {"Host", con.getSocket().getHost()},
            {"Port", Integer.toString(con.getSocket().getPort())},
            {"Database", Objects.toString(con.getCredential().getDatabase())},
            {"Username", Objects.toString(con.getCredential().getUsername())},
            {"Password", Objects.toString(con.getCredential().getDatabase())},
        };
        
        for(String[] s : infoArray)
            rowsList.add(Arrays.asList(s));
        
        if(!con.getCustomAttributes().isEmpty())
            rowsList.add(Arrays.asList("Attributes", con.getCustomAttributes().toString()));
        
        if(!con.getCustomParameters().isEmpty())
            rowsList.add(Arrays.asList("Parameters", con.getCustomParameters().toString()));
        
        return infoGenerator.generateTable(Arrays.asList("Key", "Value"), rowsList);
    }

    private String formatChangesConnections(Diff d) {
        TableGenerator infoGenerator = new TableGenerator();
        List<List<String>> rowsList = new ArrayList<>();
        
        List<ValueChange> changes = d.getChanges().stream().map(ValueChange.class::cast).collect(Collectors.toList());
        List<ValueChange> sorted = new ArrayList<>();
        changes.removeIf(c -> {
            if(!c.getPropertyNameWithPath().contains(".")) {
                sorted.add(c);
                return true;
            }
            return false;
        });
        sorted.sort((c1, c2) -> c1.getPropertyNameWithPath().compareToIgnoreCase(c2.getPropertyNameWithPath()));
        changes.sort((c1, c2) -> c1.getPropertyNameWithPath().compareToIgnoreCase(c2.getPropertyNameWithPath()));
        sorted.addAll(changes);
        
        sorted.forEach(c ->
            rowsList.add(Arrays.asList(c.getPropertyNameWithPath(), Objects.toString(c.getLeft()), Objects.toString(c.getRight())))
        );
        
        return "Changes detected: \n\n" + infoGenerator.generateTable(Arrays.asList("Property", "From", "To"), rowsList);
    }
    
    private Javers javers() {
        return JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .registerIgnoredClass(DatabaseEngine.class)
                .registerIgnoredClass(UUID.class)
                .withPrettyPrint(true)
                .build();
    }
    
    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> unregister());
    }

    @Override
    public void onDestruction() {
        projectHandle.projectChangedSignal().unregister(suicideHandler);
    }
}
