package net.redstonecraft.redstoneapi.webserver.obj.requests;

import net.redstonecraft.redstoneapi.tools.HttpHeader;
import net.redstonecraft.redstoneapi.webserver.WebServer;

public class WebDeleteRequest extends WebRequest {

    public WebDeleteRequest(String path, HttpHeader[] headers, byte[] content, WebServer webServer) {
        super(path, headers, content, webServer);
    }
}
