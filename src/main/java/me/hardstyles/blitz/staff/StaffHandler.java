package me.hardstyles.blitz.staff;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.staff.report.ReportEntry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashSet;
import java.util.Set;

public class StaffHandler implements Listener {
    private final StaffManager manager;

    public StaffHandler(Core core) {
        manager = core.getStaffManager();
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("Reports")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BOOK) {
                String name = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                Set<ReportEntry> reports = new HashSet<>();
                for (ReportEntry report : manager.getReports()) {
                    if (report.getReported().equals(name)) {
                        reports.add(report);
                    }
                }
                manager.getReports().removeAll(reports);
                e.getView().close();
                e.getWhoClicked().sendMessage("§aSuccessfully cleared reports on " + name);
            }
        }
    }
}
