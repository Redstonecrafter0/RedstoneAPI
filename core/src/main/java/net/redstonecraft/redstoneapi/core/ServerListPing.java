package net.redstonecraft.redstoneapi.core;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;
import org.xbill.DNS.*;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Class to ping a Minecraft server to get some data.
 * Might have problems with DNS resolving when used in MCP, Forge or Fabric projects so use instead the buildin methods for pinging a server.
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class ServerListPing {

    private final static int CHUNK_SIZE = 16;

    private final String faviconB64;
    private final String motd;
    private final String motdColored;
    private final RenderedImage favicon;
    private final String version;
    private final int protocol;
    private final long onlinePlayers;
    private final long maxPlayers;
    private final long latency;
    private final Sample[] sample;

    private ServerListPing(String motd, String motdColored, String faviconB64, RenderedImage favicon, String version, long onlinePlayers, long maxPlayers, int protocol, long latency, Sample[] sample) {
        this.motd = motd;
        this.motdColored = motdColored;
        this.faviconB64 = faviconB64;
        this.favicon = favicon;
        this.version = version;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.protocol = protocol;
        this.latency = latency;
        this.sample = sample;
    }

    /**
     * Ping the server with port 25565
     *
     * @param host server hostname
     *
     * @return response object as {@link ServerListPing}
     * */
    public static ServerListPing ping(String host) {
        return ping(host, 25565);
    }

    /**
     * Ping the server
     *
     * @param host server hostname
     * @param port server port
     *
     * @return response object as {@link ServerListPing}
     * */
    public static ServerListPing ping(String host, int port) {
        try {
            try {
                SRVRecord srvRecord = (SRVRecord) lookupRecord("_minecraft._tcp." + host, Type.SRV);
                host = srvRecord.getTarget().toString().replaceFirst("\\.$","");
            } catch (UnknownHostException ignored) {
            }
            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream handshake = new DataOutputStream(b);
            handshake.writeByte(0x00);
            writeVarInt(handshake, 4);
            writeVarInt(handshake, host.length());
            handshake.writeBytes(host);
            handshake.writeShort(port);
            writeVarInt(handshake, 1);
            writeVarInt(dataOutputStream, b.size());
            dataOutputStream.write(b.toByteArray());
            dataOutputStream.writeByte(0x01);
            dataOutputStream.writeByte(0x00);
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            int size = readVarInt(dataInputStream);
            int id = readVarInt(dataInputStream);
            if (id == -1) {
                throw new IOException("Premature end of stream.");
            }
            if (id != 0x00) {
                throw new IOException("Invalid packetID");
            }
            int length = readVarInt(dataInputStream);
            if (length == -1) {
                throw new IOException("Premature end of stream.");
            }
            if (length == 0) {
                throw new IOException("Invalid string length.");
            }
            byte[] data = new byte[length];
            dataInputStream.readFully(data);
            dataOutputStream.writeByte(0x09);
            dataOutputStream.writeByte(0x01);
            long now = System.currentTimeMillis();
            dataOutputStream.writeLong(now);
            readVarInt(dataInputStream);
            long end = System.currentTimeMillis();
            id = readVarInt(dataInputStream);
            if (id == -1) {
                throw new IOException("Premature end of stream.");
            }
            if (id != 0x01) {
                throw new IOException("Invalid packetID");
            }
            long pingtime = dataInputStream.readLong();
            long latency;
            if (now == pingtime) {
                latency = (end - now) / 4;
            } else {
                latency = 0;
            }
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            String json = new String(data, StandardCharsets.UTF_8);
            JSONObject root = JSONParser.parseObject(json);
            JSONObject version = Objects.requireNonNull(root).getObject("version");
            String versionName = version.getString("name");
            int protocol = version.getInt("protocol");
            JSONObject players = root.getObject("players");
            long online = players.getLong("online");
            long maxPlayers = players.getLong("max");
            Sample[] sample;
            if (players.containsKey("sample")) {
                JSONArray sampleTmp = players.getArray("sample");
                sample = new Sample[sampleTmp.size()];
                for (int i = 0; i < sampleTmp.size(); i++) {
                    sample[i] = new Sample(((JSONObject) sampleTmp.get(i)).getString("name"), UUID.fromString(((JSONObject) sampleTmp.get(i)).getString("id")));
                }
            } else {
                sample = new Sample[]{};
            }
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
            return new ServerListPing(motd, motdColored, b64favicon, favicon, versionName, online, maxPlayers, protocol, latency, sample);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Sample {

        private final String name;
        private final UUID uuid;

        private Sample(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public UUID getUniqueId() {
            return uuid;
        }
    }

    private static RenderedImage b64toImage(String base64) throws IOException {
        String imageString = base64.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(imageString);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    private static String removeColor(String input) {
        return input.replace("§0", "")
                .replace("§1", "")
                .replace("§2", "")
                .replace("§3", "")
                .replace("§4", "")
                .replace("§5", "")
                .replace("§6", "")
                .replace("§7", "")
                .replace("§8", "")
                .replace("§9", "")
                .replace("§a", "")
                .replace("§b", "")
                .replace("§c", "")
                .replace("§d", "")
                .replace("§e", "")
                .replace("§f", "")
                .replace("§k", "")
                .replace("§l", "")
                .replace("§m", "")
                .replace("§n", "")
                .replace("§o", "")
                .replace("§r", "");
    }

    private static String getColorCode(String input) {
        String key = MinecraftColors.getColorByName(input).getKey();
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

    public String getFaviconB64() {
        return faviconB64;
    }

    public RenderedImage getFavicon() {
        return favicon;
    }

    public long getMaxPlayers() {
        return maxPlayers;
    }

    public long getOnlinePlayers() {
        return onlinePlayers;
    }

    public String getMotd() {
        return motd;
    }

    public String getMotdColored() {
        return motdColored;
    }

    public String getVersion() {
        return version;
    }

    public int getProtocol() {
        return protocol;
    }

    public long getLatency() {
        return latency;
    }

    public Sample[] getSample() {
        return sample;
    }

    public String getSampleAsString() {
        String[] tmp = new String[sample.length];
        for (int i = 0; i < sample.length; i++) {
            tmp[i] = sample[i].getName();
        }
        return String.join("\n", tmp);
    }

    public List<Sample> getSampleAsList() {
        return Arrays.asList(getSample());
    }

    @Override
    public String toString() {
        return "ServerListPing{" +
                "motd='" + motd + '\'' +
                ", motdColored='" + motdColored + '\'' +
                ", version='" + version + '\'' +
                ", protocol=" + protocol +
                ", onlinePlayers=" + onlinePlayers +
                ", maxPlayers=" + maxPlayers +
                ", latency=" + latency +
                ", sample=" + Arrays.toString(sample) +
                ", faviconB64='" + faviconB64 + '\'' +
                '}';
    }
}
