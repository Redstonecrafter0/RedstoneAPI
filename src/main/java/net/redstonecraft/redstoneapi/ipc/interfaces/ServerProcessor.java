package net.redstonecraft.redstoneapi.ipc.interfaces;

import net.redstonecraft.redstoneapi.ipc.response.Response;
import net.redstonecraft.redstoneapi.json.JSONObject;

/**
 * The base class for processing client requests
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public abstract class ServerProcessor {

    /**
     * Method called when a client requests is incomming
     *
     * @param payload the payload provided by the client request that should contain data that has to be processed
     *
     * @return a {@link Response} object that contains payload and a {@link net.redstonecraft.redstoneapi.ipc.response.ResponseStatus} that will be sent back to the client
     * */
    abstract public Response onProcess(JSONObject payload);

    /**
     * The packet name to identify the {@link ServerProcessor}
     *
     * @return packetname
     * */
    abstract public String getPacketName();

}
