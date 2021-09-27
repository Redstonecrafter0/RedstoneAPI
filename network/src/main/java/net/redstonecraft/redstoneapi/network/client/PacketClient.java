package net.redstonecraft.redstoneapi.network.client;

import net.redstonecraft.redstoneapi.network.Connection;
import net.redstonecraft.redstoneapi.network.abs.Packet;
import net.redstonecraft.redstoneapi.network.abs.PacketIn;
import net.redstonecraft.redstoneapi.network.util.PacketInputStream;
import net.redstonecraft.redstoneapi.network.util.PacketOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public class PacketClient extends Connection {

    private final Socket socket;
    private final Map<Integer, Class<? extends PacketIn>> packetMap = new HashMap<>();
    private boolean run = true;

    public PacketClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        new Thread(() -> {
            while (run) {
                try {
                    byte[] idBuf = new byte[4];
                    socket.getInputStream().read(idBuf);
                    int packetId = ByteBuffer.wrap(idBuf).getInt();
                    byte[] len = new byte[4];
                    socket.getInputStream().read(len);
                    byte[] buf = new byte[ByteBuffer.wrap(len).getInt()];
                    socket.getInputStream().read(buf);
                    PacketIn packet = packetMap.get(packetId).newInstance();
                    packet.read(new PacketInputStream(buf));
                    packet.handle(this);
                } catch (IOException ignored) {
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public <T extends PacketIn> void registerPacketIn(Class<T> packet, Consumer<T> callback) {
        try {
            packetMap.put(packet.newInstance().getPacketId(), packet);
        } catch (InstantiationException | IllegalAccessException ignored) {
            throw new IllegalArgumentException("The packet needs a public constructor without parameters");
        }
    }

    public void sendPacket(Packet packet) throws IOException {
        PacketOutputStream os = new PacketOutputStream();
        packet.write(os);
        ByteBuffer buf = ByteBuffer.allocate(8 + os.size());
        buf.putInt(packet.getPacketId());
        buf.putInt(os.size());
        buf.put(os.toByteArray());
        socket.getOutputStream().write(buf.array());
    }

    public void close() throws IOException {
        socket.close();
    }

}
