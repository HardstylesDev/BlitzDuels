package me.hardstyles.blitz.player;

import com.google.gson.JsonObject;
import me.hardstyles.blitz.Core;


import me.hardstyles.blitz.party.Party;
import me.hardstyles.blitz.rank.Rank;
import me.hardstyles.blitz.nickname.Nick;


import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.UUID;

public class IPlayer {

    private int gameTaunt;
    private UUID uuid;

    private String customTag;
    private boolean robinhood;
    private boolean wobbuffet;
    private int gameKills;
    private boolean punched;
    private Nick nick;
    private HashSet<Entity> gameEntities;
    private Location gameSpawn;
    private Rank rank;
    private int elo;
    private int wins;
    private int kills;
    private int deaths;
    private boolean hideOthers;
    private int coins;

    private JsonObject jsonObject;
    private String ip;
    private Party party;

    public int getFfaKills() {
        return ffaKills;
    }

    public void setFfaKills(int ffaKills) {
        this.ffaKills = ffaKills;
    }

    public int getFfaDeaths() {
        return ffaDeaths;
    }

    public void setFfaDeaths(int ffaDeaths) {
        this.ffaDeaths = ffaDeaths;
    }

    public int getFfaStreak() {
        return ffaStreak;
    }

    public void setFfaStreak(int ffaStreak) {
        this.ffaStreak = ffaStreak;
    }

    private int ffaKills;
    private int ffaDeaths;
    private int ffaStreak;



    private String name;


    public void setHideOthers(boolean b){
        this.hideOthers = b;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setGameKills(int gameKills) {
        this.gameKills = gameKills;
    }

    public void setNick(Nick nick) {
        this.nick = nick;
    }

    public void setGameEntities(HashSet<Entity> gameEntities) {
        this.gameEntities = gameEntities;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }


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
        this.rank = null;
        this.gameEntities = new HashSet<Entity>();



        this.ffaKills = this.ffaDeaths = this.ffaStreak = 0;






        this.gameKills = 0;
        this.gameTaunt = -1;
        this.gameSpawn = null;

        Core.getInstance().getBlitzSGPlayerManager().addBsgPlayer(this.uuid, this);
    }



    //Player Stats




    public Rank getRank() {

        return rank;
    }

    public void setPunched(boolean b) {
        this.punched = b;
    }

    public boolean getPunched() {
        return this.punched;
    }



    public Rank getRank(boolean checkNick) {
       // if (nick != null && nick.isNicked())
       //     return BlitzSG.getInstance().getRankManager().getRankByName("Default");
       // if (nick == null)
       //     this.nick = new Nick("", null, null, false);
        return rank;
    }

    public boolean doesHideOthers(){
        return this.hideOthers;
    }
    public Nick getNick() {
        return nick;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getElo() {
        return elo;
    }



    public boolean isNicked() {
        return (nick.isNicked());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public void setCustomTag(String tag) {
        this.customTag = tag;
    }

    public String getCustomTag() {
        return this.customTag;
    }

    public String getName() {
        return this.name;
    }

    // public String getNickName() {
    //     if (this.nick != null)
    //         return this.nick.getNickName();
    //     return null;
    // }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public void addElo(int elo) {
        this.elo += elo;
    }

    public void removeElo(int elo) {
        if (this.elo - elo <= 0) {
            this.elo = 0;
            return;
        }
        this.elo += -elo;
    }

    public Location getGameSpawn() {
        return gameSpawn;
    }


    public void setGameSpawn(Location gameSpawn) {
        this.gameSpawn = gameSpawn;
    }



    public int getWins() {
        return this.wins;
    }

    public void addWin() {
        this.wins += 1;
    }

    public int getKills() {
        return this.kills;
    }

    public HashSet<Entity> getGameEntities() {
        return gameEntities;
    }


    public void addKill() {
        this.kills += 1;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void addDeath() {
        this.deaths += 1;
    }

    public int getCoins() {
        return this.coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins += -coins;
    }

    public void setRank(Rank rank) {
        if (rank == null)
            this.rank = Core.getInstance().getRankManager().getRankByName("Default");
        else
            this.rank = rank;
    }



    //Game Stats

    public int getGameKills() {
        return this.gameKills;
    }

    public int getGameTaunt() {
        return this.gameTaunt;
    }

    public void resetGameKills() {
        this.gameKills = 0;
    }

    public void setGameTaunt(int i) {
        this.gameTaunt = i;
    }

    public void addGameKill() {
        this.gameKills += 1;
        this.kills += 1;
    }




    public void setWobbuffet(boolean idk) {
        wobbuffet = idk;
    }

    public boolean getWobbuffet() {
        return this.wobbuffet;
    }

    public void setRobinhood(boolean idk) {
        robinhood = idk;
    }

    public boolean getRobinhood() {
        return this.robinhood;
    }






    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }
    public Party getParty(){
        return this.party;
    }
    public void setParty(Party party){
        this.party = party;
    }

}
