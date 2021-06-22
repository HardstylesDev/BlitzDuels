package me.hardstyles.blitz.queue;

import org.bukkit.OfflinePlayer;

import java.util.ArrayList;

public class Queue {
    private final QueueType queueType;
    private final ArrayList<OfflinePlayer> players;
    public Queue(QueueType queueType){
        this.queueType = queueType;
        this.players = new ArrayList<>();
    }
    public ArrayList<OfflinePlayer> getPlayers(){return this.players;}
    public QueueType getQueueType(){
        return this.queueType;
    }
}
