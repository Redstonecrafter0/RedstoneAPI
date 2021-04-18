package net.redstonecraft.redstoneapi.spigot.helper;

import net.redstonecraft.redstoneapi.json.JSONArray;
import net.redstonecraft.redstoneapi.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Use this class when you want to store and restore an {@link Inventory} or an {@link ItemStack} and its properties
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class InventoryHelper {

    public static JSONObject serializeItemMeta(ItemMeta itemMeta) {
        JSONObject obj = new JSONObject();
        obj.put("displayName", itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null);
        JSONArray lore = new JSONArray();
        if (itemMeta.hasLore()) {
            lore.addAll(itemMeta.getLore());
        }
        obj.put("lore", lore);
        JSONArray flags = new JSONArray();
        for (ItemFlag flag : itemMeta.getItemFlags()) {
            flags.add(flag.name());
        }
        obj.put("flags", flags);
        try {
            obj.put("unbreakable", itemMeta.spigot().isUnbreakable());
        } catch (UnsupportedOperationException ignored) {
        }
        return obj;
    }

    public static void deserializeItemMeta(JSONObject obj, ItemMeta base) {
        base.setDisplayName(obj.getString("displayName"));
        base.setLore(new ArrayList<>(obj.getArray("lore")));
        base.addItemFlags(((List<ItemFlag>) obj.getArray("flags").stream().map(i -> ItemFlag.valueOf((String) i)).collect(Collectors.toList())).toArray(new ItemFlag[0]));
    }

    public static JSONObject serializeItemStack(ItemStack itemStack) {
        JSONObject obj = new JSONObject();
        obj.put("type", itemStack.getType().name());
        obj.put("amount", itemStack.getAmount());
        obj.put("durability", (int) itemStack.getDurability());
        JSONObject enchantments = new JSONObject();
        for (Map.Entry<Enchantment, Integer> i : itemStack.getEnchantments().entrySet()) {
            enchantments.put(i.getKey().getName(), i.getValue());
        }
        obj.put("enchantments", enchantments);
        obj.put("meta", serializeItemMeta(itemStack.getItemMeta()));
        return obj;
    }

    public static ItemStack deserializeItemStack(JSONObject obj) {
        ItemStack itemStack = new ItemStack(Material.valueOf(obj.getString("type")));
        deserializeItemMeta(obj.getObject("meta"), itemStack.getItemMeta());
        itemStack.setAmount(obj.getInt("amount"));
        itemStack.setDurability((short) obj.getInt("durability"));
        obj.getObject("enchantments").forEach((key, value) -> itemStack.addEnchantment(Enchantment.getByName((String) key), (int) value));
        return itemStack;
    }

    public static JSONObject serializeInventory(Inventory inventory) {
        JSONObject obj = new JSONObject();
        obj.put("size", inventory.getSize());
        obj.put("maxStackSize", inventory.getMaxStackSize());
        obj.put("title", inventory.getTitle());
        JSONObject items = new JSONObject();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null && !inventory.getItem(i).getType().equals(Material.AIR)) {
                items.put(String.valueOf(i), serializeItemStack(inventory.getItem(i)));
            }
        }
        obj.put("items", items);
        return obj;
    }

    public static Inventory deserializeInventory(JSONObject obj) {
        Inventory inventory = Bukkit.createInventory(null, obj.getInt("size"), obj.getString("title"));
        inventory.setMaxStackSize(obj.getInt("maxStackSize"));
        obj.getObject("items").forEach((key, value) -> inventory.setItem(Integer.parseInt((String) key), deserializeItemStack((JSONObject) value)));
        return inventory;
    }

}
