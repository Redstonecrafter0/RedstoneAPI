package net.redstonecraft.redstoneapi.webserver.annotations;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(value = Routes.class)
public @interface Route {
    String value();
}
