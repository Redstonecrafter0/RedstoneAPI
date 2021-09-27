package net.redstonecraft.redstoneapi.webserver.obj.requests;

import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.webserver.WebServer;

public class WebOptionsRequest extends WebRequest {

    public WebOptionsRequest(String path, HttpHeader[] headers, byte[] content, WebServer webServer) {
        super(path, headers, content, webServer);
    }
}
