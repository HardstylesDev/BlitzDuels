package me.hardstyles.blitz.staff.report;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;


public class ReportCommand implements CommandExecutor {

    final private Core core;

    public ReportCommand(Core core) {
        this.core = core;
    }

    private HashMap<UUID, Long> cooldown = new HashMap<>();


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        IPlayer iPlayer = core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
        if (iPlayer == null) {
            return true;
        }

        if (args.length < 2) {
            p.sendMessage(ChatColor.RED + "Usage: /report <player> <reason>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (!target.isOnline() || target == null) {
            p.sendMessage(ChatColor.RED + "Couldn't find that player.");
            return true;
        }

        Long remainingTime = 30000 - (System.currentTimeMillis() - cooldown.getOrDefault(p.getUniqueId(), 0L));
        if (remainingTime > 0) {
            p.sendMessage(ChatColor.RED + "Please wait before reporting another player! Wait " + (remainingTime / 1000) + " more seconds.");
            return true;
        }

        IPlayer iTarget = core.getPlayerManager().getPlayer(target.getUniqueId());


        StringBuilder builder = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            builder.append(" ").append(args[i]);
        }
        String message = builder.toString();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            IPlayer onlineIPlayer = core.getPlayerManager().getPlayer(onlinePlayer.getUniqueId());
            if (onlineIPlayer.getRank().getPosition() <= 5) {
                continue;
            }

            onlinePlayer.sendMessage(ChatColor.RED + "[REPORT] " + iPlayer.getRank().getChatColor() + p.getDisplayName() + ChatColor.GOLD + " has reported " + iTarget.getRank().getChatColor() + target.getName() + ChatColor.RED + "\nReason: " + ChatColor.GOLD + message);
        }

        core.getStaffManager().getReports().add(new ReportEntry(message, false, target.getUniqueId(), p.getUniqueId()));
        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
        return true;
    }

}

