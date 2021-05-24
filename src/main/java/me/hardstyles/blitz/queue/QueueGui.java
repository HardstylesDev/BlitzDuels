package me.hardstyles.blitz.queue;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
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

public class QueueGui implements Listener {
    final private Core core;
    final private Inventory inv;
    public QueueGui(Core core) {
        this.core = core;
        inv = Bukkit.createInventory(null, 9, ChatColor.GRAY + "Queue Selector");

        inv.setItem(3, new ItemBuilder(Material.IRON_SWORD).name("&eSolo Queue").lore("§7Click here to join the solo queue").make());
        inv.setItem(5, new ItemBuilder(Material.IRON_SWORD).name("&eTeams Queue").lore("§cSoon").amount(2).make());


    }

    public void open(Player p){


        ItemStack solo = new ItemStack(Material.IRON_SWORD,1);
        ItemMeta soloMeta = solo.getItemMeta();
        soloMeta.setDisplayName(ChatColor.YELLOW + "Solo Queue");
        List<String> loreList = new ArrayList<String>();
        loreList.add(ChatColor.GRAY + "In queue: " + ChatColor.WHITE + core.getQueueManager().getQueues().get(QueueType.NORMAL).size());
        loreList.add(ChatColor.GRAY + "In match: " + ChatColor.WHITE + core.getMatchManager().getMatchCount());
        loreList.add("");
        loreList.add(ChatColor.GRAY +"Click here to join!");
        soloMeta.setLore(loreList);
        solo.setItemMeta(soloMeta);

        inv.setItem(3, solo);

        p.openInventory(inv);

    }


    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().getName().equalsIgnoreCase(inv.getName())){
            return;
        }

        e.setCancelled(true);


        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        final ItemStack clickedItem = e.getCurrentItem();


        final Player p = (Player) e.getWhoClicked();

        if(clickedItem.getItemMeta().getDisplayName().contains("Solo Queue")){

            core.getQueueManager().add(QueueType.NORMAL, p);
            open(p);
            return;
        }
        else if(clickedItem.getItemMeta().getDisplayName().contains("Teams Queue")){

            IPlayer iPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());
            if(iPlayer.getParty() == null){
                p.sendMessage(ChatColor.RED + "You can only queue Teams with a party.");
                return;
            }
            if(iPlayer.getParty().getMembers().size() != 2){
                p.sendMessage(ChatColor.RED + "You can only queue Teams with a party of 2 players.");
                return;
            }
            if(iPlayer.getParty().getOwner() != p.getUniqueId()){
                p.sendMessage(ChatColor.RED + "You must be the owner of the party to join the Teams queue");
                return;
            }
            p.sendMessage(ChatColor.YELLOW + "Coming soon...");
            return;
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(inv.getName())){
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
