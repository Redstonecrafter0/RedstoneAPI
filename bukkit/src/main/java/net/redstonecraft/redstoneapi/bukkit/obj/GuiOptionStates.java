package net.redstonecraft.redstoneapi.bukkit.obj;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Container for setting a default state and a {@link Map} containing all stated possible with the {@link ItemStack} for each state
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class GuiOptionStates<T> {

    private final Map<T, ItemStack> states;
    private final T defaultState;

    public GuiOptionStates(Map<T, ItemStack> states, T defaultState) {
        this.states = states;
        this.defaultState = defaultState;
    }

    public Map<T, ItemStack> getStates() {
        return states;
    }

    public T getDefaultState() {
        return defaultState;
    }

}
