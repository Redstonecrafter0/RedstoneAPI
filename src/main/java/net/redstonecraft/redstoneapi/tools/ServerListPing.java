package net.redstonecraft.redstoneapi.tools;

import net.redstonecraft.redstoneapi.json.JSONArray;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.json.parser.JSONParser;
import org.xbill.DNS.*;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ServerListPing {

    public final String faviconB64;
    public final String motd;
    public final String motdColored;
    public final RenderedImage favicon;
    public final String version;
    public final long onlinePlayers;
    public final long maxPlayers;

    public ServerListPing(String motd, String motdColored, String faviconB64, RenderedImage favicon, String version, long onlinePlayers, long maxPlayers) {
        this.motd = motd;
        this.motdColored = motdColored;
        this.faviconB64 = faviconB64;
        this.favicon = favicon;
        this.version = version;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
    }

    public static ServerListPing ping(String host, int port) {
        try {
            try {
                SRVRecord srvRecord = (SRVRecord) lookupRecord("_minecraft._tcp." + host, Type.SRV);
                host = srvRecord.getTarget().toString().replaceFirst("\\.$","");
            } catch (UnknownHostException ignored) {
            }
            Socket socket = new Socket(host, port);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
            DataOutputStream handshake = new DataOutputStream(handshake_bytes);
            handshake.writeByte(0x00);
            writeVarInt(handshake, 4);
            writeVarInt(handshake, host.length());
            handshake.writeBytes(host);
            handshake.writeShort(port);
            writeVarInt(handshake, 1);
            writeVarInt(out, handshake_bytes.size());
            out.write(handshake_bytes.toByteArray());
            out.writeByte(0x01);
            out.writeByte(0x00);
            int length = readVarInt(in);
            byte[] data = new byte[length];
            in.readFully(data);
            String json = new String(data, StandardCharsets.UTF_8).substring(3);
            JSONObject root = new JSONParser().parseObject(json);
            JSONObject version = Objects.requireNonNull(root).getObject("version");
            String versionName = version.getString("name");
            JSONObject players = root.getObject("players");
            long online = players.getLong("online");
            long maxPlayers = players.getLong("max");
            StringBuilder sb = new StringBuilder();
            if (root.get("description") instanceof JSONObject) {
                JSONObject description = root.getObject("description");
                sb.append(description.getString("text"));
                JSONArray extra = description.getArray("extra");
                if (extra != null) {
                    for (Object o : extra) {
                        JSONObject i = (JSONObject) o;
                        if (i.containsKey("color")) {
                            sb.append(getColorCode(i.getString("color")));
                        }
                        if (i.containsKey("bold")) {
                            if (i.getBoolean("bold")) {
                                sb.append("§l");
                            }
                        }
                        if (i.containsKey("obfuscated")) {
                            if (i.getBoolean("obfuscated")) {
                                sb.append("§k");
                            }
                        }
                        if (i.containsKey("strikethrough")) {
                            if (i.getBoolean("strikethrough")) {
                                sb.append("§m");
                            }
                        }
                        if (i.containsKey("underline")) {
                            if (i.getBoolean("underline")) {
                                sb.append("§n");
                            }
                        }
                        if (i.containsKey("italic")) {
                            if (i.getBoolean("italic")) {
                                sb.append("§o");
                            }
                        }
                        sb.append(i.getString("text"));
                        if (i.containsKey("color") || i.containsKey("bold") || i.containsKey("obfuscated") || i.containsKey("strikethrough") || i.containsKey("underline") || i.containsKey("italic")) {
                            sb.append("§r");
                        }
                    }
                }
            } else if (root.get("description") instanceof String) {
                sb.append(root.getString("description"));
            }
            String motdColored = sb.toString();
            String motd = removeColor(motdColored);
            String b64favicon = root.getString("favicon");
            RenderedImage favicon = null;
            try {
                favicon = b64toImage(b64favicon);
            } catch (NullPointerException ignored) {
            }
            return new ServerListPing(motd, motdColored, b64favicon, favicon, versionName, online, maxPlayers);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static RenderedImage b64toImage(String base64) throws IOException {
        String imageString = base64.split(",")[1];
        byte[] imageBytes = new BASE64Decoder().decodeBuffer(imageString);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    private static void writeVarInt(DataOutputStream out, int paramInt) {
        try {
            while (true) {
                if ((paramInt & 0xFFFFFF80) == 0) {
                    out.writeByte(paramInt);
                    return;
                }
                out.writeByte(paramInt & 0x7F | 0x80);
                paramInt >>>= 7;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int readVarInt(DataInputStream in) {
        int i = 0;
        int j = 0;
        while (true) {
            try {
                int k = in.readByte();
                i |= (k & 0x7F) << j++ * 7;
                if (j > 5) {
                    return 0;
                }
                if ((k & 0x80) != 128)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    private static String removeColor(String input) {
        return input.replaceAll("§0", "")
                .replaceAll("§1", "")
                .replaceAll("§2", "")
                .replaceAll("§3", "")
                .replaceAll("§4", "")
                .replaceAll("§5", "")
                .replaceAll("§6", "")
                .replaceAll("§7", "")
                .replaceAll("§8", "")
                .replaceAll("§9", "")
                .replaceAll("§a", "")
                .replaceAll("§b", "")
                .replaceAll("§c", "")
                .replaceAll("§d", "")
                .replaceAll("§e", "")
                .replaceAll("§f", "")
                .replaceAll("§k", "")
                .replaceAll("§l", "")
                .replaceAll("§m", "")
                .replaceAll("§n", "")
                .replaceAll("§o", "")
                .replaceAll("§r", "");
    }

    private static String getColorCode(String input) {
        String key = MinecraftColors.getColorByName(input).key;
        return key.equals("") ? "" : "§" + key;
    }

    private static Record lookupRecord(String hostName, int type) throws UnknownHostException {
        Record record;
        Lookup lookup;
        int result;
        try {
            lookup = new Lookup(hostName, type);
        } catch (TextParseException e) {
            throw new UnknownHostException(String.format("Host '%s' parsing error:%s", hostName, e.getMessage()));
        }
        lookup.run();
        result = lookup.getResult();
        if (result == Lookup.SUCCESSFUL) {
            return lookup.getAnswers()[0];
        } else {
            switch (result) {
                case Lookup.HOST_NOT_FOUND:
                    throw new UnknownHostException(String.format("Host '%s' not found", hostName));
                case Lookup.TYPE_NOT_FOUND:
                    throw new UnknownHostException(String.format("Host '%s' not found (no A record)", hostName));
                case Lookup.UNRECOVERABLE:
                    throw new UnknownHostException(String.format("Cannot lookup host '%s'", hostName));
                case Lookup.TRY_AGAIN:
                    throw new UnknownHostException(String.format("Temporary failure to lookup host '%s'", hostName));
                default:
                    throw new UnknownHostException(String.format("Unknown error %d in host lookup of '%s'", result, hostName));
            }
        }
    }

}
