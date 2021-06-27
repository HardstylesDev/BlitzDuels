package me.hardstyles.blitz.punishments.commands;

import com.google.common.collect.Lists;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.punishments.PType;
import me.hardstyles.blitz.punishments.redis.PunishmentInfo;
import me.hardstyles.blitz.punishments.redis.RedisManager;
import me.hardstyles.blitz.utils.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KickCommand extends Command {
    private final RedisManager redisManager;

    public KickCommand() {
        super("kick", Lists.newArrayList(), 6);
        redisManager = Core.i().getRedisManager();
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length > 1) {
            Player t = Bukkit.getPlayerExact(args[0]);
            IPlayer target = t == null ? null : Core.i().getPlayerManager().getPlayer(t.getUniqueId());

            if (target == null) {
                p.sendMessage("§cThat player is not online!");
                return;
            }

            String executorDisplay = iPlayer.getRank().getChatColor() + iPlayer.getName();
            String punishedDisplay = target.getRank().getChatColor() + target.getName();

            StringBuilder reason = new StringBuilder(args[1]);

            for (int i = 2; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(PType.KICK, false, -1, reason.toString(), p.getName(), target.getName(), executorDisplay, punishedDisplay);
            redisManager.getPubSubSender().async().publish("PUNISHMENT", redisManager.getGSON().toJson(info));

        } else {
            p.sendMessage("§cUsage: /kick <player> <reason...>");
        }
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        if (args.length > 1) {
            Player t = Bukkit.getPlayerExact(args[0]);
            IPlayer target = t == null ? null : Core.i().getPlayerManager().getPlayer(t.getUniqueId());

            if (target == null) {
                sender.sendMessage("§cThat player is not online!");
                return;
            }

            String executorDisplay = "§4§lConsole";
            String punishedDisplay = target.getRank().getChatColor() + target.getName();

            StringBuilder reason = new StringBuilder(args[1]);

            for (int i = 2; i < args.length; i++) {
                reason.append(" ").append(args[i]);
            }

            PunishmentInfo info = new PunishmentInfo(PType.KICK, false, -1, reason.toString(), "Console", target.getName(), executorDisplay, punishedDisplay);
            redisManager.getPubSubSender().async().publish("PUNISHMENT", redisManager.getGSON().toJson(info));

        } else {
            sender.sendMessage("§cUsage: /kick <player> <reason...>");
        }
    }
}
