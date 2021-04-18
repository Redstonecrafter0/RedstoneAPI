package net.redstonecraft.redstoneapi.tools.commandframework.converters;

import net.redstonecraft.redstoneapi.tools.commandframework.ConvertException;
import net.redstonecraft.redstoneapi.tools.commandframework.Converter;

public class IntegerConverter extends Converter<Integer> {

    public IntegerConverter() {
        super(Integer.class);
    }

    @Override
    public Integer convert(String data) throws ConvertException {
        try {
            return Integer.parseInt(data);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

}
