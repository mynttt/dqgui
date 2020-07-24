package de.hshannover.dqgui.framework.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import de.hshannover.dqgui.framework.AbstractController;
import de.hshannover.dqgui.framework.WindowInstances;

/**
 * Marks a method in an {@link AbstractController} to be called with a specified varargs parameter if the corresponding view is already loaded but called again.<br>
 * This only works for registered views using the {@link WindowInstances#ONCE_AT_A_TIME} strategy or views managed by an identifier.<p>
 *
 * This does not support void methods!
 *
 * @author Marc Herschel
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface InjectedCallback {}
