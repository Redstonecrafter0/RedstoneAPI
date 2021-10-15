package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.core.HttpResponseCode;
import net.redstonecraft.redstoneapi.core.MimeType;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public abstract class RequestHandler {

    private WebServer webServer = null;

    void setWebServer(WebServer webServer) {
        if (this.webServer == null) {
            this.webServer = webServer;
        } else {
            throw new IllegalStateException();
        }
    }

    public static WebResponse.Builder redirect(String url) {
        return redirect(url, false);
    }

    public static WebResponse.Builder redirect(String url, boolean permanent) {
        return WebResponse.create().setResponseCode(permanent ? HttpResponseCode.MOVED_PERMANENTLY : HttpResponseCode.FOUND).addHeader(new HttpHeader("Location", url));
    }

    public WebResponse.Builder renderTemplate(String template) throws IOException {
        return renderTemplate(template, new HashMap<>());
    }

    public WebResponse.Builder renderTemplate(String template, Map<String, ?> items) throws IOException {
        return renderTemplate(WebResponse.create(), template, items);
    }

    public WebResponse.Builder renderTemplate(WebResponse.Builder response, String template, Map<String, ?> items) throws IOException {
        return response.setContent(webServer.getJinjava().render(Files.readString(new File(webServer.getTemplateDir(), template).toPath()), items));
    }

    public static WebResponse.Builder jsonify(JSONObject object) {
        return jsonify(WebResponse.create(), object);
    }

    public static WebResponse.Builder jsonify(WebResponse.Builder response, JSONObject object) {
        return response.setContent(object.toPrettyJsonString());
    }

    public static WebResponse.Builder jsonify(JSONArray array) {
        return jsonify(WebResponse.create(), array);
    }

    public static WebResponse.Builder jsonify(WebResponse.Builder response, JSONArray array) {
        return response.setContent(array.toPrettyJsonString());
    }

    public static WebResponse.Builder sendfile(File file) throws IOException {
        return WebResponse.create().setContent(new FileInputStream(file)).addHeader(new HttpHeader("Content-Type", MimeType.getByFilename(file.getName()).getMimetype()));
    }

    public WebServer getWebServer() {
        return webServer;
    }

}
