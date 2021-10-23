package net.redstonecraft.redstoneapi.data.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LongTag extends NamedTag<Long> {

    public static final byte ID = 4;

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        setValue(readLong(inputStream));
    }

    @Override
    public String toString() {
        return Long.toString(getValue());
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        this.writeLong(outputStream, getValue());
    }

}
