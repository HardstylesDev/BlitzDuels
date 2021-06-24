package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class HubCommand implements CommandExecutor {

    final private Core core;

    public HubCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        IPlayer player = core.getPlayerManager().getPlayer(p.getUniqueId());
        if (player.getMatch() == null) {
            core.getPlayerManager().hub(p);
            player.setMatch(null);
            return true;
        }

        Match match = player.getMatch();
        if (!match.getAlivePlayers().contains(player.getUuid())) {
            core.getPlayerManager().hub(p);
            player.setMatch(null);
            return true;
        }

        match.onDeath(p.getUniqueId());
        core.getPlayerManager().hub(p);
        player.setMatch(null);
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

