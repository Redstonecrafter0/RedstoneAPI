package net.redstonecraft.redstoneapi.tools.webhooks.discord;

import com.google.gson.internal.bind.util.ISO8601Utils;
import net.redstonecraft.redstoneapi.json.JSONArray;
import net.redstonecraft.redstoneapi.json.JSONObject;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public class EmbedMessage {

    private final JSONObject data = new JSONObject();

    public EmbedMessage() {
        data.put("type", "rich");
    }

    public EmbedMessage setTitle(String title) {
        data.put("title", title);
        return this;
    }

    public EmbedMessage setDescription(String description) {
        data.put("description", description);
        return this;
    }

    public EmbedMessage setUrl(String url) {
        data.put("url", url);
        return this;
    }

    public EmbedMessage setTimestamp(long timestamp, TimeZone timeZone) {
        data.put("timestamp", ISO8601Utils.format(new Date(timestamp), false, timeZone));
        return this;
    }

    public EmbedMessage setTimestamp(long timestamp) {
        setTimestamp(timestamp, TimeZone.getDefault());
        return this;
    }

    public EmbedMessage setColor(int color) {
        data.put("color", color);
        return this;
    }

    public EmbedMessage setFooter(Footer footer) {
        data.put("footer", footer.data);
        return this;
    }

    public EmbedMessage setImage(Image image) {
        data.put("image", image == null ? null : image.data);
        return this;
    }

    public EmbedMessage setThumbnail(Thumbnail thumbnail) {
        data.put("thumbnail", thumbnail == null ? null : thumbnail.data);
        return this;
    }

    public EmbedMessage setAuthor(Author author) {
        data.put("author", author.data);
        return this;
    }

    public EmbedMessage addFields(Field... fields) {
        if (!data.containsKey("fields")) {
            data.put("fields", new JSONArray());
        }
        for (Field i : fields) {
            data.getArray("fields").add(i.data);
        }
        return this;
    }

    public JSONObject getJson() {
        return data;
    }

    public static class Footer {

        private final JSONObject data = new JSONObject();

        public Footer(String text) {
            data.put("text", text);
        }

        public Footer(String text, String icon_url) {
            this(text);
            data.put("icon_url", icon_url);
        }

    }

    public static class Image {

        public static final Image ATTACHMENT = null;

        private final JSONObject data = new JSONObject();

        public Image(String url) {
            data.put("url", url);
        }

    }

    public static class Thumbnail {

        public static final Thumbnail ATTACHMENT = null;

        private final JSONObject data = new JSONObject();

        public Thumbnail(String url) {
            data.put("url", url);
        }

    }

    public static class Author {

        private final JSONObject data = new JSONObject();

        public Author(String name) {
            data.put("name", name);
        }

        public Author url(String url) {
            data.put("url", url);
            return this;
        }

        public Author iconUrl(String icon_url) {
            data.put("icon_url", icon_url);
            return this;
        }

    }

    public static class Field {

        private final JSONObject data = new JSONObject();

        public Field(String title, String description, boolean inline) {
            data.put("title", title);
            data.put("description", description);
            if (inline) {
                data.put("inline", true);
            }
        }

    }

}
