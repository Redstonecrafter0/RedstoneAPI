package net.redstonecraft.redstoneapi.data.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ShortTag extends NamedTag<Short> {

    public static final byte ID = 2;

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        setValue(readShort(inputStream));
    }

    @Override
    public String toString() {
        return Short.toString(getValue());
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        this.writeShort(outputStream, getValue());
    }

}
