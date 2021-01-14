package net.redstonecraft.redstoneapi.tools;

import net.redstonecraft.redstoneapi.json.JSONArray;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.tools.mojangapi.*;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MojangAPI {

    public static UUID getUnigueIdByName(String name) {
        try {
            HttpRequest req = HttpRequest.get("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));
            if (req.responseCode == 200) {
                JSONObject resp = JSONParser.parseObject(new String(req.content, StandardCharsets.UTF_8));
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

    public static List<NameHistory> getNameHistory(UUID uuid) {
        try {
            HttpRequest req = HttpRequest.get("https://api.mojang.com/user/profiles/" + URLEncoder.encode(uniqueIdToString(uuid), StandardCharsets.UTF_8.toString()) + "/names");
            if (req.responseCode == 200) {
                JSONArray obj = JSONParser.parseArray(new String(req.content, StandardCharsets.UTF_8));
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

    public static MojangProfile getProfile(UUID uuid) {
        try {
            HttpRequest req = HttpRequest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + URLEncoder.encode(uniqueIdToString(uuid), StandardCharsets.UTF_8.toString()));
            if (req.responseCode == 200) {
                JSONObject resp = Objects.requireNonNull(JSONParser.parseObject(new String(req.content, StandardCharsets.UTF_8)));
                UUID uuid1 = getUniqueIdByString(resp.getString("id"));
                String name = resp.getString("name");
                JSONObject textures = Objects.requireNonNull(JSONParser.parseObject(new String(new BASE64Decoder().decodeBuffer(resp.getArray("properties").getObject(0).getString("value")), StandardCharsets.UTF_8)));
                if (textures.getObject("textures").containsKey("CAPE")) {
                    return new MojangProfile(uuid1, name, textures.getObject("textures").getObject("SKIN").getString("url"), textures.getObject("textures").getObject("CAPE").getString("url"), textures.getLong("timestamp"));
                } else {
                    return new MojangProfile(uuid1, name, textures.getObject("textures").getObject("SKIN").getString("url"), null, textures.getLong("timestamp"));
                }
            } else {
                return null;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    private static UUID getUniqueIdByString(String uuid) {
        StringBuffer sb = new StringBuffer(uuid);
        sb.insert(8, "-");
        sb = new StringBuffer(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuffer(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuffer(sb.toString());
        sb.insert(23, "-");
        return UUID.fromString(sb.toString());
    }

    private static String uniqueIdToString(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

}