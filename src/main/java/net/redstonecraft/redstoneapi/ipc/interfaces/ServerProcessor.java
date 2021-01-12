package net.redstonecraft.redstoneapi.ipc.interfaces;

import net.redstonecraft.redstoneapi.ipc.response.Response;
import net.redstonecraft.redstoneapi.json.JSONObject;

public abstract class ServerProcessor {

    abstract public Response onProcess(JSONObject payload);

    abstract public String getPacketName();

}
