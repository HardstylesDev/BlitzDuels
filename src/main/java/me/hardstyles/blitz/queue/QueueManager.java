package me.hardstyles.blitz.queue;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;

import java.util.HashMap;
import java.util.HashSet;

public class QueueManager {
    private final Core core;
    private HashMap<QueueType, IPlayer> queues;

    public QueueManager(Core core) {
        this.core = core;
        this.queues = new HashMap<QueueType, IPlayer>();
    }




}
