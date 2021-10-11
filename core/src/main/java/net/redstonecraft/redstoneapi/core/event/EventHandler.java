package net.redstonecraft.redstoneapi.core.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to declare a method to receive events.
 * The only parameter has to be a class that extends {@link Event} or itself.
 * When using an {@link Event} that is extended by other classes they will apply to this method.
 * The method must be public, not static and return void.
 *
 * @see #priority() Specify the priority in comparision to other EventHandlers receiving the same event.
 * @author Redstonecrafter0
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /**
     * Defines the priority of the EventHandler compared to other EventHandlers. A higher number is executed first.
     *
     * @return the priority
     */
    int priority() default 0;

}
