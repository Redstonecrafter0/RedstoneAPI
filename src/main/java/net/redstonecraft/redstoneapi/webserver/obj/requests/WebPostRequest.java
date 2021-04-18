package net.redstonecraft.redstoneapi.webserver.obj.requests;

import net.redstonecraft.redstoneapi.tools.HttpHeader;
import net.redstonecraft.redstoneapi.webserver.WebServer;
import net.redstonecraft.redstoneapi.webserver.obj.WebArgument;

import java.util.ArrayList;
import java.util.List;

public class WebPostRequest extends WebRequest {

    public WebPostRequest(String path, HttpHeader[] headers, byte[] content, WebServer webServer) {
        super(path, headers, content, webServer);
    }

    public WebArgument[] getFormData() {
        List<WebArgument> list = new ArrayList<>();
        for (String i : getContentAsString().split("&")) {
            try {
                list.add(new WebArgument(i.split("=")[0], i.split("=")[1]));
            } catch (Exception ignored) {
            }
        }
        WebArgument[] arr = new WebArgument[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

}
