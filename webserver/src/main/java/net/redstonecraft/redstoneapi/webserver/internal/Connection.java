package net.redstonecraft.redstoneapi.webserver.internal;

import java.nio.channels.SocketChannel;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class Connection {

    private final SocketChannel channel;
    private long keepAlive = System.currentTimeMillis();

    public Connection(SocketChannel channel) {
        this.channel = channel;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public long getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(long keepAlive) {
        this.keepAlive = keepAlive;
    }

}
