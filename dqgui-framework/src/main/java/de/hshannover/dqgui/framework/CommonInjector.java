package de.hshannover.dqgui.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import de.hshannover.dqgui.framework.annotations.InjectedCallback;
import de.hshannover.dqgui.framework.api.Dependencies;

/**
 * Injector that does not inject/handle signals but creates instances and handles injection callbacks.<br>
 * Used when not using dependency injection.
 *
 * @author Marc Herschel
 *
 */
class CommonInjector extends AbstractInjector {

    CommonInjector(DependencyConfiguration config, Dependencies container) {
        super(config, container);
    }

    @Override
    public Object inject(Class<?> controller, Object[] constructorArguments) throws Exception {
        Constructor<?> c = ReflectionUtility.findMatchingConstructor(controller, constructorArguments);
        return ReflectionUtility.createInstance(c, constructorArguments);
    }

    @Override
    public void handleInjectedCallbacks(Class<?> controller, Object instance, Object... args) throws Exception {
        if(args == null || args.length == 0)
            return;
        List<Method> methods = ReflectionUtility.findMatchingMethods(ReflectionUtility.findMethodsWithAnnotation(controller, InjectedCallback.class), args);
        if(methods.isEmpty())
            return;
        if(methods.size() > 1) {
            Class<?>[] types = new Class[args.length];
            for(int i = 0; i < types.length; i++)
                types[i] = args[i].getClass();
            throw new IllegalArgumentException(String.format("Supplied arguments %s for InjectedCallback but class %s has > 1 methods. Signature of annotated methods must be unique.", Arrays.toString(types), controller.getCanonicalName()));
        }
        Method method = methods.get(0);
        method.setAccessible(true);
        method.invoke(instance, args);
    }

}
