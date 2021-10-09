package net.redstonecraft.redstoneapi.webserver.ws.events;

import net.redstonecraft.redstoneapi.webserver.WebSocketConnection;

/**
 * Executed when binary data is received
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebsocketBinaryDataEvent {

    private final WebSocketConnection webSocketConnection;
    private final byte[] payload;

    public WebsocketBinaryDataEvent(WebSocketConnection webSocketConnection, byte[] payload) {
        this.webSocketConnection = webSocketConnection;
        this.payload = payload;
    }

    public WebSocketConnection getWebSocketConnection() {
        return webSocketConnection;
    }

    public byte[] getPayload() {
        return payload;
    }
}
