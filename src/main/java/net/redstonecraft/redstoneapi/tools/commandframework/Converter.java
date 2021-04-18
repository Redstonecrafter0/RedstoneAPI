package net.redstonecraft.redstoneapi.tools.commandframework;

/**
 * Abstract converter for creating custom converters
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class Converter<T> {

    private final Class<T> clazz;

    public Converter(Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract T convert(String data) throws ConvertException;

    public Class<T> convertsTo() {
        return clazz;
    }

}
