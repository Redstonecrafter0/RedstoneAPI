package net.redstonecraft.redstoneapi.webserver.ws.events;

import net.redstonecraft.redstoneapi.webserver.WebSocketConnection;

import java.io.InputStream;

/**
 * Executed when binary data is received
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebsocketBinaryDataEvent {

    private final WebSocketConnection webSocketConnection;
    private final InputStream payload;

    public WebsocketBinaryDataEvent(WebSocketConnection webSocketConnection, InputStream payload) {
        this.webSocketConnection = webSocketConnection;
        this.payload = payload;
    }

    public WebSocketConnection getWebSocketConnection() {
        return webSocketConnection;
    }

    public InputStream getPayload() {
        return payload;
    }
}
