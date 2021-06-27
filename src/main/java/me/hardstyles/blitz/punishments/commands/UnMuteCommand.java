package me.hardstyles.blitz.punishments.commands;

import com.google.common.collect.Lists;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.punishments.PType;
import me.hardstyles.blitz.punishments.PunishmentManager;
import me.hardstyles.blitz.punishments.redis.PunishmentInfo;
import me.hardstyles.blitz.utils.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnMuteCommand extends Command {
    private final PunishmentManager punishmentManager;

    public UnMuteCommand() {
        super("unmute", Lists.newArrayList(), 6);
        punishmentManager = Core.i().getPunishmentManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length > 0) {
            if (punishmentManager.isPunished(args[0], PType.MUTE)) {
                String reason;

                if (args.length > 1) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }
                    reason = builder.toString().trim();
                } else reason = "None";

                String display = iPlayer.getRank().getChatColor() + iPlayer.getName();

                punishmentManager.remove(punishmentManager.getUUID(args[0]), new PunishmentInfo(
                        PType.MUTE, true, -1, reason, p.getName(), args[0], display, punishmentManager.getDisplay(args[0])));
            } else {
                p.sendMessage("§cThat player is not muted!");
            }
        } else {
            p.sendMessage("§cUsage: /unmute <player> <reason...>");
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (punishmentManager.isPunished(args[0], PType.MUTE)) {
                String reason;

                if (args.length > 1) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }
                    reason = builder.toString().trim();
                } else reason = "None";

                String display = "§4§lConsole";

                punishmentManager.remove(punishmentManager.getUUID(args[0]), new PunishmentInfo(
                        PType.MUTE, true, -1, reason, "Console", args[0], display, punishmentManager.getDisplay(args[0])));
            } else {
                sender.sendMessage("§cThat player is not muted!");
            }
        } else {
            sender.sendMessage("§cUsage: /unmute <player> [reason...]");
        }
    }
}
