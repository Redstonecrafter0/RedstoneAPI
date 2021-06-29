package net.redstonecraft.redstoneapi.tools.webhooks.discord;

import net.redstonecraft.redstoneapi.json.JSONArray;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.tools.HttpHeader;
import net.redstonecraft.redstoneapi.tools.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public class WebhookMessage {

    private final JSONObject data = new JSONObject();

    public WebhookMessage setMessage(String content) {
        data.put("content", content);
        return this;
    }

    public WebhookMessage setUsername(String username) {
        data.put("username", username);
        return this;
    }

    public WebhookMessage setAvatarUrl(String avatar_url) {
        data.put("avatar_url", avatar_url);
        return this;
    }

    public WebhookMessage addEmbeds(EmbedMessage... messages) {
        if (!data.containsKey("embeds")) {
            data.put("embeds", new JSONArray());
        }
        for (EmbedMessage i : messages) {
            data.getArray("embeds").add(i.getJson());
        }
        return this;
    }

    public void send(String url) throws IOException {
        HttpRequest.post(url, data.toJSONString().getBytes(StandardCharsets.UTF_8), new HttpHeader("Content-Type", "application/json"));
    }

    public void send(String url, byte[] fileData, String filename) throws IOException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setContentType(ContentType.MULTIPART_FORM_DATA);
        if (data.containsKey("embeds")) {
            for (Object o : data.getArray("embeds")) {
                JSONObject i = (JSONObject) o;
                if (i.containsKey("image") && i.getObject("image") == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("url", "attachment://" + filename);
                    i.put("image", obj);
                }
                if (i.containsKey("thumbnail") && i.getObject("thumbnail") == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("url", "attachment://" + filename);
                    i.put("thumbnail", obj);
                }
            }
        }
        builder.addBinaryBody("file", fileData, ContentType.APPLICATION_OCTET_STREAM, filename);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(builder.addTextBody("payload_json", data.toJSONString()).build());
        HttpClients.createDefault().execute(httpPost);
    }

    public void send(String url, InputStream is, String filename) throws IOException {
        byte[] arr = new byte[is.available()];
        is.read(arr);
        send(url, arr, filename);
    }

    public void send(String url, File file) throws IOException {
        send(url, new FileInputStream(file), file.getName());
    }

    public JSONObject getData() {
        return data;
    }

}
