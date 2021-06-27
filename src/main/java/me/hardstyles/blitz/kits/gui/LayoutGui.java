package me.hardstyles.blitz.kits.gui;

import com.google.common.collect.Lists;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.kits.IItem;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LayoutGui implements Listener {
    private final Core core;
    private final Map<UUID, Integer> indexCache = new HashMap<>();
    private final Map<UUID, Map<Integer, IItem>> layoutCache = new HashMap<>();
    private final String name = ChatColor.GRAY + "Equipment Selector";
    private final int[] slots = {10, 19, 28, 37, 12, 13, 14, 22, 16, 25, 34, 43, 39, 41};

    public LayoutGui(Core core) {
        this.core = core;
    }

    public void open(Player p, int index) {
        indexCache.put(p.getUniqueId(), index);
        layoutCache.put(p.getUniqueId(), new HashMap<>());
        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.GRAY + "Equipment Selector");
        for (int i = 0; i < (9 * 6); i++) {
            inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(9).name(" ").make());
        }

        IPlayer iPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());

        if (iPlayer.getLayouts().containsKey(index)) {
            String[] layout = iPlayer.getLayouts().get(index).split(";");
            for (int i = 0; i < layout.length; i++) {
                IItem item;
                try {
                    item = IItem.valueOf(layout[i]);
                } catch (IllegalArgumentException e) {
                    System.out.println("non-existing ID found in " + iPlayer.getUuid() + " layout #" + index);
                    layoutCache.get(p.getUniqueId()).put(slots[i], IItem.BLANK);
                    continue;
                }
                ItemStack itemStack = item.getItem().clone();
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(Lists.newArrayList(" ", ChatColor.GRAY + "Points: " + ChatColor.GREEN + item.getPrice()));
                itemStack.setItemMeta(meta);
                inv.setItem(slots[i], itemStack);
                layoutCache.get(p.getUniqueId()).put(slots[i], item);
            }
        } else {
            ItemStack itemStack = IItem.BLANK.getItem().clone();
            ItemMeta meta = itemStack.getItemMeta();
            meta.setLore(Lists.newArrayList(" ", ChatColor.GRAY + "Points: " + ChatColor.GREEN + IItem.BLANK.getPrice()));
            itemStack.setItemMeta(meta);
            for (int i : slots) {
                inv.setItem(i, itemStack);
                layoutCache.get(p.getUniqueId()).put(i, IItem.BLANK);
            }
        }

        int points = getUsedPoints(p.getUniqueId());
        inv.setItem(49, new ItemBuilder(Material.DOUBLE_PLANT).name("&6Points: " + (points > 100 ? ChatColor.RED : ChatColor.GREEN) + (100 - points)).make());
        inv.setItem(53, new ItemBuilder(Material.WOOL).durability(5).name("&aClick to save this layout").make());
        inv.setItem(45, new ItemBuilder(Material.WOOL).durability(14).name("&cClick to delete").make());

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
            Map<Integer, IItem> layout = layoutCache.get(e.getWhoClicked().getUniqueId());
            for (int i : slots) {
                layout.put(i, IItem.BLANK);
            }
            e.getWhoClicked().closeInventory();
        } else if (slot == 53) {
            if (clickedItem.getDurability() != 5) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points to save this layout");
            } else {
                e.getWhoClicked().closeInventory();
            }
        } else {
            handleClick(e);

            int points = getUsedPoints(e.getWhoClicked().getUniqueId());
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
            Map<Integer, IItem> layout = layoutCache.get(e.getPlayer().getUniqueId());
            if (layout.values().stream().allMatch(item -> item == IItem.BLANK)) {
                IPlayer p = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
                p.getLayouts().remove(indexCache.get(p.getUuid()));
                core.getStatisticsManager().saveAsync(p);
                e.getPlayer().sendMessage("§cDeleted layout §7(#" + indexCache.get(p.getUuid()) + ")");
                ((Player)e.getPlayer()).playSound(e.getPlayer().getLocation(), Sound.WOOD_CLICK, 1, 1);

            } else if (getUsedPoints(e.getPlayer().getUniqueId()) <= 100) {
                int index = 0;
                StringBuilder builder = new StringBuilder(layout.get(slots[index++]).name());
                for (int i = 1; i < layout.size(); i++) {
                    builder.append(";").append(layout.get(slots[index++]).name());
                }

                IPlayer p = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
                p.getLayouts().put(indexCache.get(p.getUuid()), builder.toString());
                core.getStatisticsManager().saveAsync(p);
                e.getPlayer().sendMessage("§aSaved layout §7(#" + indexCache.get(p.getUuid()) + ")");
                ((Player)e.getPlayer()).playSound(e.getPlayer().getLocation(), Sound.WOOD_CLICK, 1, 1);
            } else {
                e.getPlayer().sendMessage("§cKit could not be saved.");
            }
        }
    }

    @EventHandler
    public void darn(final InventoryDragEvent e) {
        if (e.getInventory().getTitle().equalsIgnoreCase(name)) {
            e.setCancelled(true);
        }
    }



    private int getUsedPoints(UUID uuid) {
        int points = 0;
        Map<Integer, IItem> layout = layoutCache.get(uuid);
        for (IItem item : layout.values()) {
            points += item.getPrice();
        }
        return points;
    }

    private IItem.Type getCategory(int slot) {
        switch (slot) {
            case 10: {
                return IItem.Type.HELMET;
            }
            case 19: {
                return IItem.Type.CHESTPLATE;
            }
            case 28: {
                return IItem.Type.LEGGINGS;
            }
            case 37: {
                return IItem.Type.BOOTS;
            }
            case 12: {
                return IItem.Type.WEAPON;
            }
            case 13: {
                return IItem.Type.PROJECTILE;
            }
            case 14: {
                return IItem.Type.BOW;
            }
            case 22: {
                return IItem.Type.ARROW;
            }
            case 43:
            case 34:
            case 25:
            case 16: {
                return IItem.Type.CONSUMABLE;
            }
            case 41:
            case 39: {
                return IItem.Type.MOB;
            }
        }
        return null;
    }

    private void handleClick(InventoryClickEvent e) {
        if (e.getRawSlot() < e.getInventory().getSize()) {
            if (layoutCache.get(e.getWhoClicked().getUniqueId()).containsKey(e.getSlot())) {
                IItem.Type type = getCategory(e.getSlot());
                if (type == null) {
                    return;
                }
                IItem cycle = layoutCache.get(e.getWhoClicked().getUniqueId()).get(e.getSlot());

                if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                    cycle = cycle.previous(type);
                } else if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                    cycle = cycle.next(type);
                } else {
                    return;
                }
                layoutCache.get(e.getWhoClicked().getUniqueId()).put(e.getSlot(), cycle);

                ItemStack item = cycle.getItem().clone();
                ItemMeta meta = item.getItemMeta();
                meta.setLore(Lists.newArrayList(" ", ChatColor.GRAY + "Points: " + ChatColor.GREEN + cycle.getPrice()));
                item.setItemMeta(meta);
                e.getInventory().setItem(e.getSlot(), item);
            }
        }
    }
}

