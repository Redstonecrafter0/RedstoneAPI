package net.redstonecraft.redstoneapi.packets.abs;

import net.redstonecraft.redstoneapi.packets.Connection;
import net.redstonecraft.redstoneapi.packets.server.ServerConnection;
import net.redstonecraft.redstoneapi.packets.util.PacketInputStream;
import net.redstonecraft.redstoneapi.packets.util.PacketOutputStream;

/**
 * @author Redstonecrafter0
 */
public class PongPacket extends PacketIn {

    private long id = 0;

    public PongPacket() {
    }

    public PongPacket(long id) {
        this.id = id;
    }

    @Override
    public int getPacketId() {
        return -2;
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
        ((ServerConnection) serverConnection).updatePing();
    }

}
