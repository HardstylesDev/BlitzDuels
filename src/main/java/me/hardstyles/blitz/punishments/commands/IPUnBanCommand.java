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

public class IPUnBanCommand extends Command {
    private final PunishmentManager punishmentManager;

    public IPUnBanCommand() {
        super("unbanip", Lists.newArrayList("unblacklist"), 7);
        punishmentManager = Core.i().getPunishmentManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length > 0) {
            String executorDisplay = iPlayer.getRank().getChatColor() + iPlayer.getName();
            String punishedDisplay = punishmentManager.getDisplay(args[0]);

            if (!punishmentManager.isIPBanned(punishmentManager.getIP(punishmentManager.getUUID(args[0])))) {
                p.sendMessage("§cThat player is not ip banned!");
                return;
            }

            String reason;

            if (args.length > 1) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    builder.append(args[i]).append(" ");
                }
                reason = builder.toString().trim();
            } else reason = "None";

            PunishmentInfo info = new PunishmentInfo(PType.IPBAN, true, -1, reason, p.getName(),
                    args[0], executorDisplay, punishedDisplay);

            punishmentManager.remove(punishmentManager.getUUID(args[0]), info);
        } else {
            p.sendMessage("§cUsage: /ipunban <player> [reason...]");
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String executorDisplay = "§4§lConsole";
            String punishedDisplay = punishmentManager.getDisplay(args[0]);

            if (!punishmentManager.isIPBanned(punishmentManager.getIP(punishmentManager.getUUID(args[0])))) {
                sender.sendMessage("§cThat player is not ip banned!");
                return;
            }

            String reason;

            if (args.length > 1) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    builder.append(args[i]).append(" ");
                }
                reason = builder.toString().trim();
            } else reason = "None";

            PunishmentInfo info = new PunishmentInfo(PType.IPBAN, true, -1, reason, "Console",
                    args[0], executorDisplay, punishedDisplay);

            punishmentManager.remove(punishmentManager.getUUID(args[0]), info);
        } else {
            sender.sendMessage("§cUsage: /ipunban <player> [reason...]");
        }
    }
}
