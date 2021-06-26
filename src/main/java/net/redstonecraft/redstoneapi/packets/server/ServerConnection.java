package net.redstonecraft.redstoneapi.packets.server;

import net.redstonecraft.redstoneapi.packets.Connection;
import net.redstonecraft.redstoneapi.packets.abs.Packet;
import net.redstonecraft.redstoneapi.packets.abs.PingPacket;
import net.redstonecraft.redstoneapi.packets.util.PacketOutputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public class ServerConnection extends Connection {

    final SocketChannel channel;
    private final PacketServer packetServer;
    long lastPing = System.currentTimeMillis();
    long payload;

    ServerConnection(PacketServer packetServer, SocketChannel channel, long payload) {
        this.packetServer = packetServer;
        this.channel = channel;
        this.payload = payload;
    }

    @Override
    public void sendPacket(Packet packet) throws IOException {
        PacketOutputStream os = new PacketOutputStream();
        packet.write(os);
        ByteBuffer buf = ByteBuffer.allocate(8 + os.size());
        buf.putInt(packet.getPacketId());
        buf.putInt(os.size());
        buf.put(os.toByteArray());
        channel.write(buf);
    }

    void ping(long id) throws IOException {
        sendPacket(new PingPacket(id));
    }

    public PacketServer getServer() {
        return packetServer;
    }

    public void updatePing() {
        lastPing = System.currentTimeMillis();
    }

}
