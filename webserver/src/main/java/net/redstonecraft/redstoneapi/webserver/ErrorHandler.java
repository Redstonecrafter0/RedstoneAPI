package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.core.HttpResponseCode;
import net.redstonecraft.redstoneapi.webserver.obj.HttpHeaders;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.util.Map;

public abstract class ErrorHandler {

    public abstract WebResponse handleError(HttpResponseCode code, String url, Map<String, String> args, HttpHeaders headers);

}
