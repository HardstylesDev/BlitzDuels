package me.hardstyles.blitz.duels;

import lombok.Getter;
import me.hardstyles.blitz.Core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class DuelManager {
    @Getter
    private HashSet<DuelRequest> requests = new HashSet<>();
    private final Core core;

    public DuelManager(Core core){
        this.core = core;
    }
}
