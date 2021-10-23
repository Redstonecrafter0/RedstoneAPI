package net.redstonecraft.redstoneapi.core.http;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.data.json.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public record HttpResponse(InputStream content, int responseCode, String mimeType, HttpHeader[] headers) {

    /**
     * @return all read bytes on the response
     */
    public byte[] getAsByteArray() {
        try {
            return content().readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @return the response converted to a UTF-8 string
     */
    public String getContentAsString() {
        return new String(getAsByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * @return the response parsed to a {@link JSONArray}
     */
    public JSONArray getJsonArray() {
        return JSONParser.parseArray(getContentAsString());
    }

    /**
     * @return the response parsed to a {@link JSONObject}
     */
    public JSONObject getJsonObject() throws ParseException {
        return JSONParser.parseObject(getContentAsString());
    }

    /**
     * @return the response code as the enum
     */
    public HttpResponseCode getResponse() {
        return HttpResponseCode.getByCode(responseCode);
    }

}
