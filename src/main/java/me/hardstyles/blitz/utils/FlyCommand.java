package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.rank.ranks.Default;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if ((Core.i().getRankManager().getRank((Player) sender) instanceof Default)) {
            Core.send((Player) sender, "&cThis command requires " + Core.i().getRankManager().getRankByName("vip").getRankFormatted() + " &cor higher!");
            return true;
        }
        Player p = (Player) sender;
        p.setAllowFlight(!p.getAllowFlight());
        Core.send(p, "&aYou have " + (p.getAllowFlight() ? "enabled" : "disabled") + " flight");
        return true;
    }
}