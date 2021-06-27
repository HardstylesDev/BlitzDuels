package me.hardstyles.blitz.punishments.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.punishments.PType;

@Getter
@AllArgsConstructor
public class PunishmentInfo {
    private final PType type;
    private final boolean removal;
    private final long length;
    private final String reason;
    private final String executor;
    private final String punished;
    private final String executorDisplay;
    private final String punishedDisplay;
    private final String server = Core.i().getConfig().getString("id");
}
