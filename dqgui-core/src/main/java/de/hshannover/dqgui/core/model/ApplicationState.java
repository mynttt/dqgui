package de.hshannover.dqgui.core.model;

import java.util.ArrayList;
import java.util.List;
import de.hshannover.dqgui.core.util.RemoteExecution.ExecutionStrategy;
import de.hshannover.dqgui.core.util.RepositorySearch.SearchOptions.PatternInterpretation;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.api.Recoverable;

/**
 *
 * @author Julian Sender
 *          The bean for saving / serializing the application state
 *          when application was closed and re-opened
 */
public final class ApplicationState implements Recoverable {
    
    public final static class LastSelectedSearchConfiguration {
        private final PatternInterpretation pattern;
        private final List<DSLComponentType> scope = new ArrayList<>();
        
        public LastSelectedSearchConfiguration(PatternInterpretation pattern, List<DSLComponentType> scope) {
            this.pattern = pattern;
            this.scope.addAll(scope);
        }

        public PatternInterpretation getPattern() {
            return pattern;
        }

        public List<DSLComponentType> getScope() {
            return scope;
        }
    }
    
    private LastSelectedSearchConfiguration search;
    private String environmentComboBoxValue;
    private DSLComponent lastSelectedActionComboBox;
    private ArrayList<DSLComponent> openedTabs = new ArrayList<>();
    private int tabIndex;
    private ExecutionStrategy executionEnvironment;

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getEnvironmentComboBoxValue() {
        return environmentComboBoxValue;
    }

    public void setEnvironmentComboBoxValue(String environmentComboBoxValue) {
        this.environmentComboBoxValue = environmentComboBoxValue;
    }

    public DSLComponent getLastSelectedActionComboBox() {
        return lastSelectedActionComboBox;
    }

    public void setLastSelectedActionComboBox(DSLComponent lastSelectedActionComboBox) {
        this.lastSelectedActionComboBox = lastSelectedActionComboBox;
    }

    public ArrayList<DSLComponent> getOpenedTabs() {
        return openedTabs;
    }

    public void setOpenedTabs(ArrayList<DSLComponent> openedTabs) {
        this.openedTabs = openedTabs;
    }

    public LastSelectedSearchConfiguration getSearch() {
        return search;
    }

    public void setSearch(LastSelectedSearchConfiguration search) {
        this.search = search;
    }

    public void setExecutionEnvironment(ExecutionStrategy executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }

    public ExecutionStrategy getExecutionEnvironment() {
        return executionEnvironment;
    }
}
