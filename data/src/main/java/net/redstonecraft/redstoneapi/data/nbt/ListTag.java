package net.redstonecraft.redstoneapi.data.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ListTag extends NamedTag<NamedTag[]> {

    public static final byte ID = 9;
    private byte id;

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        this.id = this.readByte(inputStream);
        setValue(new NamedTag[readInt(inputStream)]);
        for (int i = 0; i < getValue().length; i++) {
            NBTTag tag = NBTTag.readTag(inputStream, this.id, false);
            if (!(tag instanceof NamedTag)) {
                throw new IOException("END_TAG not allowed here");
            }
            ((NamedTag)tag).read(inputStream, false);
            getValue()[i] = (NamedTag) tag;
        }
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public String toString() {
        return Arrays.toString(getValue());
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        this.writeByte(outputStream, this.id);
        this.writeInt(outputStream, getValue().length);
        for (NamedTag namedTag : getValue()) {
            NBTTag.writeTag(outputStream, namedTag, false);
            namedTag.write(outputStream, false);
        }
    }

}
