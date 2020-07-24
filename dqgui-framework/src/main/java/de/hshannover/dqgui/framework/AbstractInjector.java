package de.hshannover.dqgui.framework;

import de.hshannover.dqgui.framework.api.Dependencies;

abstract class AbstractInjector {
    protected final DependencyConfiguration config;
    protected final Dependencies container;

    public AbstractInjector(DependencyConfiguration config, Dependencies container) {
        this.config = config;
        this.container = container;
    }

    public abstract Object inject(Class<?> controller, Object[] constructorArguments) throws Exception;
    public abstract void handleInjectedCallbacks(Class<?> controller, Object instance, Object... args) throws Exception;

    public void closeContainer() {
        if(container != null)
            container.onClose();
    }

}
