package net.redstonecraft.redstoneapi.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Minecraft Server's remote Console (rcon) client
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
public class MCRcon implements Closeable {

    private final Socket socket;
    private int lastRequestId = 0;
    private final Map<Integer, Consumer<String>> callbacks = new HashMap<>();
    private final Timer timer = new Timer();
    private boolean run = true;
    private boolean loggedIn = false;
    private boolean usePassword = false;

    public MCRcon(String host, int port) throws IOException {
        socket = new Socket(host, port);
        new Thread(() -> {
            while (run) {
                try {
                    if (socket.getInputStream().available() > 0) {
                        byte[] length = new byte[4];
                        socket.getInputStream().read(length);
                        byte[] data = new byte[byteArrayToInt(length) + 1];
                        socket.getInputStream().read(data);
                        byte[] raw_id = new byte[4];
                        byte[] raw_type = new byte[4];
                        boolean hasPayload = (data.length - 10) > 0;
                        byte[] raw_payload = new byte[Math.max(0, data.length - 11)];
                        System.arraycopy(data, 0, raw_id, 0, 4);
                        System.arraycopy(data, 4, raw_type, 0, 4);
                        System.arraycopy(data, 8, raw_payload, 0, raw_payload.length);
                        int id = byteArrayToInt(raw_id);
                        int type = byteArrayToInt(raw_type);
                        String payload = hasPayload ? new String(raw_payload, StandardCharsets.ISO_8859_1) : "";
                        try {
                            callbacks.get(id).accept(payload);
                        } catch (NullPointerException ignored) {
                            if (id <= 1 && type == 2) {
                                loggedIn = true;
                            } else if (id == -1) {
                                close();
                                break;
                            }
                        }
                    }
                } catch (IOException ignored) {
                    return;
                }
            }
        }).start();
    }

    public MCRcon(String host) throws IOException {
        this(host, 25575);
    }

    public MCRcon(String host, String password) throws IOException {
        this(host, 25575, password);
    }

    public MCRcon(String host, int port, String password) throws IOException {
        this(host, port);
        this.usePassword = true;
        login(password);
    }

    private void login(String password) throws IOException {
        sendPacket(3, password);
    }

    public void execute(String command, Consumer<String> callback) throws IOException {
        execute(command, 60, callback);
    }

    public void execute(String command, int timeout, Consumer<String> callback) throws IOException {
        int id = sendPacket(2, command);
        callbacks.put(id, callback);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callbacks.remove(id);
            }
        }, timeout * 1000L);
    }

    private int sendPacket(int packetType, String data) throws IOException {
        byte[] rawData = data.getBytes(StandardCharsets.ISO_8859_1);
        byte[] packet = new byte[14 + rawData.length];
        byte[] paddedData = new byte[rawData.length + 1];
        System.arraycopy(rawData, 0, paddedData, 0, rawData.length);
        paddedData[paddedData.length - 1] = 0x00;
        byte[] length = intToByteArray(packet.length - 4);
        int requestId = lastRequestId++;
        byte[] id = intToByteArray(requestId);
        byte[] type = intToByteArray(packetType);
        System.arraycopy(length, 0, packet, 0, length.length);
        System.arraycopy(id, 0, packet, length.length, id.length);
        System.arraycopy(type, 0, packet, length.length + id.length, type.length);
        System.arraycopy(paddedData, 0, packet, length.length + id.length + type.length, paddedData.length);
        packet[packet.length - 1] = 0x00;
        socket.getOutputStream().write(packet);
        socket.getOutputStream().flush();
        return requestId;
    }

    private static int byteArrayToInt(byte[] a) {
        return ByteBuffer.wrap(a).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static byte[] intToByteArray(int a) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(a).array();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        run = false;
    }

    /**
     * @return true if the login process was succesfull. That means if no password was provided this will keep false. Check {@link MCRcon#isUsePassword()} for this.
     * */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isUsePassword() {
        return usePassword;
    }

}
