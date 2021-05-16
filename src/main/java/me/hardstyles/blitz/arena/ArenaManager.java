package me.hardstyles.blitz.arena;

import me.hardstyles.blitz.Core;

import java.util.Arrays;
import java.util.HashSet;

public class ArenaManager {
    final private Core core;

    private HashSet<Arena> occupied;
    private HashSet<Arena> arenas;

    public ArenaManager(Core core) {
        this.core = core;
        arenas = new HashSet<>();
        occupied = new HashSet<>();

        Arrays.asList("DM4", "DM5", "DM6", "DM7").forEach(s -> arenas.add(new Arena(core, s)));

    }


    public HashSet<Arena> getArenas() {
        return arenas;
    }


    public Arena getNext() {
        for (Arena arena : arenas) {
            if (arena.isOccupied()) {
                continue;
            }
            return arena;
        }
        return null;
    }


}
