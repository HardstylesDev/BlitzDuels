package me.hardstyles.blitz.kits;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class IItem {
    public final int index;
    public final ItemStack item;
    public final int price;

    public IItem(int index, ItemStack itemStack, int price) {
        this.index = index;

        this.price = price;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(Arrays.asList(ChatColor.GOLD + "Cost: " + price + " points"));
        itemStack.setItemMeta(itemMeta);
        this.item = itemStack;

    }
}
