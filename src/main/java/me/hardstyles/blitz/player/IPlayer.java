package me.hardstyles.blitz.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.hardstyles.blitz.Core;


import me.hardstyles.blitz.match.Match;
import me.hardstyles.blitz.party.Party;
import me.hardstyles.blitz.rank.Rank;
import me.hardstyles.blitz.nickname.Nick;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

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


    public void setHideOthers(boolean b){
        this.hideOthers = b;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setNick(Nick nick) {
        this.nick = nick;
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
        this.match = null;
        this.rank = null;
        this.layouts = new HashMap<>();






        Core.getInstance().getPlayerManager().addBsgPlayer(this.uuid, this);
    }



    //Player Stats




    public Rank getRank() {

        return rank;
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

    public int getWins() {
        return this.wins;
    }

    public void addWin() {
        this.wins += 1;
    }

    public int getKills() {
        return this.kills;
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

    public boolean hasMatch(){
        if(this.match == null){
            return false;
        }
        return true;
    }

    public void setMatch(Match match){
        this.match = match;
    }
    public Match getMatch(){
        return this.match;
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


    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
    public HashMap<Integer, JsonArray> getLayouts() {
        return layouts;
    }

    public void setLayouts(HashMap<Integer, JsonArray> layouts) {
        this.layouts = layouts;
    }

    public JsonArray getEditingLayout() {
        return editingLayout;
    }

    public void setEditingLayout(JsonArray editingLayout) {
        this.editingLayout = editingLayout;
    }
}

