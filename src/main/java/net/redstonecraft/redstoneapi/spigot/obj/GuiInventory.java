package net.redstonecraft.redstoneapi.spigot.obj;

import net.redstonecraft.redstoneapi.spigot.events.GuiInventoryClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Use this class for custom Gui inventories with clickable items
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class GuiInventory {

    private final Map<Player, Inventory> inventories = new HashMap<>();
    private final Map<Integer, GuiOnClick> options = new HashMap<>();
    private final Map<Integer, GuiOptionStates> optionStates = new HashMap<>();
    private GuiOnOpen guiOnOpen;
    private GuiOnClose guiOnClose;
    private final int rows;
    private final String title;

    public GuiInventory(String title, int rows, GuiOnOpen guiOnOpen, GuiOnClose guiOnClose) {
        this.rows = rows;
        this.title = title;
        this.guiOnOpen = guiOnOpen == null ? (event) -> {} : guiOnOpen;
        this.guiOnClose = guiOnClose == null ? (event) -> {} : guiOnClose;
    }

    /**
     * Set what happenes when the inventory gets openned (e.g. set the current options)
     *
     * @param onGuiOpen the eventhandler
     * */
    public void setOnGuiOpen(GuiOnOpen onGuiOpen) {
        this.guiOnOpen = guiOnOpen == null ? (event) -> {} : guiOnOpen;
    }

    /**
     * Set what happens when the inventory gets closed (e.g. store settings)
     *
     * @param guiOnClose the eventhandler
     * */
    public void setOnGuiClose(GuiOnClose guiOnClose) {
        this.guiOnClose = guiOnClose == null ? (event) -> {} : guiOnClose;
    }

    public void openGui(InventoryOpenEvent event) {
        guiOnOpen.onOpen(event);
    }

    public void closeGui(InventoryCloseEvent event) {
        guiOnClose.onClose(event);
    }

    public int size() {
        return rows * 9;
    }

    public String getTitle() {
        return title;
    }

    public void click(GuiInventoryClickEvent event) {
        if (options.get(event.getSlot()) != null) {
            options.get(event.getSlot()).onClick(new GuiInventoryClickEvent(event, optionStates.get(event.getSlot()), this));
        }
    }

    /**
     * Set an option with a default state and a {@link Map} containing all stated possible with the {@link ItemStack} for each state
     *
     * @param <T> the value the option uses
     * @param option the position and click handler
     * @param states all possible states
     * @param defaultState the default state
     *
     * @throws IndexOutOfBoundsException when the slot id is higher than possible
     * */
    public <T> void setOption(Option option, Map<T, ItemStack> states, T defaultState) {
        ItemStack defaultItem = states.get(defaultState);
        if (option.slot > rows * 9) {
            throw new IndexOutOfBoundsException("Slot id too high for this GuiInventory");
        }
        options.put(option.slot, option.guiOnClick);
        optionStates.put(option.slot, new GuiOptionStates(states, defaultState));
    }

    public void openFor(Player player) {
        Inventory inventory = Bukkit.createInventory(null, rows * 9, title);
        optionStates.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
            ItemStack item = ((Map<Object, ItemStack>) entry.getValue().getStates()).get(entry.getValue().getDefaultState());
            if (item != null) {
                inventory.setItem(entry.getKey(), item);
            }
        });
        inventories.put(player, inventory);
        player.openInventory(inventory);
    }

    public Inventory getFor(Player player) {
        return inventories.get(player);
    }

    public Map<Player, Inventory> getInventories() {
        return inventories;
    }

    /**
     * Set an option with a default state and a {@link Map} containing all stated possible with the {@link ItemStack} for each state in a {@link GuiOptionStates} object
     *
     * @param option the position and click handler
     * @param guiOptionStates the option states and the default state
     *
     * @throws IndexOutOfBoundsException when the slot id is higher than possible
     * */
    public void setOption(Option option, GuiOptionStates guiOptionStates) {
        ItemStack defaultItem = (ItemStack) guiOptionStates.getStates().get(guiOptionStates.getDefaultState());
        if (option.slot > size()) {
            throw new IndexOutOfBoundsException("Slot id too high for this GuiInventory");
        }
        options.put(option.slot, option.guiOnClick);
        optionStates.put(option.slot, guiOptionStates);
    }

    public static class Option {

        private final int slot;
        private final GuiOnClick guiOnClick;

        public Option(int slotX, int slotY, GuiOnClick guiOnClick) {
            slot = (slotY * 9) + slotX;
            this.guiOnClick = guiOnClick;
        }

    }

    public interface GuiOnOpen {

        public void onOpen(InventoryOpenEvent event);

    }

    public interface GuiOnClick {

        public void onClick(GuiInventoryClickEvent event);

    }

    public interface GuiOnClose {

        public void onClose(InventoryCloseEvent event);

    }

}
