package net.redstonecraft.redstoneapi.sql.obj;

public abstract class Base {

    String tablename = null;
    Column[] columns = null;

    Column getColumn(String name) {
        if (columns != null) {
            for (Column i : columns) {
                if (i.name.equals(name)) {
                    return i;
                }
            }
        }
        return null;
    }

}
