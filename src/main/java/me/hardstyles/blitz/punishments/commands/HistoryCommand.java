package me.hardstyles.blitz.punishments.commands;

import com.google.common.collect.Lists;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.punishments.PunishmentManager;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HistoryCommand extends Command {
    private final ItemStack filler = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(3).name(" ").make();
    private final PunishmentManager punishmentManager;

    public HistoryCommand() {
        super("history", Lists.newArrayList("h", "c"), 6);
        punishmentManager = Core.i().getPunishmentManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length > 0) {
            if (!punishmentManager.hasData(args[0])) {
                p.sendMessage("§cThat player has never joined!");
                return;
            }
            String name = punishmentManager.getName(punishmentManager.getUUID(args[0]));
            Inventory inv = Bukkit.createInventory(null, 36, "§eHistory: §6" + name);
            for (int i = 0; i < 36; i++) {
                inv.setItem(i, filler);
            }
            inv.setItem(12, new ItemBuilder(Material.BOOK).name("§6§lMutes").make());
            inv.setItem(13, new ItemBuilder(Material.BOOK).name("§6§lBans").make());
            inv.setItem(14, new ItemBuilder(Material.BOOK).name("§6§lIP Bans").make());
            inv.setItem(31, new ItemBuilder(Material.NETHER_STAR).name("§7» §a§lExit §7«").make());
            p.openInventory(inv);
        } else {
            p.sendMessage("§cUsage: /history <player>");
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        sender.sendMessage("§cThis command uses a GUI and is not supported by console.");
    }
}

