package framework.mock;

import de.hshannover.dqgui.framework.api.Dependencies;

@SuppressWarnings("unused")
public class ContainerTypes implements Dependencies {

    public class A {

    }

    public class B extends A {

    }

    public class C {

    }

    private final A a = null;
    private final B b = null;
    private final C c = null;
    private final C d = null;
    private final A e = null;

    @Override
    public void loadEager() {

    }

    @Override
    public void onClose() {

    }

}
