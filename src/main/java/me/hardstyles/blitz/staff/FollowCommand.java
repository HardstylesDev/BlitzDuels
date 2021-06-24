package me.hardstyles.blitz.staff;

import com.google.common.collect.ImmutableList;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.utils.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class FollowCommand extends Command {

    public FollowCommand() {
        super("follow", ImmutableList.of(), 6);
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return args.length == 1 ? null : ImmutableList.of();
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            IPlayer targetIp = target == null ? null : Core.i().getPlayerManager().getPlayer(target.getUniqueId());
            if (targetIp == null) {
                p.sendMessage("§cThat player is not online.");
                return;
            }


            if (iPlayer.getFollowing() != null && iPlayer.getFollowing().equals(target.getUniqueId())) {
                p.sendMessage("You are no longer following " + targetIp.getRank().getPrefix() + target.getName());
                iPlayer.setFollowing(null);
            } else {
                p.sendMessage("You are now following " + targetIp.getRank().getPrefix() + target.getName());
                iPlayer.setFollowing(target.getUniqueId());
            }
        } else {
            p.sendMessage("§cUsage: /follow <player>");
        }
    }
}
