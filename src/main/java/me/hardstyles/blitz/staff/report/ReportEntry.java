package me.hardstyles.blitz.staff.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
@Getter
@AllArgsConstructor
public class ReportEntry {
    private long time;
    private String reason;
    private boolean handled;
    private UUID target;
    private UUID executor;

}
