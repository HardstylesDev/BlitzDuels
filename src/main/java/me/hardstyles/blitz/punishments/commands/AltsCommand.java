package me.hardstyles.blitz.punishments.commands;

import com.google.common.collect.Lists;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.punishments.PType;
import me.hardstyles.blitz.punishments.PunishmentManager;
import me.hardstyles.blitz.utils.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AltsCommand extends Command {
    private final PunishmentManager punishmentManager;

    public AltsCommand() {
        super("alts", Lists.newArrayList("ipcheck", "accounts"), 6);
        punishmentManager = Core.i().getPunishmentManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length == 1) {
            if (punishmentManager.hasData(args[0])) {
                List<String> alts = punishmentManager.getAccounts(punishmentManager.getIP(punishmentManager.getUUID(args[0])));
                p.sendMessage("§4§lStaff §8⏐ §7Showing accounts on §f" + punishmentManager.getName(punishmentManager.getUUID(args[0])) + "'s §7IP:");
                for (String alt : alts) {
                    p.sendMessage("§7- §f" + alt +
                            (punishmentManager.isPunished(alt, PType.BAN) ? " §7[§4Banned§7]" : ""));
                }
            } else {
                p.sendMessage("§cThat player has never joined!");
            }
        } else {
            p.sendMessage("§cUsage: /alts <player>");
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (punishmentManager.hasData(args[0])) {
                List<String> alts = punishmentManager.getAccounts(punishmentManager.getIP(punishmentManager.getUUID(args[0])));
                sender.sendMessage("§4§lStaff §8⏐ §7Showing accounts on §f" + punishmentManager.getName(punishmentManager.getUUID(args[0])) + "'s §7IP:");
                for (String alt : alts) {
                    sender.sendMessage("§7- §f" + alt +
                            (punishmentManager.isPunished(alt, PType.BAN) ? " §7[§4Banned§7]" : ""));
                }
            } else {
                sender.sendMessage("§cThat player has never joined!");
            }
        } else {
            sender.sendMessage("§cUsage: /alts <player>");
        }
    }
}
