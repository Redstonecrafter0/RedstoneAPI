package net.redstonecraft.redstoneapi.tools.mojangapi;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class MojangProfile {

    private final UUID uuid;
    private final String name;
    private final String skinUrl;
    private final String capeUrl;
    private final long timestamp;

    public MojangProfile(UUID uuid, String name, String skinUrl, String capeUrl, long timestamp) {
        this.uuid = uuid;
        this.name = name;
        this.skinUrl = skinUrl;
        this.capeUrl = capeUrl;
        this.timestamp = timestamp;
    }

    public UUID getIniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BufferedImage fetchSkin() {
        try {
            return ImageIO.read(new URL(skinUrl));
        } catch (IOException ignored) {
        }
        return null;
    }

    public String fetchSkinB64() {
        BufferedImage img = fetchSkin();
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

    public BufferedImage fetchCape() {
        try {
            return ImageIO.read(new URL(capeUrl));
        } catch (IOException ignored) {
        }
        return null;
    }

    public String fetchCapeB64() {
        BufferedImage img = fetchCape();
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

}
