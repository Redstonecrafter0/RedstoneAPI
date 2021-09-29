package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.core.HttpResponseCode;
import net.redstonecraft.redstoneapi.webserver.obj.ErrorResponse;
import net.redstonecraft.redstoneapi.webserver.obj.HttpHeaders;

import java.util.Map;

public abstract class ErrorHandler {

    public abstract ErrorResponse handleError(HttpResponseCode code, String url, Map<String, String> args, HttpHeaders headers);

}
