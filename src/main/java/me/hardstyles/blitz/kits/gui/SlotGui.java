package me.hardstyles.blitz.kits.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class SlotGui implements Listener {
    final private Core core;
    final private String name = ChatColor.GRAY + "Confirm Equipment";

    private int slot1 = 10;
    private int slot2 = 11;
    private int slot3 = 12;
    private int slot4 = 14;
    private int slot5 = 15;
    private int slot6 = 16;

    public SlotGui(Core core) {
        this.core = core;


    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, name);
        for (int i = 0; i < (9 * 3); i++) {
            inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(8).name("&e").make());
        }

        int index = 1;
        for(int i : Arrays.asList(slot1,slot2,slot3,slot4,slot5,slot6)){
            inv.setItem(i, new ItemBuilder(Material.INK_SACK).durability(8).amount(index).make());
            index++;
        }
        index = 1;
        IPlayer iPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());

        for(int i : Arrays.asList(slot1,slot2,slot3,slot4,slot5,slot6)){
            if(iPlayer.getLayouts().containsKey(index)){
                inv.setItem(i, new ItemBuilder(Material.BOOK).amount(index).name(ChatColor.GOLD + "Kit #" + index).make());
            }
            index++;
        }

        p.openInventory(inv);
    }


    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        System.out.println(e.getInventory().getTitle() + " - " + name);

        if (!e.getInventory().getName().equalsIgnoreCase(name)) {
            return;
        }
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        final ItemStack clickedItem = e.getCurrentItem();
        final int slot = e.getSlot();
        if(clickedItem.getType() != Material.INK_SACK && clickedItem.getType() != Material.BOOK){
            return;
        }
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

        int index = 1;
        for(int i : Arrays.asList(slot1,slot2,slot3,slot4,slot5,slot6)){
            if(slot == i){
                iPlayer.getLayouts().put(index, iPlayer.getEditingLayout());
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Saved layout! " + ChatColor.GRAY + "(" + index + ChatColor.GRAY + ")");
                e.getWhoClicked().closeInventory();
                core.getStatisticsManager().saveAsync(iPlayer);
                return;
            }
            index++;
        }

    }

    @EventHandler
    public void onDrag(final InventoryDragEvent e) {
        if (e.getInventory().getTitle().equalsIgnoreCase(name)) {
            e.setCancelled(true);
        }

    }
    @EventHandler
    public void onClose(final InventoryCloseEvent e){
        if(e.getInventory().getTitle().equalsIgnoreCase(name)){
            core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId()).setEditingLayout(null);
        }
    }
}
