package net.redstonecraft.redstoneapi.data.nbt;

import org.apache.commons.lang.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EndTag extends NBTTag {

    public static final byte ID = 0;

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void read(InputStream inputStream) throws IOException {
    }

    @Override
    public String toString() {
        return "END";
    }

    @Override
    public void write(OutputStream outputStream) {
    }

    @Override
    public Object getValue() {
        throw new NotImplementedException("END_TAG does not have an value");
    }

    @Override
    public void setValue(Object value) {
        throw new NotImplementedException("END_TAG does not have an value");
    }

}
