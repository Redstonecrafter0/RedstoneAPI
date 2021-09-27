package net.redstonecraft.redstoneapi.webserver.websocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebsocketEvents {

    WebsocketEvent[] value();

}
