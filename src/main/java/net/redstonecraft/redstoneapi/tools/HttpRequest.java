package net.redstonecraft.redstoneapi.tools;

import sun.misc.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    public final byte[] content;
    public final int responseCode;
    public final String mimeType;

    public HttpRequest(byte[] content, int responseCode, String mimeType) {
        this.content = content;
        this.responseCode = responseCode;
        this.mimeType = mimeType;
    }

    public static HttpRequest get(String url, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key, i.value);
        }
        byte[] response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = IOUtils.readAllBytes(con.getInputStream());
        } else {
            response = IOUtils.readAllBytes(con.getErrorStream());
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        con.disconnect();
        return new HttpRequest(response, code, mime);
    }

    public static HttpRequest post(String url, byte[] content, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key, i.value);
        }
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        out.write(content);
        out.flush();
        byte[] response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = IOUtils.readAllBytes(con.getInputStream());
        } else {
            response = IOUtils.readAllBytes(con.getErrorStream());
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        con.disconnect();
        return new HttpRequest(response, code, mime);
    }

}
