package net.redstonecraft.redstoneapi.webserver.internal;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class RangeFileInputStream extends InputStream {

    private final FileInputStream fileInputStream;
    private int remaining;

    public RangeFileInputStream(FileInputStream fileInputStream, int from, int length) throws IOException {
        this.fileInputStream = fileInputStream;
        this.fileInputStream.skip(from);
        this.remaining = length;
    }

    @Override
    public int read() throws IOException {
        if (remaining > 0) {
            remaining--;
            return fileInputStream.read();
        } else {
            throw new EOFException();
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return fileInputStream.read(b, off, Math.min(len, remaining));
    }

}
