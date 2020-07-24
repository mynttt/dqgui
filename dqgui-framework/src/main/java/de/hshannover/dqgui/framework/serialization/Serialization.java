package de.hshannover.dqgui.framework.serialization;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import org.tinylog.Logger;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.ReflectionUtility;
import de.hshannover.dqgui.framework.api.Recoverable;
import de.hshannover.dqgui.framework.exceptions.ReflectionUtilityException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Serialization solution using GSON.
 *
 * @author Marc Herschel
 *
 */
public final class Serialization {
    private static HashMap<Class<? extends Recoverable>, Path> CONFIGS = new HashMap<>();

    private Serialization() {}

    /**
     * Register a class for serialization.<br>
     * A class must have a default constructor or the recovery will fail.
     * @param path to register for
     * @param clazz to register for (must implement Recoverable)
     * @throws IllegalArgumentException if path or class is null
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public static void register(Path path, Class<? extends Recoverable> clazz) {
        if(clazz == null)
            throw new IllegalArgumentException("Class must no be null.");
        if(path == null)
            throw new IllegalArgumentException("Path must no be null.");
        if(!Files.exists(path.getParent())) {
            try {
                Logger.info("Created folder for config {} at {}.", path.getFileName(), path.getParent().toAbsolutePath());
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                Logger.error("Failed to create config folder for {}.", path.getParent().toAbsolutePath());
                throw ErrorUtility.rethrow(e);
            }
        }
        if(CONFIGS.values().contains(path))
            throw new IllegalArgumentException("Path already registered.");
        Logger.info("Registered {} for serialization/recovery @ {}", clazz.getSimpleName(), path.toAbsolutePath());
        CONFIGS.put(clazz, path);
    }

    /**
     * Remove a registered class.
     * @param clazz to remove registration for
     * @throws IllegalArgumentException if class is null
     */
    public static void unregister(Class<? extends Recoverable> clazz) {
        if(clazz == null)
            throw new IllegalArgumentException("Class must no be null.");
        Logger.info("Unregistered {} for serialization/recovery", clazz.getSimpleName());
        CONFIGS.remove(clazz);
    }

    /**
     * Recover a serialized object.<br>
     * The object has to be registered with the recovery.<br>
     * If recovery fails a default instance is returned.
     * @param <E> type of recovered class
     * @param clazz of the recoverable object
     * @throws IllegalArgumentException if class null or unregistered
     * @throws InvalidClassForRecoveryException if class does not have the required format
     * @return instance recovered from serialization.
     */
    public static <E extends Recoverable> E recover(Class<E> clazz) {
        Path p = CONFIGS.get(clazz);     
        if(p == null)
            throw new IllegalArgumentException(String.format("Class: %s is not registered.", clazz.getSimpleName()));
        return recoverInternal(p, clazz);
    }
    
    /**
     * Recover a serialized object without registering first.<br>
     * If recovery fails a default instance is returned.
     * @param <E> type of recovered class
     * @param p path of serialized json
     * @param clazz of the recoverable object
     * @throws IllegalArgumentException if class null or unregistered
     * @throws InvalidClassForRecoveryException if class does not have the required format
     * @return instance recovered from serialization.
     */
    public static <E> E recoverUnregistered(Path p, Class<E> clazz) {
        return recoverInternal(p, clazz);
    }
    
    private static <E> E recoverInternal(Path p, Class<E> clazz) {
        E result = null;
        Objects.requireNonNull(p, "Invalid path: null");
        if(clazz == null)
            throw new IllegalArgumentException("Class must no be null.");

        if(Files.exists(p)) {
            try {
                result = clazz.cast(JSONOperations.fromJSON(clazz, p));
            } catch(Exception e) {
                Logger.error(String.format("Failed to load %s from %s: %s", p.getFileName(), p.toString(), e.getMessage()));
                Logger.error(e);
                try {
                    ReflectionUtility.createInstance(clazz);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ReflectionUtilityException e1) {
                    Logger.error("Failed to instantiate for class: {}", clazz.getName());
                    throw new InvalidClassForRecoveryException(e1);
                }
            }
        } else {
            Logger.info(String.format("Config not found: %s. Initializing with default value.", p.getFileName()));
            try {
                result = ReflectionUtility.createInstance(clazz);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ReflectionUtilityException e) {
                Logger.error("Failed to instantiate for class: {}", clazz.getName());
                throw new InvalidClassForRecoveryException(e);
            }
        }
        
        if(result instanceof Recoverable)
            ((Recoverable) result).recoverHook();

        return result;
    }

    /**
     * Dump a registered instance to the file system.
     * @param r to dump
     * @return true if success
     */
    public static boolean dump(Recoverable r) {
        Path p = CONFIGS.get(r.getClass());
        if(p == null)
            throw new IllegalArgumentException(String.format("Class: %s is not registered.", r.getClass().getSimpleName()));
        return dumpInternal(p, r);
    }
    
    /**
     * Dump a unregistered instance to the file system.
     * @param p where to dump
     * @param r to dump
     * @return true if success
     */
    public static boolean dumpUnregistered(Path p, Object r) {
        return dumpInternal(p, r);
    }
    
    private static boolean dumpInternal(Path p, Object r) {
        Objects.requireNonNull(p, "Invalid path: null");
        if(r == null)
            throw new IllegalArgumentException("Can't serialize null.");
        try {
            JSONOperations.toJSON(r, p);
        } catch(IOException e) {
            Logger.error(String.format("Failed to dump %s state: %s", r.getClass().getSimpleName(), p.toString()));
            throw ErrorUtility.rethrow(e);
        }
        return true;
    }
}
