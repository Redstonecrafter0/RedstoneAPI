package net.redstonecraft.redstoneapi.ipc.request;

import net.redstonecraft.redstoneapi.json.JSONObject;

public abstract class Request {

    abstract public String getPacketName();

    abstract public JSONObject getPayload();

}
