package net.redstonecraft.redstoneapi.network.abs;

import net.redstonecraft.redstoneapi.network.util.PacketInputStream;
import net.redstonecraft.redstoneapi.network.util.PacketOutputStream;
import net.redstonecraft.redstoneapi.core.event.Event;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public abstract class Packet extends Event {

    public abstract int getPacketId();

    public abstract void write(PacketOutputStream outputStream);

    public abstract void read(PacketInputStream inputStream);

}
