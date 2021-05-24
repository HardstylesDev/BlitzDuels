package me.hardstyles.blitz.kits;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.queue.QueueType;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitGui implements Listener {
    final private Core core;
    final private String name = ChatColor.GRAY + "Equipment Selector";
    public int potion1 = 15;
    public int potion2 = 15+9;
    public int potion3 = 15+9+9;
    public int potion4 = 15+9+9+9;
    public KitGui(Core core) {
        this.core = core;


    }

    public void open(Player p){
        Inventory inv  = Bukkit.createInventory(null, 9*6, ChatColor.GRAY + "Equipment Selector");
        for (int i = 0; i < (9*6); i++) {
            inv.setItem(i,new ItemBuilder(Material.STAINED_GLASS_PANE).durability(9).name("&e").make());
        }
        p.openInventory(inv);
    }


    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().getName().equalsIgnoreCase(name)){
            return;
        }

        e.setCancelled(true);


        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        final ItemStack clickedItem = e.getCurrentItem();



    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        System.out.println(e.getInventory().getTitle() + " - " + name);
        if (e.getInventory().getTitle().equalsIgnoreCase(name)){
            e.setCancelled(true);
        }
    }


    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }


}
