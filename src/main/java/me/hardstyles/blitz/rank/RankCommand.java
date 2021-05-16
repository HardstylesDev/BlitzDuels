package me.hardstyles.blitz.rank;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {
    final private Core core;


    public RankCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if ((sender instanceof Player) && core.getPlayerManager().getPlayer(((Player)sender).getUniqueId()).getRank().getPosition() < 8) {
            sender.sendMessage(ChatColor.RED + "You can't use this command!");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /rank <player> <rank>");
            return true;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        if (!offlinePlayer.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not online.");
            return true;
        }

        Rank rank = core.getRankManager().getRankByName(args[1]);
        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Rank not found.");
            return true;
        }
        IPlayer argPlayer = core.getPlayerManager().getPlayer(offlinePlayer.getUniqueId());
        argPlayer.setRank(rank);
        core.getStatisticsManager().save(argPlayer);
        return true;
    }


}