package net.redstonecraft.redstoneapi.ipc.request;

import net.redstonecraft.redstoneapi.json.JSONObject;

public interface Request {

    String getPacketName();

    JSONObject getPayload();

}
