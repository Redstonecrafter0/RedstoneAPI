package net.redstonecraft.redstoneapi.webserver.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class SocketChannelInputStream extends InputStream {

    private Integer contentLength = null;
    private final SocketChannel channel;

    public SocketChannelInputStream(SocketChannel channel) {
        this.channel = channel;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public int read() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1);
        if (channel.read(buf) == -1) {
            return -1;
        }
        return buf.array()[0];
    }

    @Override
    public int read(byte[] b) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(b.length);
        int len = channel.read(buf);
        System.arraycopy(buf.array(), 0, b, 0, len);
        return len;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(len);
        int l = channel.read(buf);
        if (l == -1) {
            return -1;
        }
        System.arraycopy(buf.array(), 0, b, off, l);
        return l;
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        if (contentLength == null) {
            return new byte[0];
        }
        ByteBuffer buf = ByteBuffer.allocate(contentLength);
        int l = channel.read(buf);
        if (l == -1) {
            return new byte[0];
        }
        byte[] buffer = new byte[l];
        System.arraycopy(buf.array(), 0, buffer, 0, l);
        return buffer;
    }

}
