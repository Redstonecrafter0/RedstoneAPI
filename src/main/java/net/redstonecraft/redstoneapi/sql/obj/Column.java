package net.redstonecraft.redstoneapi.sql.obj;

public class Column {

    public final String name;
    public final Object value;

    public Column(String name, Object value, Class<? implements SQLVariable>) {
        this.name = name;
        this.value = value;
    }

}
