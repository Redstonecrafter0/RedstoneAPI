package net.redstonecraft.redstoneapi.ipc.response;

public enum ResponseStatus {

    SUCCESS("success"),
    ERROR("error"),
    WARNING("warning"),
    EMPTY("empty");

    public final String name;

    ResponseStatus(String name) {
        this.name = name;
    }

}
