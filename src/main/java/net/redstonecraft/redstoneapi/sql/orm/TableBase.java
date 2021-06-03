package net.redstonecraft.redstoneapi.sql.orm;

import net.redstonecraft.redstoneapi.json.JSONObject;

import java.lang.reflect.Field;

/**
 * This class is used to specify a table.
 * Add a constructor without any parameters which executed {@link #init()} for it to work properly.
 * Use {@link #toJsonObject()} if you want to use it for example in the {@link net.redstonecraft.redstoneapi.webserver.WebServer} for a simple api.
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class TableBase {

    protected void init() {
        for (Field i : getClass().getFields()) {
            if (i.isAnnotationPresent(Column.class) && BaseType.class.isAssignableFrom(i.getType())) {
                Column column = i.getAnnotation(Column.class);
                i.setAccessible(true);
                try {
                    ((BaseType) i.get(this)).setKey(i.getName());
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    /**
     * Get a JSON representation of the table entry
     *
     * @return a JSON representation of the table entry except the {@link Field} has the {@link java.lang.annotation.Annotation} {@link HideOnJson} or {@link Column#hideOnJson()} is true
     * */
    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        for (Field i : getClass().getFields()) {
            if (i.isAnnotationPresent(Column.class) && !(i.getAnnotation(Column.class).hideOnJson() || i.isAnnotationPresent(HideOnJson.class)) && BaseType.class.isAssignableFrom(i.getType())) {
                try {
                    Object value = ((BaseType) i.get(this)).getValue();
                    if (Enum.class.isAssignableFrom(value.getClass())) {
                        obj.put(i.getName(), ((Enum) value).name());
                    } else {
                        obj.put(i.getName(), value);
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return obj;
    }

}
