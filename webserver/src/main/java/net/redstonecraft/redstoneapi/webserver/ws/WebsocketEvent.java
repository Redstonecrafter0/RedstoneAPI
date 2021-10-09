package net.redstonecraft.redstoneapi.webserver.ws;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(WebsocketEvents.class)
public @interface WebsocketEvent {

    String path();

}
