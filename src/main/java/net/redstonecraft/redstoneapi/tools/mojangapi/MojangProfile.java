package net.redstonecraft.redstoneapi.tools.mojangapi;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
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
    private final String signature;
    private final String skinTexture;

    public MojangProfile(UUID uuid, String name, String skinUrl, String capeUrl, long timestamp, String signature, String skinTexture) {
        this.uuid = uuid;
        this.name = name;
        this.skinUrl = skinUrl;
        this.capeUrl = capeUrl;
        this.timestamp = timestamp;
        this.signature = signature;
        this.skinTexture = skinTexture;
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
                return Base64.getEncoder().encodeToString(os.toByteArray());
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
                return Base64.getEncoder().encodeToString(os.toByteArray());
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    /**
     * @return the skin signature used in a gameprofile sent by a minecraft server to a client
     * */
    public String getSignature() {
        return signature;
    }

    /**
     * @return the skin texture value used in a gameprofile sent by a minecraft server to a client
     * */
    public String getSkinTexture() {
        return skinTexture;
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
