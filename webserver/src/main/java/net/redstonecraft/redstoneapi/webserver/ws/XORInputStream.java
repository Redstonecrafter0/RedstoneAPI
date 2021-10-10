package net.redstonecraft.redstoneapi.webserver.ws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class XORInputStream extends ByteArrayInputStream {

    private final byte[] maskKey;

    public XORInputStream(ByteBuffer buf, byte[] maskKey) {
        super(buf.array());
        this.maskKey = maskKey;
    }

    @Override
    public int read() {
        return super.read() ^ maskKey[pos % 4];
    }

    @Override
    public int read(byte[] b, int off, int len) {
        byte[] tmp = new byte[b.length];
        int len1 = super.read(tmp, off, len);
        for (int i = off; i < len; i++) {
            b[i] = (byte) (tmp[i] ^ maskKey[i % 4]);
        }
        return len1;
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        int len = count - pos;
        byte[] tmp = new byte[len];
        read(tmp, pos, len);
        out.write(tmp, pos, len);
        pos = count;
        return len;
    }

    @Override
    public byte[] readAllBytes() {
        int tpos = pos;
        byte[] tmp = super.readAllBytes();
        for (int i = tpos; i < tmp.length; i++) {
            tmp[i] = (byte) (buf[i] ^ maskKey[i % 4]);
        }
        return tmp;
    }

}
