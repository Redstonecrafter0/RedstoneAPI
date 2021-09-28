package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.core.HttpResponseCode;
import net.redstonecraft.redstoneapi.core.MimeType;
import net.redstonecraft.redstoneapi.webserver.obj.RenderedItems;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public abstract class RequestHandler {

    private WebServer webServer = null;

    void setWebServer(WebServer webServer) {
        if (this.webServer == null) {
            this.webServer = webServer;
        } else {
            throw new IllegalStateException();
        }
    }

    public WebResponse redirect(String url) {
        return new WebResponse(new byte[0], HttpResponseCode.FOUND, new HttpHeader("Location", url));
    }

    public WebResponse redirect(String url, boolean permanent) {
        return new WebResponse(new byte[0], permanent ? HttpResponseCode.MOVED_PERMANENTLY : HttpResponseCode.FOUND, new HttpHeader("Location", url));
    }

    public WebResponse renderTemplate(String template, RenderedItems items, HttpHeader... headers) throws IOException {
        //noinspection ReadWriteStringCanBeUsed
        return new WebResponse(webServer.jinjava.render(new String(Files.readAllBytes(new File(webServer.getTemplateDir(), template).toPath()), StandardCharsets.UTF_8), items), headers);
    }

    public WebResponse renderTemplate(String template, RenderedItems items, HttpResponseCode code, HttpHeader... headers) throws IOException {
        //noinspection ReadWriteStringCanBeUsed
        return new WebResponse(webServer.jinjava.render(new String(Files.readAllBytes(new File(template).toPath()), StandardCharsets.UTF_8), items), code, headers);
    }

    public WebResponse jsonify(JSONObject object, HttpHeader... headers) {
        return new WebResponse(object.toPrettyJsonString(), headers);
    }

    public WebResponse jsonify(JSONObject object, HttpResponseCode code, HttpHeader... headers) {
        return new WebResponse(object.toPrettyJsonString(), code, headers);
    }

    public WebResponse jsonify(JSONArray array, HttpHeader... headers) {
        return new WebResponse(array.toPrettyJsonString(), headers);
    }

    public WebResponse jsonify(JSONArray array, HttpResponseCode code, HttpHeader... headers) {
        return new WebResponse(array.toPrettyJsonString(), code, headers);
    }

    public WebResponse sendfile(File file) throws IOException {
        return new WebResponse(Files.readAllBytes(file.toPath()), HttpResponseCode.OK, new HttpHeader("Content-Type", MimeType.getByFilename(file.getName()).getMimetype()));
    }

    public WebServer getWebServer() {
        return webServer;
    }

}
