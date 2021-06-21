package me.hardstyles.blitz.arena;

import me.hardstyles.blitz.Core;

import java.util.*;

public class ArenaManager {
    final private Core core;

    private HashSet<Arena> occupied;
    private HashSet<Arena> arenas;

    public ArenaManager(Core core) {
        this.core = core;
        arenas = new HashSet<>();
        occupied = new HashSet<>();

        Arrays.asList("DM1", "DM4", "DM5", "DM6", "DM7","DM1_1", "DM4_1", "DM5_1", "DM6_1", "DM7_1").forEach(s -> arenas.add(new Arena(core, s)));

    }


    public HashSet<Arena> getArenas() {
        return arenas;
    }


    public Arena getNext() {
        ArrayList<Arena> random = new ArrayList<>(arenas);
        Collections.shuffle(random);
        for (Arena arena : random) {

            if (arena.isOccupied()) {
                continue;
            }
            return arena;
        }
        return null;
    }


}
