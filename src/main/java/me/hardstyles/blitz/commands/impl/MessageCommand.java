package me.hardstyles.blitz.commands.impl;

import com.google.common.collect.ImmutableList;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageCommand extends Command {
    private final Map<Player, Player> replying = new HashMap<>();

    public MessageCommand() {
        super("message", ImmutableList.of("msg", "t", "tell", "w", "whisper"), 0);

        new Command("reply", ImmutableList.of("r"), 0) {

            @Override
            public List<String> onTabComplete(Player player, String[] args) {
                return ImmutableList.of();
            }

            @Override
            public void onExecute(Player p, IPlayer iPlayer, String[] args) {
                if (args.length > 0) {
                    if (!replying.containsKey(p)) {
                        p.sendMessage("§cYou have nobody to reply to.");
                    } else if (replying.get(p) == null) {
                        p.sendMessage("§cThat player is no longer online.");
                    } else {
                        StringBuilder builder = new StringBuilder();
                        for (String arg : args) {
                            builder.append(" ").append(arg);
                        }
                        p.performCommand("msg " + replying.get(p).getName() + builder.toString());
                    }
                } else {
                    p.sendMessage("§cUsage: /reply <player>");
                }
            }
        };
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return args.length == 1 ? null : ImmutableList.of();
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length > 1) {
            Player t = Bukkit.getPlayer(args[0]);
            IPlayer target = t == null ? null : Core.i().getPlayerManager().getPlayer(t.getUniqueId());
            if (target == null) {
                p.sendMessage("§cThat player is not online.");
            } else if (target.getIgnoreList().contains(p.getUniqueId().toString())) {
                p.sendMessage("§cThat player has you ignored.");
            } else {
                StringBuilder builder = new StringBuilder(args[1]);
                for (int i = 2; i < args.length; i++) {
                    builder.append(" ").append(args[i]);
                }
                String message = builder.toString();
                p.sendMessage("§7(To " + target.getRank().getPrefix() + target.getName() + "§7) " + message);
                t.sendMessage("§7(From " + iPlayer.getRank().getPrefix() + p.getName() + "§7) " + message);
                t.playSound(t.getLocation(), Sound.ORB_PICKUP, 1, 1.1f);
                replying.put(p, t);
                replying.put(t, p);
            }
        } else {
            p.sendMessage("§cUsage: /msg <player> <message..>");
        }
    }
}
