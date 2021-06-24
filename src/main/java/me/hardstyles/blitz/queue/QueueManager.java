package me.hardstyles.blitz.queue;

import lombok.Getter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.match.Match;
import me.hardstyles.blitz.party.Party;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Getter
public class QueueManager {
    private final Core core;
    private final HashMap<QueueType, HashSet<UUID>> queues = new HashMap<>();

    public QueueManager(Core core) {
        this.core = core;
        for (QueueType type : QueueType.values()) {
            this.queues.put(type, new HashSet<>());
        }
    }

    public void handleQueue(QueueType queueType, Player player) {
        IPlayer p = core.getPlayerManager().getPlayer(player.getUniqueId());
        if(core.isDisableQueues() || p == null) {
            player.sendMessage("§cYou cannot queue right now.");
        } else if (p.getMatch() != null) {
            player.sendMessage("§cYou cannot queue in a game.");
        } else if (p.getParty() != null) {
            if (p.getParty().getOwner().equals(p.getUuid())) {
                player.chat("/p match");
            } else {
                player.sendMessage("§cYou cannot queue in a party.");
            }
        } else if (getQueue(p.getUuid()) != null) {
            QueueType current = getQueue(p.getUuid());
            this.queues.get(current).remove(player.getUniqueId());
            player.sendMessage("§aYou have left the " + current.name().toLowerCase() + " queue.");
        } else if (core.getArenaManager().getNext() == null) {
            player.sendMessage("§cThere are currently no arenas available, try again later.");
        } else {
            this.queues.get(queueType).add(player.getUniqueId());
            player.sendMessage("§aYou have joined the " + queueType.name().toLowerCase() + " queue.");
            tryStart(queueType);
        }
    }

    public void remove(Player player) {
        for (QueueType value : QueueType.values()) {
            this.queues.get(value).remove(player.getUniqueId());
        }
        player.sendMessage("§aYou have left the queue.");
    }

    private void tryStart(QueueType type) {
        Arena arena = core.getArenaManager().getNext();
        ArrayList<UUID> players = new ArrayList<>(queues.get(type));
        if (arena == null || players.size() < 2) {
            return;

        }

        Match match = new Match(core, arena);
        match.add(players.get(0));
        match.add(players.get(1));
        queues.get(type).remove(players.get(0));
        queues.get(type).remove(players.get(1));
        match.start();
    }

    public QueueType getQueue(UUID uuid) {
        for (QueueType type : QueueType.values()) {
            if (queues.get(type).contains(uuid)) {
                return type;
            }
        }
        return null;
    }

    public void tryStart(Party party) {
        Arena arena = core.getArenaManager().getNext();
        if (arena == null) {
            return;
        }
        ArrayList<UUID> players = party.getMembers();
        Match match = new Match(core, arena);
        for (UUID uuid : players) {
            match.add(uuid);
        }
        match.start();
    }
}
