package net.redstonecraft.redstoneapi.webserver.ws.events;

import net.redstonecraft.redstoneapi.webserver.WebRequest;
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

    public WebRequest getArguments() {
        return webSocketConnection.getRequest();
    }

}
