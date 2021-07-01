package me.hardstyles.blitz.commands.impl;

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
        core.getPlayerManager().hub(p);
        return true;
    }

}

