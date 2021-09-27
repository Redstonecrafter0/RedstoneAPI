package net.redstonecraft.redstoneapi.core;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.data.json.parser.ParseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Customized JSON Web Token
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class JWT {

    private final String key;
    private final JWTSigner jwtSigner;

    public static final JWTSigner DEFAULT_JWT_SIGNER = new JWTSigner() {
        @Override
        public String sign(String content, String key) {
            return Hashlib.sha512(content + key);
        }

        @Override
        public boolean verify(String content, String key, String sign) {
            return Hashlib.sha512(content + key).equals(sign);
        }
    };

    public JWT(String key, JWTSigner jwtSigner) {
        this.key = key;
        this.jwtSigner = jwtSigner;
    }

    public String createJsonWebToken(JSONObject header, JSONObject payload) {
        return createJsonWebToken(header.toJSONString(), payload.toJSONString());
    }

    public String createJsonWebToken(JSONObject header, JSONArray payload) {
        return createJsonWebToken(header.toJSONString(), payload.toJSONString());
    }

    public String createJsonWebToken(JSONArray header, JSONObject payload) {
        return createJsonWebToken(header.toJSONString(), payload.toJSONString());
    }

    public String createJsonWebToken(JSONArray header, JSONArray payload) {
        return createJsonWebToken(header.toJSONString(), payload.toJSONString());
    }

    public String createJsonWebToken(String header, String payload) {
        return createJsonWebToken(header.getBytes(StandardCharsets.UTF_8), payload.getBytes(StandardCharsets.UTF_8));
    }

    public String createJsonWebToken(byte[] header, byte[] payload) {
        String content = b64encode(header) + "." + b64encode(payload);
        return content + "." + b64encode(jwtSigner.sign(content, key).getBytes(StandardCharsets.UTF_8));
    }

    public boolean verifyJsonWebToken(String token) {
        try {
            String content = token.split("\\.")[0] + "." + token.split("\\.")[1];
            return jwtSigner.verify(content, key, new String(b64decode(token.split("\\.")[2]), StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            return false;
        }
    }

    public static JWTData getData(String token) {
        try {
            return new JWTData(b64decode(token.split("\\.")[0]), b64decode(token.split("\\.")[1]));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String b64encode(byte[] src) {
        return Base64.getUrlEncoder().encodeToString(src).replace("=", "");
    }

    private static byte[] b64decode(String src) {
        return Base64.getUrlDecoder().decode(src + StringUtils.sameChar('=', 4 - (src.length() % 4)));
    }

    public static class JWTData {

        private final byte[] header;
        private final byte[] payload;

        private JWTData(byte[] header, byte[] payload) {
            this.header = header;
            this.payload = payload;
        }

        public byte[] getHeader() {
            return header;
        }

        public byte[] getBody() {
            return payload;
        }

        public String getHeaderAsString() {
            return new String(header, StandardCharsets.UTF_8);
        }

        public String getPayloadAsString() {
            return new String(payload, StandardCharsets.UTF_8);
        }

        public JSONObject getHeaderAsJsonObject() throws ParseException {
            return JSONParser.parseObject(getHeaderAsString());
        }

        public JSONObject getPayloadAsJsonObject() throws ParseException {
            return JSONParser.parseObject(getPayloadAsString());
        }

        public JSONArray getHeaderAsJsonArray() {
            return JSONParser.parseArray(getHeaderAsString());
        }

        public JSONArray getPayloadAsJsonArray() {
            return JSONParser.parseArray(getPayloadAsString());
        }
    }

    public interface JWTSigner {

        String sign(String content, String key);
        boolean verify(String content, String key, String sign);

    }

}
