package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.core.HttpResponseCode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * The response sent to the client when an HTTP request came in to the {@link net.redstonecraft.redstoneapi.webserver.WebServer}
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebResponse {

    private final InputStream content;
    private final HttpHeader[] headers;
    private HttpResponseCode code;

    public WebResponse(String content, HttpHeader... headers) {
        this(content, HttpResponseCode.OK, headers);
    }

    public WebResponse(String content, HttpResponseCode code, HttpHeader... headers) {
        this((content != null ? content : "null").getBytes(StandardCharsets.UTF_8), code, headers);
    }

    public WebResponse(byte[] content, HttpHeader... headers) {
        this(content, HttpResponseCode.OK, headers);
    }

    public WebResponse(byte[] content, HttpResponseCode code, HttpHeader... headers) {
        this(new ByteArrayInputStream(content), code, headers);
    }

    public WebResponse(ByteBuffer content, HttpHeader... headers) {
        this(content, HttpResponseCode.OK, headers);
    }

    public WebResponse(ByteBuffer content, HttpResponseCode code, HttpHeader... headers) {
        this(content.array(), code, headers);
    }

    public WebResponse(InputStream content, HttpHeader... headers) {
        this(content, HttpResponseCode.OK, headers);
    }

    public WebResponse(InputStream content, HttpResponseCode code, HttpHeader... headers) {
        this.content = content;
        this.headers = headers;
        this.code = code;
    }

    public HttpResponseCode getCode() {
        return code;
    }

    public InputStream getContent() {
        return content;
    }

    public HttpHeader[] getHeaders() {
        return headers;
    }

    public void setErrorCode(HttpResponseCode code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "WebResponse{" +
                "headers=" + Arrays.toString(headers) +
                ", code=" + code.getCode() +
                '}';
    }
}
