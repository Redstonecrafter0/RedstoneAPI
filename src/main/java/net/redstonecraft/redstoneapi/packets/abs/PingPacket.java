package net.redstonecraft.redstoneapi.packets.abs;

import net.redstonecraft.redstoneapi.packets.Connection;
import net.redstonecraft.redstoneapi.packets.util.PacketInputStream;
import net.redstonecraft.redstoneapi.packets.util.PacketOutputStream;

import java.io.IOException;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public class PingPacket extends PacketIn {

    private long id = 0;

    public PingPacket() {
    }

    public PingPacket(long id) {
        this.id = id;
    }

    @Override
    public int getPacketId() {
        return -1;
    }

    @Override
    public void write(PacketOutputStream outputStream) {
        outputStream.writeLong(id);
    }

    @Override
    public void read(PacketInputStream inputStream) {
        id = inputStream.readLong();
    }

    @Override
    public void handle(Connection serverConnection) {
        try {
            serverConnection.sendPacket(new PongPacket(id));
        } catch (IOException ignored) {
        }
    }

}
