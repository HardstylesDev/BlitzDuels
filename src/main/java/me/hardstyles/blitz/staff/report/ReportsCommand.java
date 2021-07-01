package me.hardstyles.blitz.staff.report;

import com.google.common.collect.ImmutableList;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Set;

public class ReportsCommand extends Command {

    public ReportsCommand() {
        super("reports", ImmutableList.of(), 6);
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        Set<ReportEntry> reports = Core.i().getStaffManager().getReports();
        Inventory inv = Bukkit.createInventory(null, 54, "Reports");
        int index = 0;
        for (ReportEntry entry : reports) {
            inv.setItem(index++, new ItemBuilder(Material.BOOK).name("&6" + entry.getReported()).lore("&7By: &f" + entry.getReporter()).lore("&7Reason: &f" + entry.getReason()).lore(" ").lore("&8(&7Click to handle&8)").make());
        }
        p.openInventory(inv);
    }
}
