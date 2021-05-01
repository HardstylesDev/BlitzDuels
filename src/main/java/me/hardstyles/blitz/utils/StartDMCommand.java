package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import me.hardstyles.blitz.rank.ranks.Admin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartDMCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(BlitzSG.getInstance().getRankManager().getRank((Player) sender) instanceof Admin))
            return true;
        BlitzSGPlayer bsgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(((Player)sender).getUniqueId());
        bsgPlayer.getGame().startDeathmatchCounter(bsgPlayer.getGame().getGameTime());
        return true;
    }
}
