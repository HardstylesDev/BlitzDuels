package me.hardstyles.blitz.kits.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class LayoutGui implements Listener {
    final private Core core;
    final private String name = ChatColor.GRAY + "Equipment Selector";
    private int potion1 = 16;
    private int potion2 = 16 + 9;
    private int potion3 = 16 + 9 + 9;
    private int potion4 = 16 + 9 + 9 + 9;
    private int coinSlot = 16 + 9 + 9 + 9 + 6;
    private int mobSlot1 = coinSlot - 10;
    private int mobSlot2 = coinSlot - 8;

    private int armorSlot1 = 10;
    private int armorSlot2 = 10 + 9;
    private int armorSlot3 = 10 + 9 + 9;
    private int armorSlot4 = 10 + 9 + 9 + 9;

    private int bowSlot = armorSlot1 + 4;
    private int arrowSlot = armorSlot1 + 3 + 9;
    private int projectileSlot = armorSlot1 + 3;
    private int weaponSlot = armorSlot1 + 2;

    private int confirmSlot = coinSlot + 4;
    private int exitslot = coinSlot - 4;

    public LayoutGui(Core core) {
        this.core = core;


    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.GRAY + "Equipment Selector");
        for (int i = 0; i < (9 * 6); i++) {
            inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(9).name("&e").make());
        }

        inv.setItem(armorSlot1, core.getItemHandler().helmets.get(0).item);
        inv.setItem(armorSlot2, core.getItemHandler().chestplates.get(0).item);
        inv.setItem(armorSlot3, core.getItemHandler().leggings.get(0).item);
        inv.setItem(armorSlot4, core.getItemHandler().boots.get(0).item);
        inv.setItem(weaponSlot, core.getItemHandler().weapons.get(0).item);
        inv.setItem(projectileSlot, core.getItemHandler().projectiles.get(0).item);
        inv.setItem(arrowSlot, core.getItemHandler().arrows.get(0).item);
        inv.setItem(bowSlot, core.getItemHandler().bows.get(0).item);
        inv.setItem(potion1, core.getItemHandler().potions.get(0).item);
        inv.setItem(potion2, core.getItemHandler().potions.get(0).item);
        inv.setItem(potion3, core.getItemHandler().potions.get(0).item);
        inv.setItem(potion4, core.getItemHandler().potions.get(0).item);

        inv.setItem(mobSlot1, core.getItemHandler().mobs.get(0).item);
        inv.setItem(mobSlot2, core.getItemHandler().mobs.get(0).item);


        inv.setItem(coinSlot, new ItemBuilder(Material.DOUBLE_PLANT).name("&6Points: &a0").make());
        inv.setItem(confirmSlot, new ItemBuilder(Material.WOOL).durability(5).name("&aClick to save this layout").make());
        inv.setItem(exitslot, new ItemBuilder(Material.WOOL).durability(14).name("&cClick to exit").make());

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
        final int slot = e.getSlot();

        if (slot == exitslot) {
            e.getWhoClicked().closeInventory();
            return;
        }
        if (slot == confirmSlot) {
            if (clickedItem.getDurability() != 5) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "You don't have enough points to save this layout");
                return;
            }


            ArrayList<String> serializedItems = new ArrayList<>();
            for (int i : Arrays.asList(armorSlot1, armorSlot2, armorSlot3, armorSlot4, weaponSlot, projectileSlot, arrowSlot, bowSlot, potion1, potion2, potion3, potion4, mobSlot1, mobSlot2)) {
                if (e.getInventory().getItem(i).getType() != Material.BARRIER) {
                    serializedItems.add(core.getItemSerializer().getStringFromItem(e.getInventory().getItem(i)));
                }
            }

            JsonArray jsonArray = new JsonArray();

            for (String serializedItem : serializedItems) {
                JsonPrimitive jsonPrimitive = new JsonPrimitive(serializedItem);
                jsonArray.add(jsonPrimitive);
            }

            core.getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId()).setEditingLayout(jsonArray);

            e.getWhoClicked().closeInventory();
            core.getSlotGui().open((Player) e.getWhoClicked());


        }

        handleClick(e);

        int points = getUsedPoints(e.getInventory());
        e.getInventory().setItem(coinSlot, new ItemBuilder(Material.DOUBLE_PLANT).name("&6Points: " + (points > 100 ? ChatColor.RED : ChatColor.GREEN) + (100 - points)).make());
        if (points > 100) {
            e.getInventory().setItem(coinSlot + 4, new ItemBuilder(Material.WOOL).durability(7).name("&cYou've gone over the 100 point limit").make());
        } else {
            e.getInventory().setItem(coinSlot + 4, new ItemBuilder(Material.WOOL).durability(5).name("&aClick to save this layout").make());
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
        for (int i = 0; i < (9 * 6); i++) {
            if (inv.getItem(i) == null)
                continue;
            if (inv.getItem(i).getItemMeta() == null)
                continue;
            if (inv.getItem(i).getItemMeta().getLore() == null || inv.getItem(i).getItemMeta().getLore().size() == 0)
                continue;
            String firstLine = inv.getItem(i).getItemMeta().getLore().get(0);
            if (firstLine.contains("points")) {
                points = points + Integer.parseInt(firstLine.replaceAll(ChatColor.GOLD + "Cost: ", "").replaceAll(" points", ""));

            }


        }
        return points;
    }

    private ArrayList<ItemStack> getSelected(Inventory inv) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < (9 * 6); i++) {
            if (inv.getItem(i) == null)
                continue;
            if (inv.getItem(i).getItemMeta() == null)
                continue;
            if (inv.getItem(i).getItemMeta().getLore() == null || inv.getItem(i).getItemMeta().getLore().size() == 0)
                continue;
            String firstLine = inv.getItem(i).getItemMeta().getLore().get(0);
            if (firstLine.contains("points")) {
                items.add(inv.getItem(i));
            }
        }
        return items;
    }


    private void handleClick(InventoryClickEvent e) {
        final ItemStack clickedItem = e.getCurrentItem();
        final int slot = e.getSlot();


        if (e.getSlot() == armorSlot1) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().helmets, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().helmets, clickedItem).item);
            }
            return;
        }

        if (e.getSlot() == armorSlot2) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().chestplates, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().chestplates, clickedItem).item);
            }
            return;
        }

        if (e.getSlot() == armorSlot3) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().leggings, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().leggings, clickedItem).item);
            }
            return;
        }

        if (e.getSlot() == armorSlot4) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().boots, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().boots, clickedItem).item);
            }
            return;
        }


        if (e.getSlot() == weaponSlot) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().weapons, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().weapons, clickedItem).item);
            }
            return;
        }
        if (e.getSlot() == projectileSlot) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().projectiles, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().projectiles, clickedItem).item);
            }
            return;
        }
        if (e.getSlot() == arrowSlot) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().arrows, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().arrows, clickedItem).item);
            }
            return;
        }
        if (e.getSlot() == bowSlot) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().bows, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().bows, clickedItem).item);
            }
            return;
        }

        if (slot == potion1 || slot == potion2 || slot == potion3 || slot == potion4) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().potions, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().potions, clickedItem).item);
            }
            return;
        }

        if (e.getSlot() == mobSlot1 || e.getSlot() == mobSlot2) {
            if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.getInventory().setItem(slot, core.getItemHandler().previous(core.getItemHandler().mobs, clickedItem).item);
            }
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.getInventory().setItem(slot, core.getItemHandler().next(core.getItemHandler().mobs, clickedItem).item);
            }
            return;
        }


    }
}

