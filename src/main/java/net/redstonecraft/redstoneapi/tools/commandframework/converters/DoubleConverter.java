package net.redstonecraft.redstoneapi.tools.commandframework.converters;

import net.redstonecraft.redstoneapi.tools.commandframework.ConvertException;
import net.redstonecraft.redstoneapi.tools.commandframework.Converter;

public class DoubleConverter extends Converter<Double> {

    public DoubleConverter() {
        super(Double.class);
    }

    @Override
    public Double convert(String data) throws ConvertException {
        try {
            return Double.parseDouble(data);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

}
