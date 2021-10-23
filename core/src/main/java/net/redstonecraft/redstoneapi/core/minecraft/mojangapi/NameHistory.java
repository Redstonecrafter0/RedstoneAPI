package net.redstonecraft.redstoneapi.core.minecraft.mojangapi;

/**
 * NameHistory object for the {@link MojangAPI} lookup.
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
@SuppressWarnings("unused")
public class NameHistory {

    private final String name;
    private final Long changedToAt;

    NameHistory(String name, Long changedToAt) {
        this.name = name;
        this.changedToAt = changedToAt;
    }

    NameHistory(String name) {
        this.name = name;
        this.changedToAt = null;
    }

    /**
     * Get the player name
     *
     * @return player name
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
