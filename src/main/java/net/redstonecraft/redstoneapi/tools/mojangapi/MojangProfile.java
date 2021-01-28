package net.redstonecraft.redstoneapi.tools.mojangapi;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

/**
 * MojangProfile object to provide the results of the {@link net.redstonecraft.redstoneapi.tools.MojangAPI} lookup
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class MojangProfile {

    private final UUID uuid;
    private final String name;
    private final String skinUrl;
    private final String capeUrl;
    private final long timestamp;
    private BufferedImage skin = null;
    private BufferedImage cape = null;

    /**
     * Contructor for the MojangProfile object.
     *
     * @param uuid the players UUID
     * @param name the playername
     * @param skinUrl the url of the skin image
     * @param capeUrl the url of the cape image
     * @param timestamp something that the mojang api provides
     *
     * @see net.redstonecraft.redstoneapi.tools.MojangAPI for getting an instance
     */
    public MojangProfile(UUID uuid, String name, String skinUrl, String capeUrl, long timestamp) {
        this.uuid = uuid;
        this.name = name;
        this.skinUrl = skinUrl;
        this.capeUrl = capeUrl;
        this.timestamp = timestamp;
    }

    /**
     * Get the UUID of the player
     *
     * @return the player uuid
     * */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Get the playername
     *
     * @return the playername
     * */
    public String getName() {
        return name;
    }

    /**
     * Get the timestamp
     *
     * @return the timestamp
     * */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Fetch the skin
     *
     * @return the skin object as {@link BufferedImage}
     * */
    private BufferedImage fetchSkin() {
        try {
            return ImageIO.read(new URL(skinUrl));
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * Get skin
     *
     * @return the skin object as {@link BufferedImage}
     * */
    public BufferedImage getSkin() {
        if (skin == null) {
            skin = fetchSkin();
        }
        return skin;
    }

    /**
     * Get skin
     *
     * @return the skin as base64 {@link String}
     * */
    public String getSkinB64() {
        BufferedImage img = getSkin();
        if (img != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(img, "png", os);
                return new BASE64Encoder().encode(os.toByteArray());
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    /**
     * Fetch cape
     *
     * @return the cape object as {@link BufferedImage}
     * */
    private BufferedImage fetchCape() {
        try {
            return ImageIO.read(new URL(capeUrl));
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * Get cape
     *
     * @return the cape object as {@link BufferedImage}
     * */
    public BufferedImage getCape() {
        if (cape == null) {
            cape = fetchCape();
        }
        return cape;
    }

    /**
     * Get cape
     *
     * @return the cape as base64 {@link String}
     * */
    public String getCapeB64() {
        BufferedImage img = getCape();
        if (img != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(img, "png", os);
                return new BASE64Encoder().encode(os.toByteArray());
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "MojangProfile{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", skinUrl='" + skinUrl + '\'' +
                ", capeUrl='" + capeUrl + '\'' +
                ", timestamp=" + timestamp +
                ", skinB64=" + getSkinB64() +
                ", capeB64=" + getCapeB64() +
                '}';
    }
}
