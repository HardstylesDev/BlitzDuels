package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WTFMapCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if(!(sender instanceof Player))
            return true;
        BlitzSGPlayer b = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(((Player) sender).getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "You are currently playing on " + ChatColor.YELLOW + b.getGame().getArena().getName());
        return true;
    }
}