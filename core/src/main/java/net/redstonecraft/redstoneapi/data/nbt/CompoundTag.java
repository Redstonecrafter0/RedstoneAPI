package net.redstonecraft.redstoneapi.data.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class CompoundTag extends NamedTag<Map<String, NamedTag>> {

    public static final byte ID = 10;

    CompoundTag() {
        setValue(new HashMap<>());
    }

    @Override
    public void read(InputStream inputStream, boolean readName) throws IOException {
        super.read(inputStream, readName);
        NBTTag tag;
        while (!((tag = NBTTag.readTag(inputStream)) instanceof EndTag)) {
            put((NamedTag)tag);
        }
    }

    public NamedTag get(String name) {
        return getValue().get(name);
    }

    private void put(NamedTag tag) {
        if (tag != null) {
            getValue().put(tag.getName(), tag);
        }
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        super.write(outputStream, writeName);
        for (NamedTag namedTag : getValue().values()) {
            namedTag.write(outputStream, writeName);
        }
    }
}
