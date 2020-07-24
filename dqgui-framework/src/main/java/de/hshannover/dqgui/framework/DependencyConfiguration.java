package de.hshannover.dqgui.framework;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.tinylog.Logger;
import org.yaml.snakeyaml.Yaml;
import de.hshannover.dqgui.framework.api.Dependencies;
import de.hshannover.dqgui.framework.api.Recoverable;
import de.hshannover.dqgui.framework.exceptions.ReflectionUtilityException;
import de.hshannover.dqgui.framework.serialization.Serialization;

/**
 * This class represents a converted and type safe loaded dependency injection configuration file.<br>
 * Such a configuration file will not be verified by default as the verification is an expensive operation relying heavily on reflection.<p>
 *
 * It is recommended to validate the configuration at least once after changing it to ensure no nasty bugs at runtime.<p>
 *
 * A sample documentation file can be found within the documentation.
 *
 * @author Marc Herschel
 *
 */
public final class DependencyConfiguration {

    public enum ErrorCode {
        FIELD_EXIST_UNASSIGNABLE_TYPE_IN_CONTAINER,
        FIELD_EXIST_IN_CONFIG_BUT_NOT_IN_CONTAINER,
        FIELD_EXIST_IN_CONTAINER_BUT_NOT_IN_CONFIG,
        CONTAINER_HAS_STATIC_VARIABLE,
        SHOULD_BE_SERIALIZED_DOES_NOT_EXTEND_RECOVERABLE,
        SERIALIZATION_ROOT_IS_NULL,
        FIELD_IN_CONFIG_BUT_NOT_IN_CONSUMER,
        FIELD_IN_CONSUMER_IS_STATIC,
        FIELD_REGISTERED_IN_CONSUMER_BUT_NOT_IN_CONTAINER,
        FIELD_REGISTERED_IN_CONSUMER_BUT_NOT_ASSIGNABLE,
    }

    public static final class ConfigurationError {
        private final ErrorCode code;
        private final String message;

        ConfigurationError(ErrorCode code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorCode getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "ConfigurationError [code=" + code + ": message=" + message + "]";
        }

    }

    public static final class ErrorReport {
        private final List<ConfigurationError> errors = new ArrayList<>();
        private final Set<ErrorCode> errorCodes = new HashSet<>();

        ErrorReport() { }

        void addIssue(ErrorCode error, String message) {
            errors.add(new ConfigurationError(error, message));
            errorCodes.add(error);
        }

        public boolean passed() {
            return errors.isEmpty();
        }

        public List<ConfigurationError> getErrors() {
            return errors;
        }

        public Set<ErrorCode> getErrorCodes() {
            return errorCodes;
        }

    }

    static final class Dependency {
        final String field, serialize;
        final Class<?> target;

        Dependency(String field, String target, String serialize) throws ClassNotFoundException {
            this.field = field;
            this.target = Class.forName(target);
            this.serialize = serialize;
        }
    }

    /**
     * Load a dependency configuration for a given path.
     * @param config path to load from
     * @return a for java converted usable configuration
     * @throws RuntimeException in case the parsing fails
     */
    public static DependencyConfiguration of(String config) {
        try {
            DependencyConfiguration d = new DependencyConfiguration(config);
            Logger.info("Configuration created {} | {} dependencies | {} consumers | {} receivers @ {}", config, d.dependencies.size(), d.consumers.size(), d.receivers.size(), d.container.getSimpleName());
            if(!d.shouldTest)
                Logger.warn("Validation is disabled for dependency config. If the config has been changed and is not validated it might cause nasty runtime exceptions.");
            return d;
        } catch(Exception e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    /**
     * Creates a {@link Dependencies Dependencies} from a given configuration file.<br>
     * This will also call {@link Dependencies#loadEager()}.
     * @param conf to load from
     * @return a container ready to be used by the injectors
     * @throws NullPointerException if configuration is null or {@link Dependencies#serializationRoot()} returns null.
     * @throws InstantiationException in case the container has no default constructor
     * @throws IllegalAccessException should really not happen
     * @throws ReflectionUtilityException if reflection fails
     * @throws InvocationTargetException if reflection fails
     * @throws IllegalArgumentException if reflection fails
     */
    @SuppressWarnings("unchecked")
    public static Dependencies loadConfiguration(DependencyConfiguration conf) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ReflectionUtilityException {
        if(conf == null)
            throw new NullPointerException("configuration is null");
        Dependencies c = ReflectionUtility.createInstance(conf.container);
        for(Dependency d : conf.dependencies.values()) {
            if(d.serialize == null)
                continue;
            if(c.serializationRoot() == null)
                throw new NullPointerException(String.format("serializationRoot() of %s returned null", c.getClass().getName()));
            Path p = Paths.get(c.serializationRoot(), d.serialize);
            Serialization.register(p, (Class<? extends Recoverable>) d.target);
        }
        c.loadEager();
        return c;
    }

    final Class<? extends Dependencies> container;
    final boolean shouldTest;
    final Map<Class<? extends AbstractController>, List<String>> consumers = new HashMap<>();
    final Map<Class<? extends AbstractController>, List<String>> receivers = new HashMap<>();
    final Map<String, Dependency> dependencies = new HashMap<>();

    @SuppressWarnings("unchecked")
    private DependencyConfiguration(String config) throws ClassNotFoundException {
        Map<String, Object> o = null;

        try(InputStream in = getClass().getResourceAsStream(config)) {
            Yaml ymal = new Yaml();
            o = ymal.load(in);
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }

        Object st = o.get("validate");
        shouldTest = st != null && (boolean) st;
        container = (Class<? extends Dependencies>) Class.forName((String) o.get("container"));

        List<Map<String, String>> yamlDependencies = (List<Map<String, String>>) o.get("dependencies");
        if(yamlDependencies != null) {
            for(Map<String, String> m : yamlDependencies) {
                String field = m.get("field");
                dependencies.put(field, new Dependency(field, (String) m.get("target"), (String) m.get("serialize")));
            }
        }

        List<Map<String, String>> yamlConsumerRoots = (List<Map<String, String>>) o.get("consumer-roots");
        Map<String, String> consumerLookupTable = new HashMap<>();
        if(yamlConsumerRoots != null) {
            for(Map<String, String> m : yamlConsumerRoots) {
                String key = m.get("id");
                String value = m.get("root");
                if(consumerLookupTable.containsKey(key))
                    throw new RuntimeException("Duplicate id for consumer root: " + key);
                if(value == null)
                    throw new RuntimeException("No root attribute defined for id: " + key);
                consumerLookupTable.put(key, value);
            }
        }

        Map<String, List<Map<String, List<String>>>> yamlConsumers = (Map<String, List<Map<String, List<String>>>>) o.get("consumers");
        if(yamlConsumers != null) {
            for(Map.Entry<String, List<Map<String, List<String>>>> e : yamlConsumers.entrySet()) {
                String consumerRoot = consumerLookupTable.get(e.getKey());
                if(consumerRoot == null)
                    throw new RuntimeException("No consumer root defined for id: " + e.getKey() + " in consumer configuration.");
                for(Map<String, List<String>> m : e.getValue()) {
                    for(Map.Entry<String, List<String>> ee : m.entrySet()) {
                        consumers.put((Class<? extends AbstractController>) Class.forName(consumerRoot+"."+ee.getKey()), ee.getValue());
                    }
                }
            }
        }
    }

    /**
     * Validate a configuration and a given container.<br>
     * This is an expensive operation, only do this when unsure if your configuration is done correctly.
     * @param c configuration to validate
     * @param d container from that configuration
     * @return an extensive error report
     */
    public static ErrorReport validate(DependencyConfiguration c, Dependencies d) {
        ErrorReport report = new ErrorReport();
        Map<String, Class<?>> dependencies = new HashMap<>();
        List<String> configFields = new ArrayList<>();

        validateDependencies(c, d, dependencies, configFields, report);
        validateSerialization(c, d, report);

        for(Map.Entry<String, Dependency> s : c.dependencies.entrySet()) {
            if(!configFields.contains(s.getKey())) {
                String msg = String.format("Field %s %s is registered in the config for injection but does not exist in %s.",
                        s.getValue().target.getSimpleName(),
                        s.getKey(),
                        d.getClass().getSimpleName());
                report.addIssue(ErrorCode.FIELD_EXIST_IN_CONFIG_BUT_NOT_IN_CONTAINER, msg);
            }
        }

        validateFields(c, d, dependencies, report);

        return report;
    }

    private static void validateDependencies(DependencyConfiguration c, Dependencies d, Map<String, Class<?>> dependencies, List<String> configFields, ErrorReport report) {
        for(Field f : d.getClass().getDeclaredFields()) {
            if(!Modifier.isStatic(f.getModifiers())) {
                configFields.add(f.getName());
                if(!c.dependencies.keySet().contains(f.getName())) {
                    String msg = String.format("Field %s %s is in container %s but not configured as dependency in your configuration file.",
                            f.getType().getSimpleName(),
                            f.getName(),
                            d.getClass().getSimpleName());
                    report.addIssue(ErrorCode.FIELD_EXIST_IN_CONTAINER_BUT_NOT_IN_CONFIG, msg);
                    continue;
                }
                if(!c.dependencies.get(f.getName()).target.isAssignableFrom(f.getType())) {
                    String msg = String.format("Field %s %s in %s is configured as %s which is not of the same type.",
                            f.getType().getSimpleName(),
                            f.getName(),
                            d.getClass().getSimpleName(),
                            c.dependencies.get(f.getName()).target.getSimpleName());
                    report.addIssue(ErrorCode.FIELD_EXIST_UNASSIGNABLE_TYPE_IN_CONTAINER, msg);
                    continue;
                }
                dependencies.put(f.getName(), f.getType());
            } else {
                String msg = String.format("%s has a static field! Please mark %s %s as a member.",
                        d.getClass().getSimpleName(),
                        f.getType().getSimpleName(),
                        f.getName());
                report.addIssue(ErrorCode.CONTAINER_HAS_STATIC_VARIABLE, msg);
            }
        }
    }

    private static void validateSerialization(DependencyConfiguration c, Dependencies d, ErrorReport report) {
        for(Map.Entry<String, Dependency> dd : c.dependencies.entrySet()) {
            if(dd.getValue().serialize!=null && !Recoverable.class.isAssignableFrom(dd.getValue().target)) {
                String msg = String.format("Dependency %s %s should be serialized but it does not implement Recoverable.", dd.getValue().target.getSimpleName(), dd.getKey());
                report.addIssue(ErrorCode.SHOULD_BE_SERIALIZED_DOES_NOT_EXTEND_RECOVERABLE, msg);
            }
        }
        boolean serialize = false;
        for(Dependency dp : c.dependencies.values())
            serialize |= !(dp.serialize == null);
        if(serialize && d.serializationRoot() == null) {
            String msg = String.format("The method serializationRoot() in %s returns null. Have you overridden it properly?", d.getClass().getSimpleName());
            report.addIssue(ErrorCode.SERIALIZATION_ROOT_IS_NULL, msg);
        }
    }

    private static void validateFields(DependencyConfiguration c, Dependencies d, Map<String, Class<?>> dependencies, ErrorReport report) {
        for(Map.Entry<Class<? extends AbstractController>, List<String>> entry : c.consumers.entrySet()) {
            Map<String, Class<?>> fieldsAndTypes = new HashMap<>();
            List<String> statics = new ArrayList<>();
            for(Field f : entry.getKey().getDeclaredFields()) {
                if(!entry.getValue().contains(f.getName()))
                    continue;
                if(Modifier.isStatic(f.getModifiers())) {
                    String msg = String.format("Injectable field %s %s in %s is static. Mark it as a member.",
                            f.getType().getSimpleName(),
                            f.getName(),
                            entry.getKey().getSimpleName());
                    report.addIssue(ErrorCode.FIELD_IN_CONSUMER_IS_STATIC, msg);
                    statics.add(f.getName());
                } else {
                    fieldsAndTypes.put(f.getName(), f.getType());
                }
            }
            for(String f : entry.getValue()) {
                if(statics.contains(f))
                    continue;
                if(!dependencies.containsKey(f)) {
                    String msg = String.format("Field %s is registered to be injected into %s but is missing from the container %s.",
                            f,
                            entry.getKey().getSimpleName(),
                            d.getClass().getSimpleName());
                    report.addIssue(ErrorCode.FIELD_REGISTERED_IN_CONSUMER_BUT_NOT_IN_CONTAINER, msg);
                    continue;
                }
                if(!fieldsAndTypes.containsKey(f)) {
                    String type = c.dependencies.values().stream().filter(p -> f.equals(p.field)).collect(Collectors.toList()).get(0).target.getSimpleName();
                    String msg = String.format("%s is configured to have field %s %s injected via the configuration file but is missing from %s.",
                            entry.getKey().getSimpleName(),
                            type,
                            f,
                            entry.getKey().getSimpleName());
                    report.addIssue(ErrorCode.FIELD_IN_CONFIG_BUT_NOT_IN_CONSUMER, msg);
                    continue;
                }
                if(!fieldsAndTypes.get(f).isAssignableFrom(dependencies.get(f))) {
                    String msg = String.format("Field %s from %s with type %s is not assignable from %s in container %s. Please make sure the types are valid.",
                            f,
                            entry.getKey().getSimpleName(),
                            fieldsAndTypes.get(f).getSimpleName(),
                            dependencies.get(f).getSimpleName(),
                            d.getClass().getSimpleName());
                    report.addIssue(ErrorCode.FIELD_REGISTERED_IN_CONSUMER_BUT_NOT_ASSIGNABLE, msg);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "DependencyConfig [container=" + container + ", shouldTest=" + shouldTest + ", consumers=" + consumers
                + ", receivers=" + receivers + ", dependencies=" + dependencies + "]";
    }
}
