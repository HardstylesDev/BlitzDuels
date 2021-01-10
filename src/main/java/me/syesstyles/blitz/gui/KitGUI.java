package me.syesstyles.blitz.gui;

import me.syesstyles.blitz.BlitzSG;
import me.syesstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import me.syesstyles.blitz.kit.Kit;
import me.syesstyles.blitz.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class KitGUI {

    public static void openGUI(Player p) {
        BlitzSGPlayer uhcPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());

        Inventory inv = Bukkit.createInventory(null, 27, "§8Kit Selector");

        ArrayList<Kit> kits = BlitzSG.getInstance().getKitManager().getKits();
        int index = 0;
        for (Kit kit : kits) {
            inv.setItem(index, ItemUtils.buildItem(kit.getIcon(), ChatColor.GOLD + kit.getName(), Arrays.asList(ChatColor.GRAY + kit.getDescription())));
            index++;
        }
        //Open the GUI
        BlitzSG.getInstance().getGuiManager().setInGUI(p, true);
        p.openInventory(inv);
    }
}
