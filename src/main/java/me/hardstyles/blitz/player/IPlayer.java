package me.hardstyles.blitz.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import me.hardstyles.blitz.Core;


import me.hardstyles.blitz.match.Match;
import me.hardstyles.blitz.party.Party;
import me.hardstyles.blitz.rank.Rank;
import me.hardstyles.blitz.nickname.Nick;


import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
public class IPlayer {

    private int gameTaunt;
    private UUID uuid;
    private String customTag;
    private Nick nick;
    private Rank rank;
    private int elo;
    private int wins;
    private int kills;
    private int deaths;
    private HashMap<Integer, JsonArray> layouts;
    private JsonArray editingLayout;
    private int streak;
    private boolean hideOthers;
    private int coins;
    private Match match;
    private JsonObject jsonObject;
    private String ip;
    private Party party;


    private String name;

    public IPlayer(UUID uuid) {
        this.jsonObject = new JsonObject();
        this.party = null;
        this.hideOthers = false;
        this.nick = null;
        this.uuid = uuid;
        this.elo = 0;
        this.wins = 0;
        this.kills = 0;
        this.customTag = null;
        this.deaths = 0;
        this.coins = 0;
        this.match = null;
        this.rank = null;
        this.layouts = new HashMap<>();






        Core.i().getPlayerManager().addPlayer(this.uuid, this);
    }



    //Player Stats





    public Rank getRank(boolean checkNick) {
        if (nick != null && nick.isNicked())
            return Core.i().getRankManager().getRankByName("Default");
        if (nick == null)
            this.nick = new Nick("", null, null, false);
        return rank;
    }



    public boolean isNicked() {
        return (nick.isNicked());
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
        if(this.match == null){
            return false;
        }
        return true;
    }




}

