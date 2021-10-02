package net.redstonecraft.redstoneapi.packets;

import net.redstonecraft.redstoneapi.packets.abs.Packet;

import java.io.IOException;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public abstract class Connection {

    public abstract void sendPacket(Packet packet) throws IOException;

}
