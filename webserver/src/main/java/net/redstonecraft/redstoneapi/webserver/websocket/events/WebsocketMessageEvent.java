package net.redstonecraft.redstoneapi.webserver.websocket.events;

import net.redstonecraft.redstoneapi.webserver.WebSocketConnection;

/**
 * Executed when a message is received
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebsocketMessageEvent {

    private final WebSocketConnection webSocketConnection;
    private final String message;

    public WebsocketMessageEvent(WebSocketConnection webSocketConnection, String message) {
        this.webSocketConnection = webSocketConnection;
        this.message = message;
    }

    public WebSocketConnection getWebSocketConnection() {
        return webSocketConnection;
    }

    public String getMessage() {
        return message;
    }
}
