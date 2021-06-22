package me.hardstyles.blitz.staff;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class StaffChatCommand implements CommandExecutor {

    final private Core core;

    public StaffChatCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        IPlayer iPlayer = core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
        if (iPlayer == null) {
            return true;
        }
        if (iPlayer.getRank().getPosition() <= 5) {
            p.sendMessage(ChatColor.RED + "You can't do this");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Usage: /sc <message>");
            return true;
        }

        StringBuilder builder = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            builder.append(" ").append(args[i]);
        }
        String message = builder.toString();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            IPlayer onlineIPlayer = core.getPlayerManager().getPlayer(onlinePlayer.getUniqueId());
            if (onlineIPlayer.getRank().getPosition() <= 5) {
                continue;
            }

            onlinePlayer.sendMessage(ChatColor.AQUA + "[STAFF] " + iPlayer.getRank().getChatColor() + p.getDisplayName() + ChatColor.WHITE + ": " + message);
        }


        return true;
    }

}

