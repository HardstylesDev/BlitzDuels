package me.hardstyles.blitz.match;

import me.hardstyles.blitz.Core;

public class MatchManager {

    final private Core core;
    private int matchCount;

    public MatchManager(Core core) {
        this.core = core;
        this.matchCount = 0;
    }

    public void add() {
        this.matchCount++;
    }

    public void remove() {
        this.matchCount--;
    }

    public int getMatchCount() {
        return matchCount;
    }
}
