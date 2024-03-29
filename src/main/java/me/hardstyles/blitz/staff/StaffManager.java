package me.hardstyles.blitz.staff;

import lombok.Getter;
import me.hardstyles.blitz.staff.report.ReportEntry;

import java.util.HashSet;

@Getter
public class StaffManager {
    private final HashSet<ReportEntry> reports = new HashSet<>();
}
