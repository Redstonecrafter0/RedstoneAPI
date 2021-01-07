package net.redstonecraft.redstoneapi.ipc.interfaces;

import net.redstonecraft.redstoneapi.ipc.response.Response;
import net.redstonecraft.redstoneapi.json.JSONObject;

public interface ServerProcessor {

    public Response onProcess(JSONObject payload);

    public String getPacketName();

}
