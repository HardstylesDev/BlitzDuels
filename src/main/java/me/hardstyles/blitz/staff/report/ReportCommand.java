package me.hardstyles.blitz.staff.report;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class ReportCommand extends me.hardstyles.blitz.utils.Command {

    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public ReportCommand() {
        super("report");
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length < 2) {
            p.sendMessage(ChatColor.RED + "Usage: /report <player> <reason>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            p.sendMessage(ChatColor.RED + "Couldn't find that player.");
            return;
        }

        long remainingTime = 30000 - (System.currentTimeMillis() - cooldown.getOrDefault(p.getUniqueId(), 0L));
        if (remainingTime > 0) {
            p.sendMessage(ChatColor.RED + "Please wait before reporting another player! Wait " + (remainingTime / 1000) + " more seconds.");
            return;
        }

        IPlayer iTarget = Core.i().getPlayerManager().getPlayer(target.getUniqueId());


        StringBuilder builder = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            builder.append(" ").append(args[i]);
        }
        String message = builder.toString();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            IPlayer onlineIPlayer = Core.i().getPlayerManager().getPlayer(onlinePlayer.getUniqueId());
            if (onlineIPlayer.getRank().getPosition() > 5) {
                onlinePlayer.sendMessage(ChatColor.RED + "[REPORT] " + iPlayer.getRank().getChatColor() + p.getDisplayName() + ChatColor.GOLD + " has reported " + iTarget.getRank().getChatColor() + target.getName() + ChatColor.RED + "\nReason: " + ChatColor.GOLD + message);
            }
        }

        Core.i().getStaffManager().getReports().add(new ReportEntry(message, false, target.getUniqueId(), p.getUniqueId()));
        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
    }
}

