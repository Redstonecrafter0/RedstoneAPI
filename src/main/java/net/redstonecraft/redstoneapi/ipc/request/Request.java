package net.redstonecraft.redstoneapi.ipc.request;

import net.redstonecraft.redstoneapi.json.JSONObject;

public abstract class Request {

    public final JSONObject payload;

    public Request(JSONObject payload) {
        this.payload = payload;
    }

    abstract public String getPacketName();

}
