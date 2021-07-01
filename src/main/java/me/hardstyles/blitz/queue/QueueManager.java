package me.hardstyles.blitz.queue;

import lombok.Getter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.match.match.TeamMatch;
import me.hardstyles.blitz.party.Party;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@Getter
public class QueueManager {
    private final Core core;
    private final HashSet<UUID> soloQueues = new HashSet<>();
    private final HashSet<Party> teamsQueues = new HashSet<>();

    public QueueManager(Core core) {
        this.core = core;

    }

    public void handleQueue(QueueType queueType, Player player) {
        IPlayer p = core.getPlayerManager().getPlayer(player.getUniqueId());
        if (core.isDisableQueues() || p == null) {
            player.sendMessage("§cYou cannot queue right now.");
            return;
        } else if (p.getMatch() != null) {
            player.sendMessage("§cYou cannot queue in a game.");
            return;
        }
        if (queueType == QueueType.SOLO) {
            if (p.getParty() != null) {
                if (p.getParty().getOwner().equals(p.getUuid())) {
                    player.chat("/p match");
                } else {
                    player.sendMessage("§cYou cannot queue in a party.");
                }
            } else if (soloQueues.contains(p.getUuid())) {
                this.soloQueues.remove(player.getUniqueId());
                player.sendMessage("§aYou have left the " + queueType.name().toLowerCase() + " queue.");
            } else if (core.getArenaManager().getNext() == null) {
                player.sendMessage("§cThere are currently no arenas available, try again later.");
            } else {
                this.soloQueues.add(player.getUniqueId());
                player.sendMessage("§aYou have joined the " + queueType.name().toLowerCase() + " queue.");
                tryStart(queueType);
            }
        } else if (queueType == QueueType.TEAMS) {
            Party party = p.getParty();
            if (party == null) {
                player.sendMessage("§cYou cannot join the teams queue without a party.");
            } else if (teamsQueues.contains(party)) {
                this.teamsQueues.remove(party);
                player.sendMessage("§aYou have left the " + queueType.name().toLowerCase() + " queue.");
            } else if (core.getArenaManager().getNext() == null) {
                player.sendMessage("§cThere are currently no arenas available, try again later.");
            } else {
                this.teamsQueues.add(party);
                player.sendMessage("§aYou have joined the " + queueType.name().toLowerCase() + " queue.");
                tryStart(queueType);
            }
        }
    }

    public void remove(Player player) {
        this.soloQueues.remove(player.getUniqueId());
        player.sendMessage("§aYou have left the queue.");
    }

    private void tryStart(QueueType type) {
        Arena arena = core.getArenaManager().getNext();
        if (type == QueueType.SOLO) {
            ArrayList<UUID> players = new ArrayList<>(soloQueues);
            if (arena == null || players.size() < 2) {
                return;
            }
            Match match = new Match(core, arena);
            match.add(players.get(0));
            match.add(players.get(1));
            soloQueues.remove(players.get(0));
            soloQueues.remove(players.get(1));
            match.start();
        } else if (type == QueueType.TEAMS) {
            ArrayList<Party> parties = new ArrayList<>(teamsQueues);
            if (arena == null || parties.size() < 2) {
                return;
            }
            TeamMatch match = new TeamMatch(core, arena);
            match.add(parties.get(0));
            match.add(parties.get(1));
            teamsQueues.remove(parties.get(0));
            teamsQueues.remove(parties.get(1));
            match.start();
        }
    }
}
