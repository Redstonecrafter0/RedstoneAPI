package net.redstonecraft.redstoneapi.webserver.ws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class XORInputStream extends InputStream {

    private final byte[] maskKey;

    public XORInputStream(ByteBuffer buf, byte[] maskKey) {
        super(buf.array());
        this.maskKey = maskKey;
    }

    @Override
    public synchronized int read() {
        return super.read();
    }

    @Override
    public synchronized long transferTo(OutputStream out) throws IOException {
        return super.transferTo(out);
    }

    @Override
    public synchronized byte[] readAllBytes() {
        return super.readAllBytes();
    }

}
