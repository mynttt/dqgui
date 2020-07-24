package framework.mock;

import de.hshannover.dqgui.framework.api.Dependencies;

public class ContainerStaticField implements Dependencies {
    @SuppressWarnings("unused")
    private static MockField staticField;

    @Override
    public void loadEager() {

    }

    @Override
    public void onClose() {

    }

}
