package framework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;
import de.hshannover.dqgui.framework.DependencyConfiguration;
import de.hshannover.dqgui.framework.DependencyConfiguration.ErrorCode;
import de.hshannover.dqgui.framework.DependencyConfiguration.ErrorReport;
import de.hshannover.dqgui.framework.api.Dependencies;
import de.hshannover.dqgui.framework.exceptions.ReflectionUtilityException;

public class DependencyInjectionConfigurationTests {

    @Test
    public void shouldFail_FIELD_EXIST_IN_CONFIG_BUT_NOT_IN_CONTAINER() {
        fail("1", ErrorCode.FIELD_EXIST_IN_CONFIG_BUT_NOT_IN_CONTAINER);
    }

    @Test
    public void shouldFail_FIELD_EXIST_IN_CONTAINER_BUT_NOT_IN_CONFIG()  {
        fail("2", ErrorCode.FIELD_EXIST_IN_CONTAINER_BUT_NOT_IN_CONFIG);
    }

    @Test
    public void shouldFail_CONTAINER_HAS_STATIC_VARIABLE()  {
        fail("3", ErrorCode.CONTAINER_HAS_STATIC_VARIABLE);
    }

    @Test
    public void shouldFail_SHOULD_BE_SERIALIZED_DOES_NOT_EXTEND_RECOVERABLE()  {
        fail("4", ErrorCode.SHOULD_BE_SERIALIZED_DOES_NOT_EXTEND_RECOVERABLE);
    }

    @Test(expected = NullPointerException.class)
    public void shouldFail_SERIALIZATION_ROOT_IS_NULL()  {
        fail("5", ErrorCode.SERIALIZATION_ROOT_IS_NULL);
    }

    @Test
    public void shouldFail_FIELD_IN_CONFIG_BUT_NOT_IN_CONSUMER()  {
        fail("8", ErrorCode.FIELD_IN_CONFIG_BUT_NOT_IN_CONSUMER);
    }

    @Test
    public void shouldFail_FIELD_IN_CONSUMER_IS_STATIC() {
        fail("9", ErrorCode.FIELD_IN_CONSUMER_IS_STATIC);
    }

    @Test
    public void shouldFail_FIELD_REGISTERED_IN_CONSUMER_BUT_NOT_IN_CONTAINER()  {
        fail("10", ErrorCode.FIELD_REGISTERED_IN_CONSUMER_BUT_NOT_IN_CONTAINER);
    }

    @Test
    public void shouldFail_FIELD_REGISTERED_IN_CONSUMER_BUT_NOT_ASSIGNABLE()  {
        fail("11", ErrorCode.FIELD_REGISTERED_IN_CONSUMER_BUT_NOT_ASSIGNABLE);
    }

    @Test
    public void shouldFail_FIELD_EXIST_UNASSIGNABLE_TYPE_IN_CONTAINER() {
        fail("12", ErrorCode.FIELD_EXIST_UNASSIGNABLE_TYPE_IN_CONTAINER);
    }

    private ErrorReport report(String s) {
        DependencyConfiguration c = DependencyConfiguration.of("/dep-yaml/"+s+".yaml");
        System.out.println(c);
        Dependencies d;
        try {
            d = DependencyConfiguration.loadConfiguration(c);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ReflectionUtilityException e) {
            throw new RuntimeException(e);
        }
        return DependencyConfiguration.validate(c, d);
    }

    private void fail(String s, ErrorCode code) {
        ErrorReport e = report(s);
        e.getErrors().forEach(System.out::println);
        if(e.passed())
            System.out.println("!!! PASSED THIS SHOULD NOT HAPPEN EVERYTHING IN HERE NEEDS TO FAIL !!!");
        assertFalse(e.passed());
        assertTrue(e.getErrorCodes().size() == 1);
        assertTrue(e.getErrors().get(0).getCode() == code);
    }
}
