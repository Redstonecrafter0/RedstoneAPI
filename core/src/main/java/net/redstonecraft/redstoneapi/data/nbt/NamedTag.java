package net.redstonecraft.redstoneapi.data.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class NamedTag<T> extends NBTTag<T> {

    private String name;

    @Override
    public void read(InputStream inputStream) throws IOException {
        this.read(inputStream, true);
    }

    public void read(InputStream inputStream, boolean readName) throws IOException {
        if (readName) {
            this.name = this.readString(inputStream);
        }
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        this.write(outputStream, true);
    }

    public void write(OutputStream outputStream, boolean writeName) throws IOException {
        if (writeName && this.name != null)
            this.writeString(outputStream, this.name);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
