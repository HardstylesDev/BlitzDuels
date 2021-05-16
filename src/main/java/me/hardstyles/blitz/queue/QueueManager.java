package me.hardstyles.blitz.queue;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.match.Match;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
        if(queues.size() == 2){
            startMatch();
        }
    }
    public void remove(Player player){
        for (QueueType value : QueueType.values()) {
            this.queues.get(value).remove(player.getUniqueId());

        }
        player.sendMessage(ChatColor.GREEN + "You left the queue.");

    }
    private void startMatch(){
        Arena arena = core.getArenaManager().getNext();
        if(arena == null){
            return;
        }
        ArrayList<UUID> players = new ArrayList<>();
        for (UUID uuid : queues.get(QueueType.NORMAL)) {
            players.add(uuid);
            if(players.size() == 2){
                Match match = new Match(core, arena);
                match.add(players.get(0));
                match.add(players.get(1));
                Bukkit.broadcastMessage("" +queues.get(QueueType.NORMAL).size());
                queues.get(QueueType.NORMAL).remove(players.get(0));
                queues.get(QueueType.NORMAL).remove(players.get(1));
                players.clear();
                Bukkit.broadcastMessage("" +queues.get(QueueType.NORMAL).size());

                match.start();
                return;
            }
        }
    }

    public HashMap<QueueType, HashSet<UUID>> getQueues() {
        return queues;
    }
}
