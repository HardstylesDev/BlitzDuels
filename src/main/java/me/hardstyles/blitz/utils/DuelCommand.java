package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DuelCommand implements CommandExecutor {

    final private Core core;

    public DuelCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        IPlayer player = core.getPlayerManager().getPlayer(p.getUniqueId());
        if(player == null){
            p.kickPlayer(ChatColor.AQUA + "Oopsie Daisy");
            return true;
        }
        if (player.getMatch() != null) {
           p.sendMessage(ChatColor.RED +"Can't use this command while in a match");
            return true;
        }
        if(args.length == 0){
            p.sendMessage(ChatColor.RED +"Usage: /duel <player>");
            return true;
        }
        Player arg = Bukkit.getPlayer(args[0]);
        if(arg == null || !arg.isOnline()){
            p.sendMessage(ChatColor.RED +"Can't find that player!");
            return true;
        }
        IPlayer target = core.getPlayerManager().getPlayer(arg.getUniqueId());
        if(target == null){
            p.sendMessage(ChatColor.RED +"Can't find that player!");
            return true;
        }
        if(target.getMatch() != null){
            p.sendMessage(ChatColor.RED +"Player is already in a match!");
            return true;
        }

        p.sendMessage(ChatColor.GREEN + "You've sent a duel request to " + target.getRank(true).getPrefix() + arg.getName() + ChatColor.GREEN + "!");
        return true;
    }

}

