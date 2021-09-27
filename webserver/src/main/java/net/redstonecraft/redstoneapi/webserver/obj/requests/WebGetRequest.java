package net.redstonecraft.redstoneapi.webserver.obj.requests;

import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.webserver.WebServer;

public class WebGetRequest extends WebRequest {

    public WebGetRequest(String path, HttpHeader[] headers, byte[] content, WebServer webServer) {
        super(path, headers, content, webServer);
    }
}
