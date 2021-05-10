package me.hardstyles.blitz.match;

import org.bukkit.OfflinePlayer;

import java.util.HashSet;

public class Match {
    int id;
    HashSet<OfflinePlayer> players;
    public Match(int id){
        this.players = new HashSet<>();
    }


}
