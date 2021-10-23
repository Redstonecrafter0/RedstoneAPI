package net.redstonecraft.redstoneapi.webserver.internal;

import net.redstonecraft.redstoneapi.core.http.HttpResponseCode;
import net.redstonecraft.redstoneapi.webserver.ErrorHandler;
import net.redstonecraft.redstoneapi.webserver.obj.HttpHeaders;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class ErrorHandlerManager {

    private final Map<HttpResponseCode, ErrorHandler> errorHandlers = new HashMap<>();
    private final ErrorHandler universalErrorHandler;

    public ErrorHandlerManager(ErrorHandler universalErrorHandler) {
        this.universalErrorHandler = universalErrorHandler;
    }

    public void setHandler(HttpResponseCode code, ErrorHandler handler) {
        if (HttpResponseCode.isError(code)) {
            errorHandlers.put(code, handler);
        }
    }

    public void removeHandler(HttpResponseCode code) {
        errorHandlers.remove(code);
    }

    public WebResponse handle(HttpResponseCode code, String url, Map<String, String> webArgs, HttpHeaders headers) {
        WebResponse errorResponse = getHandler(code).handleError(code, url, webArgs, headers);
        return errorResponse;
    }

    public ErrorHandler getHandler(HttpResponseCode code) {
        ErrorHandler errorHandler = errorHandlers.get(code);
        return errorHandler != null ? errorHandler : universalErrorHandler;
    }

}
