package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


public class SpectatorCommand implements CommandExecutor {

    final private Core core;

    public SpectatorCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        IPlayer player = core.getPlayerManager().getPlayer(p.getUniqueId());

        if (player.getMatch() != null) {
           p.sendMessage(ChatColor.RED +"Can't use this command while in a match");
            return true;
        }
        if(args.length == 0){
            p.sendMessage(ChatColor.RED +"Usage: /spectate <player>");
            return true;
        }
        Player arg = Bukkit.getPlayer(args[0]);
        if(arg == null || !arg.isOnline()){
            p.sendMessage(ChatColor.RED +"Can't find that player!");
            return true;
        }
        IPlayer target = core.getPlayerManager().getPlayer(arg.getUniqueId());
        if(target.getMatch() == null){
            p.sendMessage(ChatColor.RED +"Player is not in a match!");
            return true;
        }
        Match match = target.getMatch();
        if (!match.getAlivePlayers().contains(arg.getUniqueId())) {
            p.sendMessage(ChatColor.RED +"Player is a spectator of a match!");
            return true;
        }
        p.getInventory().clear();
        p.teleport(arg.getLocation());
        boolean isSilent = false;
        if(args.length == 2){
            if(args[1].equalsIgnoreCase("-s")){
                isSilent = true;
            }
        }
        if(!isSilent){
            match.send(player.getRank().getPrefix()+p.getName() + ChatColor.YELLOW + " is now spectating!");
        }
        match.getPlayers().add(p.getUniqueId());
        match.getPlayerReference().put(p.getUniqueId(), p);
        for (UUID alivePlayer : match.getAlivePlayers()) {
            Player alive = match.getPlayerReference().get(alivePlayer);
            alive.hidePlayer(p);
        }
        player.setMatch(match);
        p.spigot().setCollidesWithEntities(false);
        p.setAllowFlight(true);
        p.setFlying(true);
        return true;
    }

}

