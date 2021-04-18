package net.redstonecraft.redstoneapi.tools.commandframework.converters;

import net.redstonecraft.redstoneapi.tools.commandframework.ConvertException;
import net.redstonecraft.redstoneapi.tools.commandframework.Converter;

public class FloatConverter extends Converter<Float> {

    public FloatConverter() {
        super(Float.class);
    }

    @Override
    public Float convert(String data) throws ConvertException {
        try {
            return Float.parseFloat(data);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

}
