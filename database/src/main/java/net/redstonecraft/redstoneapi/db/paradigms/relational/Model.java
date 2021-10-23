package net.redstonecraft.redstoneapi.db.paradigms.relational;

import net.redstonecraft.redstoneapi.core.tuple.Pair;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.Column;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.HideOnJson;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.PrimaryKey;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public abstract class Model {

    public String getTablename() {
        return getClass().getAnnotation(Table.class).value();
    }

    public List<Field> getDatafields() {
        List<Field> list = new ArrayList<>();
        for (Field i : getClass().getFields()) {
            if (i.isAnnotationPresent(Column.class)) {
                list.add(i);
            }
        }
        return list;
    }

    public Field getPrimaryKey() {
        for (Field i : getClass().getFields()) {
            if (i.isAnnotationPresent(PrimaryKey.class)) {
                return i;
            }
        }
        return null;
    }

    public Long getId() {
        try {
            return ((Number) getPrimaryKey().get(this)).longValue();
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }

    public static <T extends Model> T fromJsonObject(JSONObject object, Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (Field i : clazz.getDeclaredFields()) {
            if (object.containsKey(i.getName())) {
                i.set(instance, object.get(i.getName()));
            }
        }
        return instance;
    }

    public Pair<Long, JSONObject> toJsonObject() {
        JSONObject obj = new JSONObject();
        for (Field i : getClass().getDeclaredFields()) {
            if (!i.isAnnotationPresent(HideOnJson.class)) {
                try {
                    obj.put(i.getName(), i.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Pair<>(getId(), obj);
    }

}
