package net.redstonecraft.redstoneapi.tools.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DoubleTag extends NamedTag<Double> {

    public static final byte ID = 6;

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        setValue(Double.longBitsToDouble(readLong(inputStream)));
    }

    @Override
    public String toString() {
        return Double.toString(getValue());
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        this.writeLong(outputStream, Double.doubleToLongBits(getValue()));
    }

}
