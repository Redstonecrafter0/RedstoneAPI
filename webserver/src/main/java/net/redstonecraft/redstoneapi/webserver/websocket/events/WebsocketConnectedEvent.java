package net.redstonecraft.redstoneapi.webserver.websocket.events;

import net.redstonecraft.redstoneapi.webserver.WebSocketConnection;

/**
 * Executed on connection
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebsocketConnectedEvent {

    private final WebSocketConnection webSocketConnection;

    public WebsocketConnectedEvent(WebSocketConnection webSocketConnection) {
        this.webSocketConnection = webSocketConnection;
    }

    public WebSocketConnection getWebSocketConnection() {
        return webSocketConnection;
    }

    public WebArgument[] getArguments() {
        return webSocketConnection.getWebArguments();
    }
}
