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
        return inputStream.read() ^ maskKey[pos++ % 4];
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        len = Math.min(len, length - pos);
        inputStream.read(b, off, len);
        return len;
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
