package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.webserver.internal.WebsocketManager;
import net.redstonecraft.redstoneapi.webserver.ws.XORInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * The WebsocketConnection is used to send data or disconnect
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebSocketConnection {

    private final SocketChannel channel;
    private final WebServer webServer;
    private final WebRequest request;
    private String room = null;

    public WebSocketConnection(SocketChannel channel, WebServer webServer, WebRequest request) {
        this.channel = channel;
        this.webServer = webServer;
        this.request = request;
    }

    void send(byte type, byte[] payload) throws IOException {
        int length;
        byte[] payloadLength;
        if (payload.length <= 125) {
            length = payload.length + 2;
            payloadLength = new byte[1];
            byte pl = (byte) payload.length;
            pl <<= 1;
            pl >>= 1;
            payloadLength[0] = pl;
        } else if (payload.length <= 65536) {
            length = payload.length + 4;
            payloadLength = new byte[]{(byte) 126, (byte) ((payload.length >> 8) & 0xff), (byte) (payload.length & 0xff)};
        } else {
            throw new IllegalArgumentException("Payload to large");
        }
        byte[] data = new byte[length];
        data[0] = (byte) (type | (byte) 0b10000000);
        System.arraycopy(payloadLength, 0, data, 1, payloadLength.length);
        System.arraycopy(payload, 0, data, 1 + payloadLength.length, payload.length);
        channel.write(ByteBuffer.wrap(data));
    }

    public void handle(WebsocketManager websocketManager, long maxLength, ExecutorService threadPool) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        if (channel.read(buffer) != 2) {
            throw new EOFException();
        }
        byte[] arr = buffer.array();
        byte actionRaw = arr[0];
        boolean fin = getBitByByte(actionRaw, 0);
        actionRaw <<= 4;
        actionRaw >>= 4;
        int action = actionRaw & 0xf;
        byte rawLength = arr[1];
        boolean mask = getBitByByte(rawLength, 0);
        rawLength <<= 1;
        rawLength >>= 1;
        int orgLength = Byte.toUnsignedInt(rawLength);
        int length;
        if (!fin || !mask) {
            disconnect();
            return;
        }
        if (orgLength <= 125) {
            length = orgLength;
        } else if (orgLength == 126) {
            buffer = ByteBuffer.allocate(2);
            if (channel.read(buffer) != 2) {
                throw new EOFException();
            }
            length = getIntByBytes(buffer.array());
        } else {
            disconnect();
            return;
        }
        if (length > maxLength) {
            throw new IOException("Message longer than allowed.");
        }
        buffer = ByteBuffer.allocate(4);
        if (channel.read(buffer) != 4) {
            throw new EOFException();
        }
        byte[] maskKey = buffer.array();
        buffer = ByteBuffer.allocate(length);
        channel.read(buffer);
        XORInputStream is = new XORInputStream(buffer, maskKey);
        threadPool.submit(() -> {
            try {
                switch (action) {
                    case 0xA -> {
                        if (new String(is.readAllBytes(), StandardCharsets.UTF_8).equals(String.valueOf(websocketManager.getPing(this).getPayload()))) {
                            websocketManager.getPing(this).setTime(System.currentTimeMillis());
                        }
                    }
                    case 0x1 -> {
                        websocketManager.getPing(this).setTime(System.currentTimeMillis());
                        websocketManager.executeMessageEvent(this, new String(is.readAllBytes(), StandardCharsets.UTF_8));
                    }
                    case 0x2 -> {
                        websocketManager.getPing(this).setTime(System.currentTimeMillis());
                        websocketManager.executeBinaryEvent(this, is);
                    }
                    case 0x9 -> this.send(is.readAllBytes());
                    default -> this.disconnect();
                }
            } catch (Throwable ignored) {
                this.disconnect();
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean getBitByByte(byte b, int pos) {
        return (b >> (8 - (pos + 1)) & 0x0001) == 1;
    }

    private static int getIntByBytes(byte[] arr) {
        int c = 0;
        for (byte i : arr) {
            c |= Byte.toUnsignedInt(i);
            c <<= 8;
        }
        return c;
    }

    public void send(byte[] payload) throws IOException {
        send((byte) 0b10000010, payload);
    }

    public void send(String message) throws IOException {
        send((byte) 0b10000001, message.getBytes(StandardCharsets.UTF_8));
    }

    public void send(JSONArray array) throws IOException {
        send(array.toJSONString());
    }

    public void send(JSONObject object) throws IOException {
        send(object.toJSONString());
    }

    public void ping(byte[] payload) throws IOException {
        send((byte) 0b10001001, payload);
    }

    public void pong(byte[] payload) throws IOException {
        send((byte) 0b10001010, payload);
    }

    public void disconnect() {
        webServer.disconnectWebsocket(this);
    }

    public WebRequest getRequest() {
        return request;
    }

    public WebServer getWebServer() {
        return webServer;
    }

    public String getRoom() {
        return room;
    }

    public void joinRoom(String room) {
        this.room = room;
    }

    public void broadcast(String message) {
        webServer.broadcastWebsocket(request.getPath(), message);
    }

    public void broadcast(byte[] payload) {
        webServer.broadcastWebsocket(request.getPath(), payload);
    }

    public void broadcastRoom(String message) {
        webServer.broadcastWebsocket(request.getPath(), room, message);
    }

    public void broadcastRoom(byte[] payload) {
        webServer.broadcastWebsocket(request.getPath(), room, payload);
    }

    public void leaveRoom() {
        room = null;
    }

    public SocketChannel getChannel() {
        return channel;
    }

}
