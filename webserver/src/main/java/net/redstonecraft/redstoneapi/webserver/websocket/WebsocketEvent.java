package net.redstonecraft.redstoneapi.webserver.websocket;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(WebsocketEvents.class)
public @interface WebsocketEvent {

    String path();

}
