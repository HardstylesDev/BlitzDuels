package me.hardstyles.blitz.queue;

import com.comphenix.protocol.PacketType;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class QueueManager {
    private final Core core;
    private HashMap<QueueType, HashSet<UUID>> queues;

    public QueueManager(Core core) {
        this.core = core;
        this.queues = new HashMap<>();
        this.queues.put(QueueType.NORMAL, new HashSet<>());
        this.queues.put(QueueType.TEAMS, new HashSet<>());
    }

    public void add(QueueType queueType, Player player){
        for (QueueType value : QueueType.values()) {
            this.queues.get(value).remove(player.getUniqueId());
        }
        this.queues.get(queueType).add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You joined the " + queueType.name() + " queue.");
    }
    public void remove(Player player){
        for (QueueType value : QueueType.values()) {
            this.queues.get(value).remove(player.getUniqueId());
        }
        player.sendMessage(ChatColor.GREEN + "You left the queue.");

    }

    public HashMap<QueueType, HashSet<UUID>> getQueues() {
        return queues;
    }
}
