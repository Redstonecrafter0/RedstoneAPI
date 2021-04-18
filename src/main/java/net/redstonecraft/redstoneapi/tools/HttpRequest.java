package net.redstonecraft.redstoneapi.tools;

import net.redstonecraft.redstoneapi.json.JSONArray;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.json.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * HttpRequest class for easier HTTP request usage
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class HttpRequest {

    /**
     * @deprecated use {@link HttpRequest#getContent()} instead
     * */
    @Deprecated
    public final byte[] content;
    /**
     * @deprecated use {@link HttpRequest#getResponseCode()} instead
     * */
    @Deprecated
    public final int responseCode;
    /**
     * @deprecated use {@link HttpRequest#getMimeType()} instead
     * */
    @Deprecated
    public final String mimeType;
    private final HttpHeader[] headers;

    private HttpRequest(byte[] content, int responseCode, String mimeType, HttpHeader[] headers) {
        this.content = content;
        this.responseCode = responseCode;
        this.mimeType = mimeType;
        this.headers = headers;
    }

    /**
     * Get the response body
     *
     * @return response body
     * */
    public byte[] getContent() {
        return content;
    }

    /**
     * Get the response as {@link String}
     *
     * @return response body
     * */
    public String getContentAsString() {
        return new String(getContent(), StandardCharsets.UTF_8);
    }

    /**
     * Get the {@link JSONArray} object from the response body
     *
     * @return response json array
     * */
    public JSONArray getJsonArray() {
        return JSONParser.parseArray(getContentAsString());
    }

    /**
     * Get the {@link JSONObject} object from the response body
     *
     * @return response json object
     * */
    public JSONObject getJsonObject() {
        return JSONParser.parseObject(getContentAsString());
    }

    /**
     * Get the response code
     *
     * @return HTTP response code
     * */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Get the type of the response
     *
     * @return mimeType
     * */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Get the response headers
     *
     * @return httpHeaders
     * */
    public HttpHeader[] getHeaders() {
        return headers;
    }

    /**
     * Simple HTTP GET request
     *
     * @param url target url
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpRequest} object
     *
     * @throws IOException if an I/O Exception occures
     * */
    public static HttpRequest get(String url, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("GET");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key, i.value);
        }
        byte[] response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = readAllBytes(con.getInputStream());
        } else {
            response = readAllBytes(con.getErrorStream());
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        List<HttpHeader> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> i : con.getHeaderFields().entrySet()) {
            headers.add(new HttpHeader(i.getKey(), String.join(", ", i.getValue())));
        }
        con.disconnect();
        return new HttpRequest(response, code, mime, headers.toArray(new HttpHeader[0]));
    }

    /**
     * Simple HTTP HEAD request
     *
     * @param url target url
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpRequest} object
     *
     * @throws IOException if an I/O Exception occures
     * */
    public static HttpRequest head(String url, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("HEAD");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key, i.value);
        }
        byte[] response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = readAllBytes(con.getInputStream());
        } else {
            response = readAllBytes(con.getErrorStream());
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        List<HttpHeader> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> i : con.getHeaderFields().entrySet()) {
            headers.add(new HttpHeader(i.getKey(), String.join(", ", i.getValue())));
        }

        con.disconnect();
        return new HttpRequest(response, code, mime, headers.toArray(new HttpHeader[0]));
    }

    /**
     * Simple HTTP DELETE request
     *
     * @param url target url
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpRequest} object
     *
     * @throws IOException if an I/O Exception occures
     * */
    public static HttpRequest delete(String url, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("DELETE");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key, i.value);
        }
        byte[] response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = readAllBytes(con.getInputStream());
        } else {
            response = readAllBytes(con.getErrorStream());
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        List<HttpHeader> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> i : con.getHeaderFields().entrySet()) {
            headers.add(new HttpHeader(i.getKey(), String.join(", ", i.getValue())));
        }
        con.disconnect();
        return new HttpRequest(response, code, mime, headers.toArray(new HttpHeader[0]));
    }

    /**
     * Simple HTTP POST request
     *
     * @param url target url
     * @param content content to post
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpRequest} object
     *
     * @throws IOException if an I/O Exception occures
     * */
    public static HttpRequest post(String url, byte[] content, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("POST");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key, i.value);
        }
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        out.write(content);
        out.flush();
        byte[] response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = readAllBytes(con.getInputStream());
        } else {
            response = readAllBytes(con.getErrorStream());
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        List<HttpHeader> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> i : con.getHeaderFields().entrySet()) {
            headers.add(new HttpHeader(i.getKey(), String.join(", ", i.getValue())));
        }
        con.disconnect();
        return new HttpRequest(response, code, mime, headers.toArray(new HttpHeader[0]));
    }

    /**
     * Simple HTTP PUT request
     *
     * @param url target url
     * @param content content to post
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpRequest} object
     *
     * @throws IOException if an I/O Exception occures
     * */
    public static HttpRequest put(String url, byte[] content, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("PUT");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key, i.value);
        }
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        out.write(content);
        out.flush();
        byte[] response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = readAllBytes(con.getInputStream());
        } else {
            response = readAllBytes(con.getErrorStream());
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        List<HttpHeader> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> i : con.getHeaderFields().entrySet()) {
            headers.add(new HttpHeader(i.getKey(), String.join(", ", i.getValue())));
        }
        con.disconnect();
        return new HttpRequest(response, code, mime, headers.toArray(new HttpHeader[0]));
    }

    /**
     * Simple HTTP PATCH request
     *
     * @param url target url
     * @param content content to post
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpRequest} object
     *
     * @throws IOException if an I/O Exception occures
     * */
    public static HttpRequest patch(String url, byte[] content, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("PATCH");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key, i.value);
        }
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        out.write(content);
        out.flush();
        byte[] response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = readAllBytes(con.getInputStream());
        } else {
            response = readAllBytes(con.getErrorStream());
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        List<HttpHeader> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> i : con.getHeaderFields().entrySet()) {
            headers.add(new HttpHeader(i.getKey(), String.join(", ", i.getValue())));
        }
        con.disconnect();
        return new HttpRequest(response, code, mime, headers.toArray(new HttpHeader[0]));
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        int len = Integer.MAX_VALUE;
        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, 8192)];
            int nread = 0;
            while ((n = is.read(buf, nread, Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }
            if (nread > 0) {
                if (Integer.MAX_VALUE - 8 - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
        } while (n >= 0 && remaining > 0);
        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ? result : Arrays.copyOf(result, total);
        }
        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }
        return result;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "content=" + Arrays.toString(content) +
                ", contentAsString=" + new String(content, StandardCharsets.UTF_8) +
                ", responseCode=" + responseCode +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
