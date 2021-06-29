package me.hardstyles.blitz.kits.gui;

import me.hardstyles.blitz.Core;
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

public class SlotGui implements Listener {
    final private Core core;
    final private String name = "Edit Layouts";

    private final int[] slots = {10, 11, 12, 14, 15, 16};

    public SlotGui(Core core) {
        this.core = core;
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, name);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name(" ").make());
        }

        int index = 1;
        for(int i : slots){
            inv.setItem(i, new ItemBuilder(Material.BOOK).amount(index).name(ChatColor.GREEN + "Kit #" + index).lore(" ").lore("§8(§7Click to edit§8)").make());
            index++;
        }

        p.openInventory(inv);
    }


    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(name)) {
            return;
        }
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null) return;

        int slot = e.getSlot();

        if(item.getType() == Material.BOOK) {
            int index = 1;
            for(int i : slots){
                if(slot == i){
                    core.getLayoutGui().open((Player) e.getWhoClicked(), index);
                    return;
                }
                index++;
            }
        }
    }

    @EventHandler
    public void onDrag(final InventoryDragEvent e) {
        if (e.getView().getTitle().equals(name)) {
            e.setCancelled(true);
        }

    }
}
