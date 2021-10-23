package net.redstonecraft.redstoneapi.data.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IntTag extends NamedTag<Integer> {

    public static final byte ID = 3;

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        setValue(readInt(inputStream));
    }

    @Override
    public String toString() {
        return Integer.toString(getValue());
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        this.writeInt(outputStream, getValue());
    }

}
