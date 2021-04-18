package net.redstonecraft.redstoneapi.tools.commandframework.converters;

import net.redstonecraft.redstoneapi.tools.commandframework.ConvertException;
import net.redstonecraft.redstoneapi.tools.commandframework.Converter;

public class LongConverter extends Converter<Long> {

    public LongConverter() {
        super(Long.class);
    }

    @Override
    public Long convert(String data) throws ConvertException {
        try {
            return Long.parseLong(data);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

}
