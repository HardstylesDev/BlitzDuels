package me.hardstyles.blitz.match.match;

import lombok.Getter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.match.MatchStage;
import me.hardstyles.blitz.party.Party;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class TeamMatch extends Match {
    @Getter
    final private HashSet<Party> parties = new HashSet<>();
    final private Core core = getCore();
    final private Arena arena = getArena();

    public TeamMatch(Core core, Arena arena) {
        super(core, arena);
    }

    @Override
    public void teleportToSpawns() {
        int spawnIndex = 0;
        for (Party party : parties) {
            if (spawnIndex == arena.getSpawns().size()) {
                spawnIndex = 0;
            }
            for (UUID memberUuid : party.getMembers()) {
                Player member = core.getServer().getPlayer(memberUuid);
                member.teleport(arena.getSpawns().get(spawnIndex));
            }
            spawnIndex++;

        }
    }
    public void add(Party party){
        this.parties.add(party);
        for (UUID member : party.getMembers()) {
            super.add(member);
        }
    }


    private HashSet<Party> getAliveParties(){
        HashSet<Party> survivingParties = new HashSet<>();
        for (UUID alivePlayer : this.getAlivePlayers()) {
            survivingParties.add(core.getPlayerManager().getPlayer(alivePlayer).getParty());
        }
        return survivingParties;
    }

    @Override
    public void finishCheck(){
        if (getAliveParties().size() <= 1) {
            getAliveParties().stream().findFirst().get().getMembers().forEach(member -> super.getWinners().add(member));
            setMatchStage(MatchStage.ENDED);
            finish();
        }
    }


}
