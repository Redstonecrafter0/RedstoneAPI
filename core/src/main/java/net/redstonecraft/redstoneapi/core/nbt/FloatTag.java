package net.redstonecraft.redstoneapi.core.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FloatTag extends NamedTag<Float> {

    public static final byte ID = 5;

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        setValue(Float.intBitsToFloat(readInt(inputStream)));
    }

    @Override
    public String toString() {
        return Float.toString(getValue());
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        this.writeInt(outputStream, Float.floatToIntBits(getValue()));
    }

}
