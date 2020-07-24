package framework.mock;

import de.hshannover.dqgui.framework.api.Dependencies;

public class ContainerSerializeRoot implements Dependencies {
    @SuppressWarnings("unused")
    private final MockField mock = null;

    @Override
    public void loadEager() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public String serializationRoot() {
        return ".";
    }
}
