package me.hardstyles.blitz.queue;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class QueueCommand implements CommandExecutor {

    public static HashMap<UUID, Long> cooldown = new HashMap<>();
    final private Core core;

    public QueueCommand(Core core) {
        this.core = core;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            for (QueueType value : QueueType.values()) {
                p.sendMessage(ChatColor.YELLOW + value.name() + " - " + core.getQueueManager().getQueues().get(value).size());
            }
        }
        if (args[0].equalsIgnoreCase("join")) {
            core.getQueueManager().add(QueueType.NORMAL, p);

        }
        if (args[0].equalsIgnoreCase("disable")) {
            IPlayer iPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());
            if (iPlayer.getRank().getRank().equalsIgnoreCase("admin")) {
                core.disableQueues = !core.disableQueues;
                p.sendMessage("Queue " + (core.disableQueues ? "disabled" : "enabled"));
                return true;
            }

        }
        if (args[0].equalsIgnoreCase("leave")) {
            core.getQueueManager().remove(p);
        }


        return true;
    }
}
