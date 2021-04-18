package net.redstonecraft.redstoneapi.tools.commandframework.converters;

import net.redstonecraft.redstoneapi.tools.commandframework.ConvertException;
import net.redstonecraft.redstoneapi.tools.commandframework.Converter;

public class ByteConverter extends Converter<Byte> {

    public ByteConverter() {
        super(Byte.class);
    }

    @Override
    public Byte convert(String data) throws ConvertException {
        try {
            return Byte.parseByte(data);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

}
