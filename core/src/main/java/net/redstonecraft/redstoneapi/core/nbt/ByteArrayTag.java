package net.redstonecraft.redstoneapi.core.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ByteArrayTag extends NamedTag<byte[]> {

    public static final byte ID = 7;

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        setValue(new byte[this.readInt(inputStream)]);
        this.readByteArray(inputStream, getValue());
    }

    @Override
    public String toString() {
        return Arrays.toString(getValue());
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        this.writeInt(outputStream, getValue().length);
        this.writeByteArray(outputStream, getValue());
    }
}
