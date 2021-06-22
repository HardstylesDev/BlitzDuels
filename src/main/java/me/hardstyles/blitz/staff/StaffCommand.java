package me.hardstyles.blitz.staff;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.Match;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class StaffCommand implements CommandExecutor {

    final private Core core;

    public StaffCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        IPlayer iPlayer = core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
        if(iPlayer == null){
            return true;
        }


        return true;
    }

}

