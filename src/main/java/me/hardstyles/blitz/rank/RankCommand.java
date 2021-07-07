package me.hardstyles.blitz.rank;

import com.google.common.collect.ImmutableList;
import me.elijuh.nametagapi.NametagAPI;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RankCommand extends Command {
    public RankCommand() {
        super("rank", ImmutableList.of("setrank"), 7);

    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length < 2) {
            p.sendMessage(ChatColor.RED + "Usage: /rank <player> <rank>");
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) {
            p.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }
        if (!offlinePlayer.isOnline()) {
            p.sendMessage(ChatColor.RED + "Player not online.");
            return;
        }

        Rank rank = core.getRankManager().getRankByName(args[1]);
        if (rank == null) {
            p.sendMessage(ChatColor.RED + "Rank not found.");
            return;
        }
        IPlayer argPlayer = core.getPlayerManager().getPlayer(offlinePlayer.getUniqueId());
        argPlayer.setRank(rank);
        core.getStatisticsManager().save(argPlayer);
        NametagAPI.setNametag(offlinePlayer.getName(), rank.getPrefix(), "");
    }
    @Override
    public void onConsole(CommandSender sender, String[] args){
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /rank <player> <rank>");
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }
        if (!offlinePlayer.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not online.");
            return;
        }

        Rank rank = core.getRankManager().getRankByName(args[1]);
        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Rank not found.");
            return;
        }
        IPlayer argPlayer = core.getPlayerManager().getPlayer(offlinePlayer.getUniqueId());
        argPlayer.setRank(rank);
        core.getStatisticsManager().save(argPlayer);
        NametagAPI.setNametag(offlinePlayer.getName(), rank.getPrefix(), "");
    }
}
