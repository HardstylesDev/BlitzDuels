package me.hardstyles.blitz.queue;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class QueueCommand extends Command {

    final private Core core = Core.i();

    public QueueCommand() {
        super("queue");
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "list": {

                    p.sendMessage(ChatColor.YELLOW + "Solo - " + core.getQueueManager().getSoloQueues().size());
                    p.sendMessage(ChatColor.YELLOW + "Teams - " + core.getQueueManager().getTeamsQueues().size());

                    break;
                }
                case "disable": {
                    if (iPlayer.getRank().getRank().equalsIgnoreCase("admin")) {
                        core.setDisableQueues(core.isDisableQueues());
                        p.sendMessage("Queue " + (core.isDisableQueues() ? "disabled" : "enabled"));
                    }
                    break;
                }
                default: {
                    try {
                        QueueType type = QueueType.valueOf(args[0]);
                        core.getQueueManager().handleQueue(type, p);
                    } catch (IllegalArgumentException e) {
                        p.sendMessage("§cThat queue does not exist.");
                    }
                }
            }
        } else {
            p.sendMessage("§8§m-----------------------");
            p.sendMessage("§c/queue list");
            p.sendMessage("§c/queue <queue>");
            p.sendMessage("§8§m-----------------------");
        }
    }
}
