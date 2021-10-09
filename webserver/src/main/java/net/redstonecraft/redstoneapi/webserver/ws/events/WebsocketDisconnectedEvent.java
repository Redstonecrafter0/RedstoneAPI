package net.redstonecraft.redstoneapi.webserver.ws.events;

import net.redstonecraft.redstoneapi.webserver.WebSocketConnection;

/**
 * Executed on disconnect
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebsocketDisconnectedEvent {

    private final WebSocketConnection webSocketConnection;

    public WebsocketDisconnectedEvent(WebSocketConnection webSocketConnection) {
        this.webSocketConnection = webSocketConnection;
    }

    public WebSocketConnection getWebSocketConnection() {
        return webSocketConnection;
    }
}
