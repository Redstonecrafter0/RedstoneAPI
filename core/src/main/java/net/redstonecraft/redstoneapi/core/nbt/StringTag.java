package net.redstonecraft.redstoneapi.core.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StringTag extends NamedTag<String> {

    public static final byte ID = 8;

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        setValue(readString(inputStream));
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        this.writeString(outputStream, getValue());
    }

}
