package de.hshannover.dqgui.framework;

import javafx.stage.Stage;

final class ControllerStage {
    private final AbstractWindowController controller;
    private final Stage stage;

    ControllerStage(AbstractWindowController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    AbstractWindowController getController() {
        return controller;
    }

    Stage getStage() {
        return stage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((controller == null) ? 0 : controller.hashCode());
        result = prime * result + ((stage == null) ? 0 : stage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ControllerStage other = (ControllerStage) obj;
        if (controller == null) {
            if (other.controller != null)
                return false;
        } else if (!controller.equals(other.controller))
            return false;
        if (stage == null) {
            if (other.stage != null)
                return false;
        } else if (!stage.equals(other.stage))
            return false;
        return true;
    }

}
