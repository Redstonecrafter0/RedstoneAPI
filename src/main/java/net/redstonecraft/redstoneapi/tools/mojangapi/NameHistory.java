package net.redstonecraft.redstoneapi.tools.mojangapi;

public class NameHistory {

    public final String name;
    public final Long changedToAt;

    public NameHistory(String name, Long changedToAt) {
        this.name = name;
        this.changedToAt = changedToAt;
    }

    public NameHistory(String name) {
        this.name = name;
        this.changedToAt = null;
    }

}
