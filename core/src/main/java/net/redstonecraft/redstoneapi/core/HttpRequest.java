package net.redstonecraft.redstoneapi.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpRequest class for simpler HTTP request usage
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
@SuppressWarnings("unused")
public class HttpRequest {

    /**
     * Simple HTTP GET request
     *
     * @param url target url
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpResponse} object
     *
     * @throws IOException if an I/O Exception occurs
     * */
    public static HttpResponse get(String url, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("GET");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key(), i.value());
        }
        return getHttpResponse(con);
    }

    /**
     * Simple HTTP HEAD request
     *
     * @param url target url
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpResponse} object
     *
     * @throws IOException if an I/O Exception occurs
     * */
    public static HttpResponse head(String url, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("HEAD");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key(), i.value());
        }
        return getHttpResponse(con);
    }

    /**
     * Simple HTTP DELETE request
     *
     * @param url target url
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpResponse} object
     *
     * @throws IOException if an I/O Exception occurs
     * */
    public static HttpResponse delete(String url, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("DELETE");
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key(), i.value());
        }
        return getHttpResponse(con);
    }

    /**
     * Simple HTTP POST request
     *
     * @param url target url
     * @param content content to post
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpResponse} object
     *
     * @throws IOException if an I/O Exception occurs
     * */
    public static HttpResponse post(String url, byte[] content, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("POST");
        return getHttpResponse(content, con, header);
    }

    /**
     * Simple HTTP PUT request
     *
     * @param url target url
     * @param content content to post
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpResponse} object
     *
     * @throws IOException if an I/O Exception occurs
     * */
    public static HttpResponse put(String url, byte[] content, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("PUT");
        return getHttpResponse(content, con, header);
    }

    /**
     * Simple HTTP PATCH request
     *
     * @param url target url
     * @param content content to post
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpResponse} object
     *
     * @throws IOException if an I/O Exception occurs
     * */
    public static HttpResponse patch(String url, byte[] content, HttpHeader... header) throws IOException {
        URL urlUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlUrl.openConnection();
        con.setRequestMethod("PATCH");
        return getHttpResponse(content, con, header);
    }

    private static HttpResponse getHttpResponse(byte[] content, HttpURLConnection con, HttpHeader[] header) throws IOException {
        for (HttpHeader i : header) {
            con.addRequestProperty(i.key(), i.value());
        }
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        out.write(content);
        out.flush();
        return getHttpResponse(con);
    }

    private static HttpResponse getHttpResponse(HttpURLConnection con) throws IOException {
        InputStream response;
        if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            response = con.getInputStream();
        } else {
            response = con.getErrorStream();
        }
        int code = con.getResponseCode();
        String mime = con.getContentType();
        List<HttpHeader> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> i : con.getHeaderFields().entrySet()) {
            if (i.getKey() != null) {
                headers.add(new HttpHeader(i.getKey(), String.join(", ", i.getValue())));
            }
        }
        con.disconnect();
        return new HttpResponse(response, code, mime, headers.toArray(new HttpHeader[0]));
    }

}
