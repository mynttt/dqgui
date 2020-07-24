package de.hshannover.dqgui.framework;

import static de.hshannover.dqgui.framework.ApplicationContext.NO_ARGS_CACHE;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import de.hshannover.dqgui.framework.exceptions.ReflectionUtilityException;

/**
 * Utility class for various reflective operations.
 *
 * @author Marc Herschel
 *
 */
public final class ReflectionUtility {
    private static final Class<?>[] NO_ARGS_CLASS_CACHE = new Class[0];

    private ReflectionUtility() {}

    public static <T> T createInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ReflectionUtilityException {
        return clazz.cast(createInstance(findMatchingConstructor(clazz, NO_ARGS_CACHE)));
    }

    public static <T> T createInstance(Class<T> clazz, Object... args) throws ReflectionUtilityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<?> c = findMatchingConstructor(clazz, args);
        return clazz.cast(createInstance(c, args));
    }

    public static Object createInstance(Constructor<?> constructor) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return createInstance(constructor, NO_ARGS_CACHE);
    }

    public static Object createInstance(Constructor<?> constructor, Object[] args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        constructor.setAccessible(true);
        if(args == null || args.length == 0)
            return constructor.newInstance();
        return constructor.newInstance(args);
    }

    public static Constructor<?> findMatchingConstructor(Class<?> clazz, Object[] arguments) throws ReflectionUtilityException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        if(arguments.length == 0) {
            for(Constructor<?> constructor : constructors) {
                if(constructor.getParameterCount() == 0)
                    return constructor;
            }
            throw new ReflectionUtilityException(String.format("Class %s has no default constructor declared.", clazz.getName()));
        }

        Class<?>[] types = new Class<?>[arguments.length];
        for(int i = 0; i < arguments.length; i++)
            types[i] = arguments[i].getClass();

        for(Constructor<?> constructor : constructors) {
            Type[] t = constructor.getGenericParameterTypes();
            Parameter[] p = constructor.getParameters();
            if(isGeneric(p, t))
                continue;
            if(types.length != p.length)
                continue;
            if(typesMatchInOrder(types, p))
                return constructor;
        }

        throw new ReflectionUtilityException(String.format("Class %s has no matching non-generic constructor for the parameters %s",
                clazz.getName(),
                Arrays.stream(arguments).map(Object::getClass).map(Class::getName).collect(Collectors.toList())));
    }

    private static boolean isGeneric(Parameter[] p, Type[] t) {
        for(int i = 0; i < p.length; i++) {
            if(!p[i].getType().getName().equals(t[i].getTypeName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean typesMatchInOrder(Class<?>[] from, Parameter[] to) {
        if(from.length != to.length)
            throw new AssertionError("from.length != t.length");
        for(int i = 0; i < from.length; i++) {
            if(!to[i].getType().isAssignableFrom(from[i]))
                return false;
        }
        return true;
    }

    @SafeVarargs
    public static List<Method> findMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation>...annotations) {
        List<Class<? extends Annotation>> search = Arrays.asList(annotations);
        List<Method> results = new ArrayList<>();
        for(Method m : clazz.getDeclaredMethods()) {
            boolean matches = true;
            Annotation[] methodAnnotations = m.getAnnotations();
            if(methodAnnotations.length == 0)
                continue;
            for(Annotation a : m.getAnnotations()) {
                matches &= search.contains(a.annotationType());
            }
            if(matches)
                results.add(m);
        }
        return results;
    }

    public static List<Method> findMatchingMethods(List<Method> methods, Object[] args) {
        List<Method> matches = new ArrayList<>();
        Class<?>[] types = new Class[args.length];
        for(int i = 0; i < args.length; i++)
            types[i] = args[i].getClass();
        for(Method method : methods) {
            if(typesMatchInOrder(types, method.getParameters()))
                matches.add(method);
        }
        return matches;
    }

    public static Method getMethod(Class<?> clazz, String name) throws NoSuchMethodException, SecurityException {
        return getMethod(clazz, name, NO_ARGS_CLASS_CACHE);
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>...parameters) throws NoSuchMethodException, SecurityException {
        return clazz.getDeclaredMethod(name, parameters);
    }

    public static void invokeMethod(Object instance, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        invokeMethod(instance, method, NO_ARGS_CACHE);
    }

    public static void invokeMethod(Object instance, Method method, Object...parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.setAccessible(true);
        method.invoke(instance, parameters);
    }

}
