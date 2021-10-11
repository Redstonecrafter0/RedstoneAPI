package net.redstonecraft.redstoneapi.core;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.data.json.parser.ParseException;

import java.nio.charset.StandardCharsets;

public record HttpResponse(byte[] content, int responseCode, String mimeType, HttpHeader[] headers) {

    public String getContentAsString() {
        return new String(content(), StandardCharsets.UTF_8);
    }

    public JSONArray getJsonArray() {
        return JSONParser.parseArray(getContentAsString());
    }

    public JSONObject getJsonObject() throws ParseException {
        return JSONParser.parseObject(getContentAsString());
    }

    public HttpResponseCode getResponse() {
        return HttpResponseCode.getByCode(responseCode);
    }

}
