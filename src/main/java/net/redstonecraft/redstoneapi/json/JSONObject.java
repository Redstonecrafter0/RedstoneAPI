/*
 * $Id: JSONObject.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package net.redstonecraft.redstoneapi.json;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

/**
 * A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
 * 
 * @author FangYidong fangyidong@yahoo.com.cn
 */
public class JSONObject extends HashMap implements Map, JSONAware, JSONStreamAware {

	private static final long serialVersionUID = -503443796854799292L;

	public JSONObject() {
		super();
	}

	public JSONObject(Object obj) {
		super();
		for (Field i : obj.getClass().getFields()) {
			if (!i.isAnnotationPresent(HideJson.class)) {
				try {
					i.setAccessible(true);
					put(i.getName(), i.get(obj));
				} catch (IllegalAccessException ignored) {
				}
			}
		}
	}

	/**
	 * Allows creation of a JSONObject from a Map. After that, both the
	 * generated JSONObject and the Map can be modified independently.
	 * 
	 * @param map
	 */
	public JSONObject(Map map) {
		super(map);
	}

    /**
     * Encode a map into JSON text and write it to out.
     * If this map is also a JSONAware or JSONStreamAware, JSONAware or JSONStreamAware specific behaviours will be ignored at this top level.
     * 
     * @see JSONValue#writeJSONString(Object, Writer)
     * 
     * @param map
     * @param out
     */
	public static void writeJSONString(Map map, Writer out) throws IOException {
		if(map == null){
			out.write("null");
			return;
		}
		
		boolean first = true;
		Iterator iter=map.entrySet().iterator();
		
        out.write('{');
		while(iter.hasNext()){
            if(first)
                first = false;
            else
                out.write(',');
			Map.Entry entry=(Map.Entry)iter.next();
            out.write('\"');
            out.write(escape(String.valueOf(entry.getKey())));
            out.write('\"');
            out.write(':');
			JSONValue.writeJSONString(entry.getValue(), out);
		}
		out.write('}');
	}

	public void writeJSONString(Writer out) throws IOException{
		writeJSONString(this, out);
	}

	public static String toJSONString(Map map){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(map, writer);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen with a StringWriter
			throw new RuntimeException(e);
		}
	}

	public String toJSONString(){
		return toJSONString(this);
	}

	public String toString(){
		return toJSONString();
	}

	public static String toString(String key,Object value){
        StringBuffer sb = new StringBuffer();
        sb.append('\"');
        if(key == null)
            sb.append("null");
        else
            JSONValue.escape(key, sb);
		sb.append('\"').append(':');
		
		sb.append(JSONValue.toJSONString(value));
		
		return sb.toString();
	}
	
	/**
	 * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
	 * It's the same as JSONValue.escape() only for compatibility here.
	 * 
	 * @see JSONValue#escape(String)
	 * 
	 * @param s
	 * @return
	 */
	public static String escape(String s){
		return JSONValue.escape(s);
	}

	public String getString(String key) {
		return (String) get(key);
	}

	public long getLong(String key) {
		return (long) get(key);
	}

	public int getInt(String key) {
		return (int) get(key);
	}

	public float getFloat(String key) {
		return ((Double) get(key)).floatValue();
	}

	public double getDouble(String key) {
		return (double) get(key);
	}

	public JSONObject getObject(String key) {
		return (JSONObject) get(key);
	}

	public JSONArray getArray(String key) {
		return (JSONArray) get(key);
	}

	public boolean getBoolean(String key) {
		return (boolean) get(key);
	}

	public String toPrettyJsonString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(toJSONString()));
	}
}
