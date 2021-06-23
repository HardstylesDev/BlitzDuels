package me.hardstyles.blitz.staff;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.utils.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;


public class StaffChatCommand extends Command {


    public StaffChatCommand() {
        super("staffchat", ImmutableList.of("sc"), 6);
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Usage: /sc <message>");
            return;
        }

        StringBuilder builder = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            builder.append(" ").append(args[i]);
        }
        String message = builder.toString();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            IPlayer onlineIPlayer = Core.i().getPlayerManager().getPlayer(onlinePlayer.getUniqueId());
            if (onlineIPlayer.getRank().getPosition() <= 5) {
                continue;
            }

            onlinePlayer.sendMessage(ChatColor.AQUA + "[STAFF] " + iPlayer.getRank().getChatColor() + p.getDisplayName() + ChatColor.WHITE + ": " + message);
        }
    }
}

