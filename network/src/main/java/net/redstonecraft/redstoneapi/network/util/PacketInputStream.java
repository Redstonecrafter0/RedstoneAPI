package net.redstonecraft.redstoneapi.network.util;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public class PacketInputStream extends ByteArrayInputStream {

    public PacketInputStream(byte[] buf) {
        super(buf);
    }

    public boolean readBoolean() throws IOException {
        int i = read();
        if (i == 1) {
            return true;
        } else if (i == 0) {
            return false;
        } else {
            throw new EOFException();
        }
    }

    public boolean[] readBooleans() {
        boolean[] arr = new boolean[readInt()];
        byte[] raw = new byte[arr.length % 8 == 0 ? arr.length / 8 : arr.length / 8 + 1];
        try {
            read(raw);
        } catch (IOException ignored) {
            throw new IllegalStateException();
        }
        for (int i = 0; i < raw.length; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    arr[(i * 8) + 7 - j] = (raw[i] & (1 << j)) >= 1;
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }
        return arr;
    }

    public byte readByte() {
        return (byte) read();
    }

    public byte[] readBytes() {
        byte[] arr = new byte[readInt()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = readByte();
        }
        return arr;
    }

    public short readShort() {
        byte[] arr = new byte[2];
        try {
            read(arr);
        } catch (IOException ignored) {
        }
        return ByteBuffer.wrap(arr).getShort();
    }

    public short[] readShorts() {
        short[] arr = new short[readInt()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = readShort();
        }
        return arr;
    }

    public int readInt() {
        byte[] arr = new byte[4];
        try {
            read(arr);
        } catch (IOException ignored) {
        }
        return ByteBuffer.wrap(arr).getInt();
    }

    public int[] readInts() {
        int[] arr = new int[readInt()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = readInt();
        }
        return arr;
    }

    public long readLong() {
        byte[] arr = new byte[8];
        try {
            read(arr);
        } catch (IOException ignored) {
        }
        return ByteBuffer.wrap(arr).getLong();
    }

    public long[] readLongs() {
        long[] arr = new long[readInt()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = readLong();
        }
        return arr;
    }

    public float readFloat() {
        byte[] arr = new byte[4];
        try {
            read(arr);
        } catch (IOException ignored) {
        }
        return ByteBuffer.wrap(arr).getFloat();
    }

    public float[] readFloats() {
        float[] arr = new float[readInt()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = readFloat();
        }
        return arr;
    }

    public double readDouble() {
        byte[] arr = new byte[8];
        try {
            read(arr);
        } catch (IOException ignored) {
        }
        return ByteBuffer.wrap(arr).getDouble();
    }

    public double[] readDoubles() {
        double[] arr = new double[readInt()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = readDouble();
        }
        return arr;
    }

    public String readString() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

}
