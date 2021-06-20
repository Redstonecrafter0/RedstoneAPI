package net.redstonecraft.redstoneapi.tools.nbt;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class NBTTag<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract byte getId();

    public abstract void read(InputStream paramInputStream) throws IOException;

    public abstract void write(OutputStream outputStream) throws IOException;

    protected byte readByte(InputStream inputStream) throws IOException {
        int value = inputStream.read();
        if (value == -1)
            throw new EOFException();
        return (byte) value;
    }

    protected short readShort(InputStream inputStream) throws IOException {
        return (short) (this.readByte(inputStream) << 8 | this.readByte(inputStream) & 0xFF);
    }

    protected int readInt(InputStream inputStream) throws IOException {
        return this.readShort(inputStream) << 16 | this.readShort(inputStream) & 0xFFFF;
    }

    protected long readLong(InputStream inputStream) throws IOException {
        return (long) this.readInt(inputStream) << 32 | this.readInt(inputStream);
    }

    protected void readByteArray(InputStream inputStream, byte[] bytes) throws IOException {
        int read = 0;
        while (read != bytes.length) {
            int count = inputStream.read(bytes, read, bytes.length - read);
            if (count == 0) {
                throw new EOFException();
            }
            read += count;
        }
    }

    protected String readString(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[this.readShort(inputStream)];
        this.readByteArray(inputStream, bytes);
        return new String(bytes);
    }

    protected void writeByteArray(OutputStream outputStream, byte[] bytes) throws IOException {
        for (byte b : bytes) {
            this.writeByte(outputStream, b);
        }
    }

    protected void writeByte(OutputStream outputStream, byte value) throws IOException {
        outputStream.write(value);
    }

    protected void writeInt(OutputStream outputStream, int value) throws IOException {
        this.writeByte(outputStream, (byte) (value / 256 / 256 / 256));
        this.writeByte(outputStream, (byte) (value / 256 / 256 % 256));
        this.writeByte(outputStream, (byte) (value / 256 % 256));
        this.writeByte(outputStream, (byte) (value % 256));
    }

    protected void writeLong(OutputStream outputStream, long value) throws IOException {
        this.writeInt(outputStream, (int) (value >> 32));
        this.writeInt(outputStream, (int) value);
    }

    protected void writeShort(OutputStream outputStream, short value) throws IOException {
        this.writeByte(outputStream, (byte) (value / 256));
        this.writeByte(outputStream, (byte) (value % 256));
    }

    protected void writeString(OutputStream outputStream, String string) throws IOException {
        byte[] bytes = string.getBytes();
        this.writeShort(outputStream, (short) bytes.length);
        this.writeByteArray(outputStream, bytes);
    }

    public ShortTag toShortTag() {
        if (this instanceof ShortTag) {
            return (ShortTag) this;
        }
        throw new IllegalStateException(this.getClass() + " is not a short tag");
    }

    public ListTag toListTag() {
        if (this instanceof ListTag) {
            return (ListTag) this;
        }
        throw new IllegalStateException(this.getClass() + " is not a list tag");
    }

    public IntTag toIntTag() {
        if (this instanceof IntTag) {
            return (IntTag) this;
        }
        throw new IllegalStateException(this.getClass() + " is not a int tag");
    }

    public CompoundTag toCompoundTag() {
        if (this instanceof CompoundTag) {
            return (CompoundTag) this;
        }
        throw new IllegalStateException(this.getClass() + " is not a compound tag");
    }

    public StringTag toStringTag() {
        if (this instanceof StringTag) {
            return (StringTag) this;
        }
        throw new IllegalStateException(this.getClass() + " is not a string tag");
    }

    public static NBTTag readTag(InputStream inputStream) throws IOException {
        int id = inputStream.read();
        if (id == -1)
            throw new EOFException();
        return NBTTag.readTag(inputStream, id, true);
    }

    protected static NBTTag readTag(InputStream inputStream, int id, boolean readTag) throws IOException {
        NBTTag tag;
        switch (id) {
            case 0:
                tag = new EndTag();
                break;
            case 1:
                tag = new ByteTag();
                break;
            case 2:
                tag = new ShortTag();
                break;
            case 3:
                tag = new IntTag();
                break;
            case 4:
                tag = new LongTag();
                break;
            case 5:
                tag = new FloatTag();
                break;
            case 6:
                tag = new DoubleTag();
                break;
            case 7:
                tag = new ByteArrayTag();
                break;
            case 8:
                tag = new StringTag();
                break;
            case 9:
                tag = new ListTag();
                break;
            case 10:
                tag = new CompoundTag();
                break;
            default:
                throw new IllegalArgumentException("Unknown id: " + id);
        }
        if (readTag) {
            tag.read(inputStream);
        }
        return tag;
    }

    public static void writeTag(OutputStream outputStream, NBTTag nbtTag, boolean writeTag) throws IOException {
        outputStream.write(nbtTag.getId());
        if (writeTag) {
            nbtTag.write(outputStream);
        }
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

}
