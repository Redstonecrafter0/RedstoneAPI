package net.redstonecraft.redstoneapi.network.util;

import net.redstonecraft.redstoneapi.core.utils.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public class PacketOutputStream extends ByteArrayOutputStream {

    public void writeBoolean(boolean b) {
        write(b ? 1 : 0);
    }

    public void writeBooleans(boolean... booleans) {
        writeInt(booleans.length);
        try {
            List<boolean[]> list = new ArrayList<>();
            for (int i = 0; i < booleans.length; i += 8) {
                boolean[] arr = ArrayUtils.arrayOf(false, 8);
                for (int j = 0; j < 8; j++) {
                    try {
                        arr[j] = booleans[i + j];
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        break;
                    }
                }
                list.add(arr);
            }
            byte[] arr = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                int a = 0;
                for (int j = 0; j < 8; j++) {
                    a += list.get(i)[j] ? 1 << 7 - j : 0;
                }
                arr[i] = (byte) a;
            }
            write(arr);
        } catch (IOException ignored) {
            throw new IllegalStateException();
        }
    }

    public void writeByte(byte b) {
        write(b);
    }

    public void writeBytes(byte... bytes) {
        writeInt(bytes.length);
        for (byte b : bytes) {
            write(b);
        }
    }

    public void writeShort(short s) {
        try {
            write(ByteBuffer.allocate(2).putShort(s).array());
        } catch (IOException ignored) {
            throw new IllegalStateException();
        }
    }

    public void writeShorts(short... shorts) {
        writeInt(shorts.length);
        for (short s : shorts) {
            writeShort(s);
        }
    }

    public void writeInt(int i) {
        try {
            write(ByteBuffer.allocate(4).putInt(i).array());
        } catch (IOException ignored) {
            throw new IllegalStateException();
        }
    }

    public void writeInts(int... ints) {
        writeInt(ints.length);
        for (int i : ints) {
            writeInt(i);
        }
    }

    public void writeLong(long l) {
        try {
            write(ByteBuffer.allocate(8).putLong(l).array());
        } catch (IOException ignored) {
            throw new IllegalStateException();
        }
    }

    public void writeLongs(long... longs) {
        writeInt(longs.length);
        for (long l : longs) {
            writeLong(l);
        }
    }

    public void writeFloat(float f) {
        try {
            write(ByteBuffer.allocate(4).putFloat(f).array());
        } catch (IOException ignored) {
            throw new IllegalStateException();
        }
    }

    public void writeFloats(float... floats) {
        writeInt(floats.length);
        for (float f : floats) {
            writeFloat(f);
        }
    }

    public void writeDouble(double d) {
        try {
            write(ByteBuffer.allocate(8).putDouble(d).array());
        } catch (IOException ignored) {
            throw new IllegalStateException();
        }
    }

    public void writeDoubles(double... doubles) {
        writeInt(doubles.length);
        for (double d : doubles) {
            writeDouble(d);
        }
    }

    public void writeString(String s) {
        writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

}
