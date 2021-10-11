package net.redstonecraft.redstoneapi.core;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.core.mojangapi.MojangProfile;
import net.redstonecraft.redstoneapi.core.mojangapi.NameHistory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A class to provide the basics of the MojangAPI
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class MojangAPI {

    /**
     * Get the players {@link UUID} by the players name
     *
     * @param name playername
     *
     * @return the uuid of the player
     * */
    public static UUID getUnigueIdByName(String name) {
        try {
            HttpResponse response = HttpRequest.get("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));
            if (response.responseCode() == 200) {
                JSONObject resp = JSONParser.parseObject(new String(response.content(), StandardCharsets.UTF_8));
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
     * @return a object containing all the names and the change timestamps
     * */
    public static List<NameHistory> getNameHistory(UUID uuid) {
        try {
            HttpResponse response = HttpRequest.get("https://api.mojang.com/user/profiles/" + URLEncoder.encode(uniqueIdToString(uuid), StandardCharsets.UTF_8.toString()) + "/names");
            if (response.responseCode() == 200) {
                JSONArray obj = JSONParser.parseArray(new String(response.content(), StandardCharsets.UTF_8));
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
     * @return a {@link MojangProfile} object that provides all the information
     * */
    public static MojangProfile getProfile(UUID uuid) {
        try {
            HttpResponse response = HttpRequest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + URLEncoder.encode(uniqueIdToString(uuid), StandardCharsets.UTF_8.toString()) + "?unsigned=false");
            if (response.responseCode() == 200) {
                JSONObject resp = Objects.requireNonNull(JSONParser.parseObject(new String(response.content(), StandardCharsets.UTF_8)));
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
     * A small utlitity to convert the uuid
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
     * A small utlitity to convert the uuid
     *
     * @param uuid uuid
     *
     * @return uuid
     * */
    public static String uniqueIdToString(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

}
