package net.redstonecraft.redstoneapi.bukkit.events;

import net.redstonecraft.redstoneapi.bukkit.manager.GuiInventoryManager;
import net.redstonecraft.redstoneapi.bukkit.obj.GuiInventory;
import net.redstonecraft.redstoneapi.bukkit.obj.GuiOptionStates;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This event is fired when an inventory from the {@link GuiInventoryManager} is used
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class GuiInventoryClickEvent {

    private final Player player;
    private final InventoryAction clickAction;
    private final int slot;
    private final GuiOptionStates guiOptionStates;
    private final GuiInventory guiInventory;
    private final Inventory inventory;

    public GuiInventoryClickEvent(Player player, InventoryAction clickAction, int slot, Inventory inventory) {
        this.player = player;
        this.clickAction = clickAction;
        this.slot = slot;
        guiOptionStates = null;
        guiInventory = null;
        this.inventory = inventory;
    }

    public GuiInventoryClickEvent(GuiInventoryClickEvent event, GuiOptionStates guiOptionStates, GuiInventory guiInventory) {
        this.player = event.player;
        this.clickAction = event.clickAction;
        this.slot = event.slot;
        this.guiOptionStates = guiOptionStates;
        this.guiInventory = guiInventory;
        this.inventory = event.inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryAction getClickAction() {
        return clickAction;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return inventory.getItem(slot);
    }

    public GuiOptionStates getGuiOptionStates() {
        return guiOptionStates;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public GuiInventory getGuiInventory() {
        return guiInventory;
    }

}
