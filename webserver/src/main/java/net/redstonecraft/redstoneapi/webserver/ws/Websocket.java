package net.redstonecraft.redstoneapi.webserver.ws;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Websockets.class)
public @interface Websocket {

    String value();

}
