package net.redstonecraft.redstoneapi.core.mojangapi;

import net.redstonecraft.redstoneapi.core.HttpRequest;
import net.redstonecraft.redstoneapi.core.HttpResponse;
import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A class to provide the basics of the MojangAPI like uuid fetching
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class MojangAPI {

    /**
     * Get the players {@link UUID} by the players name
     *
     * @param name player name
     *
     * @return the uuid of the player or null if not found
     * */
    public static UUID getUniqueIdByName(String name) {
        try {
            HttpResponse response = HttpRequest.get("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));
            if (response.responseCode() == 200) {
                JSONObject resp = response.getJsonObject();
                if (resp == null) {
                    return null;
                }
                if (!resp.containsKey("id")) {
                    return null;
                }
                return getUniqueIdByString(resp.getString("id"));
            } else {
                return null;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * Get all names a player had
     *
     * @param uuid the players uuid
     *
     * @return a list containing all the names and the change timestamps or an empty list if not found
     * */
    public static List<NameHistory> getNameHistory(UUID uuid) {
        try {
            HttpResponse response = HttpRequest.get("https://api.mojang.com/user/profiles/" + URLEncoder.encode(uniqueIdToString(uuid), StandardCharsets.UTF_8.toString()) + "/names");
            if (response.responseCode() == 200) {
                JSONArray obj = response.getJsonArray();
                if (obj != null) {
                    List<NameHistory> list = new ArrayList<>();
                    for (Object o : obj) {
                        JSONObject i = (JSONObject) o;
                        if (i.containsKey("name")) {
                            if (i.containsKey("changedToAt")) {
                                list.add(new NameHistory(i.getString("name"), i.getLong("changedToAt")));
                            } else {
                                list.add(new NameHistory(i.getString("name")));
                            }
                        }
                    }
                    return list;
                } else {
                    return new ArrayList<>();
                }
            } else {
                return new ArrayList<>();
            }
        } catch (IOException ignored) {
        }
        return new ArrayList<>();
    }

    /**
     * Get the full profile of a player
     *
     * @param uuid the players uuid
     *
     * @return a {@link MojangProfile} that provides all the information or null if not found
     * */
    public static MojangProfile getProfile(UUID uuid) {
        try {
            HttpResponse response = HttpRequest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + URLEncoder.encode(uniqueIdToString(uuid), StandardCharsets.UTF_8.toString()) + "?unsigned=false");
            if (response.responseCode() == 200) {
                JSONObject resp = response.getJsonObject();
                UUID uuid1 = getUniqueIdByString(resp.getString("id"));
                String name = resp.getString("name");
                JSONObject prop = resp.getArray("properties").getObject(0);
                JSONObject textures = Objects.requireNonNull(JSONParser.parseObject(new String(Base64.getDecoder().decode(prop.getString("value")), StandardCharsets.UTF_8)));
                if (textures.getObject("textures").containsKey("CAPE")) {
                    return new MojangProfile(uuid1, name, textures.getObject("textures").getObject("SKIN").getString("url"), textures.getObject("textures").getObject("CAPE").getString("url"), textures.getLong("timestamp"), prop.getString("signature"), prop.getString("value"));
                } else {
                    return new MojangProfile(uuid1, name, textures.getObject("textures").getObject("SKIN").getString("url"), null, textures.getLong("timestamp"), prop.getString("signature"), prop.getString("value"));
                }
            } else {
                return null;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * A small utlitity to convert the uuid withoud dashes to {@link UUID}
     *
     * @param uuid uuid
     *
     * @return uuid
     * */
    public static UUID getUniqueIdByString(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");
        return UUID.fromString(sb.toString());
    }

    /**
     * A small utlitity to convert the uuid to a {@link String} withour dashes
     *
     * @param uuid uuid
     *
     * @return uuid
     * */
    public static String uniqueIdToString(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

}
