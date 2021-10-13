package net.redstonecraft.redstoneapi.webserver.internal;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class Connection implements Closeable {

    private final SocketChannel channel;
    private long keepAlive = System.currentTimeMillis();
    private final InputStream inputStream;
    private boolean processed = false;

    public Connection(SocketChannel channel) {
        this.channel = channel;
        inputStream = new SocketChannelInputStream(channel);
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public long getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(long keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void markProcessed() {
        processed = true;
    }

    public boolean isProcessed() {
        return processed;
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException ignored) {
        }
    }

}
