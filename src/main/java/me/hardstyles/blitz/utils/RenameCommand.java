package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.Core;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class RenameCommand implements CommandExecutor {

    final private Core core;

    public RenameCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if (core.getPlayerManager().getPlayer(p.getUniqueId()).getRank().getPosition() < 6) {
            p.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }

        if (p.getItemInHand() == null) {
            p.sendMessage("No ItemStack to rename");
            return true;
        }
        ItemStack itemStack = p.getItemInHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', joined(args)));
        itemStack.setItemMeta(itemMeta);
        p.setItemInHand(itemStack);
        p.sendMessage("Item renamed");
        return true;
    }

    private String joined(String[] args) {
        String a = "";
        for (String part : args) {
            if (a != "") a += " ";
            a += part;
        }
        return a;
    }

}

