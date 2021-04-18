package net.redstonecraft.redstoneapi.tools.commandframework.converters;

import net.redstonecraft.redstoneapi.tools.commandframework.ConvertException;
import net.redstonecraft.redstoneapi.tools.commandframework.Converter;

public class BooleanConverter extends Converter<Boolean> {

    public BooleanConverter() {
        super(Boolean.class);
    }

    @Override
    public Boolean convert(String data) throws ConvertException {
        try {
            return Boolean.parseBoolean(data);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

}
