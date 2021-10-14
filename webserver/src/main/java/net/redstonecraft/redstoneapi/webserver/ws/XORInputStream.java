package net.redstonecraft.redstoneapi.webserver.ws;

import java.io.IOException;
import java.io.InputStream;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class XORInputStream extends InputStream {

    private final byte[] maskKey;
    private final InputStream inputStream;
    private final int length;
    private int pos = 0;
    private boolean closed = false;

    public XORInputStream(InputStream inputStream, byte[] maskKey, int length) {
        this.inputStream = inputStream;
        this.maskKey = maskKey;
        this.length = length;
    }

    @Override
    public int read() throws IOException {
        pos++;
        return inputStream.read() ^ maskKey[(pos - 1) % 4];
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        len = Math.min(len, length - pos);
        byte[] t = new byte[len];
        inputStream.read(t, off, len);
        for (int i = 0; i < len; i++) {
            pos++;
            b[i] = (byte) (t[i] ^ maskKey[(pos - 1) % 4]);
        }
        return len;
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        byte[] buf = new byte[length - pos];
        read(buf);
        return buf;
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            readAllBytes();
        }
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

}
