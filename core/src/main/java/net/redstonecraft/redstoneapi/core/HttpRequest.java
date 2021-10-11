package net.redstonecraft.redstoneapi.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
     * Simple HTTP GET request
     *
     * @param url target url
     * @param header array of {@link HttpHeader}
     *
     * @return response as {@link HttpResponse} object
     *
     * @throws IOException if an I/O Exception occures
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
     * @throws IOException if an I/O Exception occures
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
     * @throws IOException if an I/O Exception occures
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
     * @throws IOException if an I/O Exception occures
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
     * @throws IOException if an I/O Exception occures
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
     * @throws IOException if an I/O Exception occures
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
            if (i.getKey() != null) {
                headers.add(new HttpHeader(i.getKey(), String.join(", ", i.getValue())));
            }
        }
        con.disconnect();
        return new HttpResponse(response, code, mime, headers.toArray(new HttpHeader[0]));
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

}
