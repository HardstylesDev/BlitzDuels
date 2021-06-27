package me.hardstyles.blitz.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Punishment {
    private final int id;
    private final String uuid;
    private final PType type;
    private final String removed;
    private final long time, length;
    private final String reason, executor, server;
}
