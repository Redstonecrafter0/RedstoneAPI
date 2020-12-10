package net.redstonecraft.redstoneapi.sql.obj;

public interface Base {

    String tablename = null;
    Column[] columns = new Column[]{};

    default Column getColumn(String name) {
        for (Column i : columns) {
            if (i.name.equals(name)) {
                return i;
            }
        }
        return null;
    }

}
