package me.hardstyles.blitz.commands.impl;

import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class IgnoreCommand extends Command {
    public IgnoreCommand() {
        super("ignore");
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage("§cThat player is not online.");
            } else {
                if (iPlayer.getIgnoreList().contains(target.getUniqueId().toString())) {
                    iPlayer.getIgnoreList().remove(target.getUniqueId().toString());
                    p.sendMessage("§aYou have removed " + target.getName() + " from your ignore list.");
                } else {
                    iPlayer.getIgnoreList().add(target.getUniqueId().toString());
                    p.sendMessage("§aYou have added " + target.getName() + " to your ignore list.");
                }
            }
        } else {
            p.sendMessage("§Usage: /ignore <player>");
        }
    }
}
