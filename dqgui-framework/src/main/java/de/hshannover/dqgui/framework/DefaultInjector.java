package de.hshannover.dqgui.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import de.hshannover.dqgui.framework.api.Dependencies;

final class DefaultInjector extends CommonInjector {
    private final HashMap<String, Field> CONTAINER_CACHE = new HashMap<>();

    DefaultInjector(DependencyConfiguration config, Dependencies container) {
        super(config, container);
    }

    @Override
    public Object inject(Class<?> controller, Object[] constructorArguments) throws Exception {
        Constructor<?> c = ReflectionUtility.findMatchingConstructor(controller, constructorArguments);
        Object instance = ReflectionUtility.createInstance(c, constructorArguments);
        if(!config.consumers.containsKey(controller))
            return instance;
        for (String field : config.consumers.get(controller)) {
            if (!CONTAINER_CACHE.containsKey(field)) {
                Field container = this.container.getClass().getDeclaredField(field);
                container.setAccessible(true);
                CONTAINER_CACHE.put(field, container);
            }
            Field inject = instance.getClass().getDeclaredField(field);
            inject.setAccessible(true);
            inject.set(instance, CONTAINER_CACHE.get(field).get(container));
        }
        return instance;
    }

}
