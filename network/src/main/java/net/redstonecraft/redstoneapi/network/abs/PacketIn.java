package net.redstonecraft.redstoneapi.network.abs;

import net.redstonecraft.redstoneapi.network.Connection;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public abstract class PacketIn extends Packet {

    public abstract void handle(Connection connection);

}
