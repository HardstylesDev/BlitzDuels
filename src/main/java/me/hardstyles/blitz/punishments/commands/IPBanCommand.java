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

public class IPBanCommand extends Command {
    private final PunishmentManager punishmentManager;
    public IPBanCommand() {
        super("ipban", Lists.newArrayList("blacklist"), 7);
        punishmentManager = Core.i().getPunishmentManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length > 1) {
            if (!punishmentManager.hasData(args[0])) {
                p.sendMessage("§cThat player has never joined!");
                return;
            }

            String executorDisplay = iPlayer.getRank().getChatColor() + iPlayer.getName();
            String punishedDisplay = punishmentManager.getDisplay(args[0]);

            if (punishmentManager.isIPBanned(punishmentManager.getIP(punishmentManager.getUUID(args[0])))) {
                p.sendMessage("§cTarget is already ip banned!");
                return;
            }

            StringBuilder reason = new StringBuilder(args[1]);

            for (int i = 2; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(PType.IPBAN, false, -1, reason.toString(), p.getName(),
                    args[0], executorDisplay, punishedDisplay);

            punishmentManager.punish(info);
        } else {
            p.sendMessage("§cUsage: /ipban <player> <reason...>");
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 1) {
            if (!punishmentManager.hasData(args[0])) {
                sender.sendMessage("§cThat player has never joined!");
                return;
            }

            String executorDisplay = "§4§lConsole";
            String punishedDisplay = punishmentManager.getDisplay(args[0]);

            if (punishmentManager.isIPBanned(punishmentManager.getIP(punishmentManager.getUUID(args[0])))) {
                sender.sendMessage("§cTarget is already ip banned!");
                return;
            }

            StringBuilder reason = new StringBuilder(args[1]);

            for (int i = 2; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(PType.IPBAN, false, -1, reason.toString(), "Console",
                    args[0], executorDisplay, punishedDisplay);

            punishmentManager.punish(info);
        } else {
            sender.sendMessage("§cUsage: /ipban <player> <reason...>");
        }
    }
}
