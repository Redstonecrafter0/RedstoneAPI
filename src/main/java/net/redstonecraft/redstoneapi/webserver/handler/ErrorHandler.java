package net.redstonecraft.redstoneapi.webserver.handler;

import net.redstonecraft.redstoneapi.tools.HttpHeader;
import net.redstonecraft.redstoneapi.tools.HttpResponseCode;
import net.redstonecraft.redstoneapi.webserver.obj.ErrorResponse;
import net.redstonecraft.redstoneapi.webserver.obj.WebArgument;

public abstract class ErrorHandler {

    public abstract ErrorResponse handleError(HttpResponseCode code, String url, WebArgument[] args, HttpHeader[] headers);

}
