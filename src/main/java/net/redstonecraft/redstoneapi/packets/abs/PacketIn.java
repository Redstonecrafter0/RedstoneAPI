package net.redstonecraft.redstoneapi.packets.abs;

import net.redstonecraft.redstoneapi.packets.Connection;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public abstract class PacketIn extends Packet {

    public abstract void handle(Connection connection);

}
