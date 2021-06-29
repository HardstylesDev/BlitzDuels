package me.hardstyles.blitz.staff.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class ReportEntry {
    private final long time = System.currentTimeMillis();
    private final String reason, reported, reporter;
}
