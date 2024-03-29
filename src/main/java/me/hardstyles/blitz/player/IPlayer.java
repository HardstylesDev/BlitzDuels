package me.hardstyles.blitz.player;

import lombok.Getter;
import lombok.Setter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.nickname.Nick;
import me.hardstyles.blitz.party.Party;
import me.hardstyles.blitz.rank.Rank;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Getter
@Setter
public class IPlayer {
    private final HashMap<Integer, String> layouts = new HashMap<>();
    private final HashSet<String> ignoreList = new HashSet<>();
    private final UUID uuid;
    private UUID following;
    private int gameTaunt, elo, wins, kills, deaths, streak, coins;
    private String customTag, ip, name;
    private Nick nick;
    private Rank rank;
    private boolean hideOthers;
    private Match match;
    private Party party;


    public IPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public Rank getRank() {
        if (nick != null && nick.isNicked())
            return Core.i().getRankManager().getRankByName("Default");
        if (nick == null)
            this.nick = new Nick("", null, null, false);
        return rank;
    }

    public boolean isNicked() {
        return nick != null && nick.isNicked();
    }

    // public String getNickName() {
    //     if (this.nick != null)
    //         return this.nick.getNickName();
    //     return null;
    // }

    public void addElo(int elo) {
        this.elo += elo;
    }

    public void addWin(){wins ++ ;}
    public void addKill(){kills ++ ;}
    public void addDeath(){deaths ++ ;}
    public void removeElo(int elo) {
        if (this.elo - elo <= 0) {
            this.elo = 0;
            return;
        }
        this.elo += -elo;
    }

    public void setRank(Rank rank) {
        if (rank == null)
            this.rank = Core.i().getRankManager().getRankByName("Default");
        else
            this.rank = rank;
    }

    public boolean hasMatch(){
        return this.match != null;
    }

}

