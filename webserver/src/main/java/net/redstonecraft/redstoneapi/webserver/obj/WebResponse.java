package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.core.HttpResponseCode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * The response sent to the client when an HTTP request came in to the {@link net.redstonecraft.redstoneapi.webserver.WebServer}
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebResponse {

    private final byte[] content;
    private final HttpHeader[] headers;
    private final HttpResponseCode code;

    public WebResponse(String content, HttpHeader... headers) {
        this.content = content.getBytes(StandardCharsets.UTF_8);
        this.headers = headers;
        this.code = HttpResponseCode.OK;
    }

    public WebResponse(String content, HttpResponseCode code, HttpHeader... headers) {
        this.content = content.getBytes(StandardCharsets.UTF_8);
        this.code = code;
        this.headers = headers;
    }

    public WebResponse(byte[] content, HttpHeader... headers) {
        this.content = content;
        this.headers = headers;
        this.code = HttpResponseCode.OK;
    }

    public WebResponse(byte[] content, HttpResponseCode code, HttpHeader... headers) {
        this.content = content;
        this.headers = headers;
        this.code = code;
    }

    public HttpResponseCode getCode() {
        return code;
    }

    public byte[] getContent() {
        return content;
    }

    public HttpHeader[] getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "WebResponse{" +
                "content=" + Arrays.toString(content) +
                ", headers=" + Arrays.toString(headers) +
                ", code=" + code +
                '}';
    }
}
