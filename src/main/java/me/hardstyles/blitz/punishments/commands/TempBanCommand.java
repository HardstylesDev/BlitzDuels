package me.hardstyles.blitz.punishments.commands;

import com.google.common.collect.Lists;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.punishments.PType;
import me.hardstyles.blitz.punishments.PunishmentManager;
import me.hardstyles.blitz.punishments.redis.PunishmentInfo;
import me.hardstyles.blitz.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TempBanCommand extends Command {
    private final PunishmentManager punishmentManager;

    public TempBanCommand() {
        super("tempban", Lists.newArrayList(), 7);
        punishmentManager = Core.i().getPunishmentManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length > 2) {
            if (!punishmentManager.hasData(args[0])) {
                p.sendMessage("§cThat player has never joined!");
                return;
            }

            String executorDisplay = iPlayer.getRank().getChatColor() + iPlayer.getName();
            String punishedDisplay = punishmentManager.getDisplay(args[0]);

            if (punishmentManager.isPunished(args[0], PType.BAN)) {
                p.sendMessage("§c" + args[0] + " is already banned!");
                return;
            }

            long duration;

            try {
                duration = punishmentManager.parseDate(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage("§cInvalid time: " + args[1]);
                return;
            }

            StringBuilder reason = new StringBuilder(args[2]);

            for (int i = 3; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(PType.BAN, false, duration, reason.toString(), p.getName(),
                    args[0], executorDisplay, punishedDisplay);

            punishmentManager.punish(info);
        } else {
            p.sendMessage("§cUsage: /tempban <player> <time> <reason...>");
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 2) {
            if (!punishmentManager.hasData(args[0])) {
                sender.sendMessage("§cThat player has never joined!");
                return;
            }

            String executorDisplay = "§4§lConsole";
            String punishedDisplay = punishmentManager.getDisplay(args[0]);

            if (punishmentManager.isPunished(args[0], PType.BAN)) {
                sender.sendMessage("§c" + args[0] + " is already banned!");
                return;
            }

            long duration;

            try {
                duration = punishmentManager.parseDate(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cPlease provide a valid date! example: 30d");
                return;
            }

            StringBuilder reason = new StringBuilder(args[2]);

            for (int i = 3; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(PType.BAN, false, duration, reason.toString(), "Console",
                    args[0], executorDisplay, punishedDisplay);

            punishmentManager.punish(info);
        } else {
            sender.sendMessage("§cUsage: /tempban <player> <time> <reason...>");
        }
    }
}


