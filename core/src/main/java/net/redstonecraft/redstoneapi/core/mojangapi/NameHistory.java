package net.redstonecraft.redstoneapi.core.mojangapi;

import net.redstonecraft.redstoneapi.core.MojangAPI;

/**
 * NameHistory object for the {@link MojangAPI} lookup
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class NameHistory {

    private final String name;
    private final Long changedToAt;

    /**
     * Contructor for the namehistory
     *
     * @param name playername
     * @param changedToAt timestamp
     * */
    public NameHistory(String name, Long changedToAt) {
        this.name = name;
        this.changedToAt = changedToAt;
    }

    /**
     * Contructor for the namehistory
     *
     * @param name playername
     * */
    public NameHistory(String name) {
        this.name = name;
        this.changedToAt = null;
    }

    /**
     * Get the playername
     *
     * @return playername
     * */
    public String getName() {
        return name;
    }

    /**
     * Get the change timestamp
     *
     * @return timestamp
     * */
    public Long getChangedToAt() {
        return changedToAt;
    }

    @Override
    public String toString() {
        return "NameHistory{" +
                "name='" + name + '\'' +
                ", changedToAt=" + changedToAt +
                '}';
    }
}
