package net.redstonecraft.redstoneapi.packets.abs;

import net.redstonecraft.redstoneapi.packets.util.PacketInputStream;
import net.redstonecraft.redstoneapi.packets.util.PacketOutputStream;
import net.redstonecraft.redstoneapi.tools.event.Event;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public abstract class Packet extends Event {

    public abstract int getPacketId();

    public abstract void write(PacketOutputStream outputStream);

    public abstract void read(PacketInputStream inputStream);

}
