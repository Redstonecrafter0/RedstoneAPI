package net.redstonecraft.redstoneapi.db;

import net.redstonecraft.redstoneapi.core.Pair;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.db.annotations.HideOnJson;

import java.lang.reflect.Field;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public abstract class Model {

    private Long id;

    public Long getId() {
        return id;
    }

    public static <T extends Model> T fromJsonObject(JSONObject object, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T instance = clazz.newInstance();
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
        return new Pair<>(id, obj);
    }

}
