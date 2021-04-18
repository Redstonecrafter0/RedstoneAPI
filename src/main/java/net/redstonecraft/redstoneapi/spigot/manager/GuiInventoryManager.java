package net.redstonecraft.redstoneapi.spigot.manager;

import net.redstonecraft.redstoneapi.spigot.events.GuiInventoryClickEvent;
import net.redstonecraft.redstoneapi.spigot.obj.GuiInventory;
import net.redstonecraft.redstoneapi.spigot.plugin.RedstoneAPISpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages instances of {@link GuiInventory}
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class GuiInventoryManager {

    private List<GuiInventory> inventories = new ArrayList<>();

    public GuiInventoryManager() {
        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onInventoryOpen(InventoryOpenEvent event) {
                inventories.stream().filter(inventory -> inventory.getFor((Player) event.getPlayer()) != null).forEach(inventory -> inventory.openGui(event));
            }

            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (inventories.stream().anyMatch(inventory -> inventory.getFor((Player) event.getWhoClicked()) != null)) {
                    inventories.stream().filter(inventory -> inventory.getFor((Player) event.getWhoClicked()) != null).forEach(inventory -> inventory.click(new GuiInventoryClickEvent(((Player) event.getWhoClicked()), event.getAction(), event.getSlot(), event.getInventory())));
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent event) {
                inventories.stream().filter(inventory -> inventory.getFor((Player) event.getPlayer()) != null).forEach(inventory -> {
                    inventory.closeGui(event);
                    inventory.getInventories().remove((Player) event.getPlayer());
                });
            }

        }, RedstoneAPISpigot.getInstance());
    }

    public void addInventory(GuiInventory inventory) {
        inventories.add(inventory);
    }

    public void removeInventory(GuiInventory inventory) {
        inventories.remove(inventory);
    }

}
