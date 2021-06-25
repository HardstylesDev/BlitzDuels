package me.hardstyles.blitz.kits.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.kits.IItem;
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

import java.util.*;

public class LayoutGui implements Listener {
    private final Core core;
    private final Map<UUID, Integer> cache = new HashMap<>();
    private final String name = ChatColor.GRAY + "Equipment Selector";

    public LayoutGui(Core core) {
        this.core = core;
    }

    public void open(Player p, int index) {
        cache.put(p.getUniqueId(), index);
        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.GRAY + "Equipment Selector");
        for (int i = 0; i < (9 * 6); i++) {
            inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(9).name("&e").make());
        }



        inv.setItem(10, core.getItemHandler().getHelmets().get(0).item);
        inv.setItem(19, core.getItemHandler().getChestplates().get(0).item);
        inv.setItem(28, core.getItemHandler().getLeggings().get(0).item);
        inv.setItem(38, core.getItemHandler().getBows().get(0).item);
        inv.setItem(12, core.getItemHandler().getWeapons().get(0).item);
        inv.setItem(13, core.getItemHandler().getProjectiles().get(0).item);
        inv.setItem(22, core.getItemHandler().getArrows().get(0).item);
        inv.setItem(14, core.getItemHandler().getBows().get(0).item);
        inv.setItem(16, core.getItemHandler().getPotions().get(0).item);
        inv.setItem(25, core.getItemHandler().getPotions().get(0).item);
        inv.setItem(34, core.getItemHandler().getPotions().get(0).item);
        inv.setItem(43, core.getItemHandler().getPotions().get(0).item);

        inv.setItem(39, core.getItemHandler().getMobs().get(0).item);
        inv.setItem(41, core.getItemHandler().getMobs().get(0).item);


        inv.setItem(49, new ItemBuilder(Material.DOUBLE_PLANT).name("&6Points: &a100").make());
        inv.setItem(53, new ItemBuilder(Material.WOOL).durability(5).name("&aClick to save this layout").make());
        inv.setItem(45, new ItemBuilder(Material.WOOL).durability(14).name("&cClick to exit").make());

        p.openInventory(inv);
    }


    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().getName().equalsIgnoreCase(name)) {
            return;
        }
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        final ItemStack clickedItem = e.getCurrentItem();
        final int slot = e.getRawSlot();

        if (slot == 45) {
            e.getWhoClicked().closeInventory();
        } else if (slot == 53) {
            if (clickedItem.getDurability() != 5) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points to save this layout");
            } else {
                e.getWhoClicked().closeInventory();
            }
        } else {
            handleClick(e);

            int points = getUsedPoints(e.getInventory());
            e.getInventory().setItem(49, new ItemBuilder(Material.DOUBLE_PLANT).name("&6Points: " + (points > 100 ? ChatColor.RED : ChatColor.GREEN) + (100 - points)).make());
            if (points > 100) {
                e.getInventory().setItem(53, new ItemBuilder(Material.WOOL).durability(7).name("&cYou've gone over the 100 point limit").make());
            } else {
                e.getInventory().setItem(53, new ItemBuilder(Material.WOOL).durability(5).name("&aClick to save this layout").make());
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals(name)) {
            if (getUsedPoints(e.getInventory()) <= 100) {
                JsonArray jsonArray = new JsonArray();
                int[] slots = {10, 19, 28, 37, 12, 13, 14, 22, 16, 25, 34, 43, 39, 41};
                for (int i : slots) {
                    if (e.getInventory().getItem(i).getType() != Material.BARRIER) {
                        jsonArray.add(new JsonPrimitive(core.getItemSerializer().getStringFromItem(e.getInventory().getItem(i))));
                    }
                }

                IPlayer p = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
                p.getLayouts().put(cache.get(p.getUuid()), jsonArray);
                core.getStatisticsManager().saveAsync(p);
                e.getPlayer().sendMessage("§aSaved layout §7(" + cache.get(p.getUuid()) + ")");
            } else {
                e.getPlayer().sendMessage("§cKit was not saved as it was more than 100 points.");
            }
        }
    }

    @EventHandler
    public void darn(final InventoryDragEvent e) {
        if (e.getInventory().getTitle().equalsIgnoreCase(name)) {
            e.setCancelled(true);
        }
    }



    private int getUsedPoints(Inventory inv) {
        int points = 0;
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getItemMeta() == null)
                continue;
            if (inv.getItem(i).getItemMeta().getLore() == null || inv.getItem(i).getItemMeta().getLore().isEmpty())
                continue;

            String firstLine = inv.getItem(i).getItemMeta().getLore().get(0);
            if (firstLine.contains("points")) {
                points += Integer.parseInt(firstLine.substring(8, firstLine.length() - 7));

            }
        }
        return points;
    }

    private List<IItem> getCategory(int slot) {
        switch (slot) {
            case 10: {
                return core.getItemHandler().getHelmets();
            }
            case 19: {
                return core.getItemHandler().getChestplates();
            }
            case 28: {
                return core.getItemHandler().getLeggings();
            }
            case 37: {
                return core.getItemHandler().getBoots();
            }
            case 12: {
                return core.getItemHandler().getWeapons();
            }
            case 13: {
                return core.getItemHandler().getProjectiles();
            }
            case 14: {
                return core.getItemHandler().getBows();
            }
            case 22: {
                return core.getItemHandler().getArrows();
            }
            case 43:
            case 34:
            case 25:
            case 16: {
                return core.getItemHandler().getPotions();
            }
            case 41:
            case 39: {
                return core.getItemHandler().getMobs();
            }
        }
        return null;
    }

    private void handleClick(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        if (e.getRawSlot() < e.getInventory().getSize()) {
            List<IItem> items = getCategory(e.getSlot());

            if (items != null) {
                if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                    e.getInventory().setItem(e.getSlot(), core.getItemHandler().previous(items, clickedItem).item);
                }
                if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                    e.getInventory().setItem(e.getSlot(), core.getItemHandler().next(items, clickedItem).item);
                }
            }
        }
    }
}

