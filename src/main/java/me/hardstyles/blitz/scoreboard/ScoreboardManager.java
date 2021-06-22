package me.hardstyles.blitz.scoreboard;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.Match;
import me.hardstyles.blitz.match.MatchStage;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ScoreboardManager extends BukkitRunnable {
    private final ScoreboardHandler scoreboardHandler;
    private final String lines;
    private final String separator;
    private final Core core;

    public ScoreboardManager(Core core) {
        this.core = core;
        this.scoreboardHandler = new ScoreboardHandler();
        this.lines = "&7&m---------------";
        this.separator = "&f";
    }

    public void run() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ScoreboardHelper board = this.scoreboardHandler.getScoreboard(p);
            IPlayer bsgPlayer = Core.i().getPlayerManager().getPlayer(p.getUniqueId());
            if(bsgPlayer == null){
                p.kickPlayer("relog plz plz");
            }
            board.clear();

            if (bsgPlayer.hasMatch() && bsgPlayer.getMatch().getMatchStage() == MatchStage.GRACE) {
                Match match = bsgPlayer.getMatch();
                board.add(lines);
                board.add(separator + "&a");
                board.add("Time: " + ChatColor.GREEN + "starting...");
                board.add(separator + "&b");
                for (UUID uuid : match.getAlivePlayers()) {
                    Player op = match.getPlayerReference().get(uuid);
                    board.add(ChatColor.GRAY + "➥ " + ChatColor.GREEN + op.getName() + " " + Math.round(op.getHealth()) + ChatColor.RED + "❤");
                }
                for (UUID uuid : match.getDead()) {
                    Player op = match.getPlayerReference().get(uuid);
                    board.add(ChatColor.GRAY + "➥ " + ChatColor.GRAY + op.getName());
                }
                board.add(separator + "&c");
                board.add(lines);

            } else if (bsgPlayer.hasMatch() && bsgPlayer.getMatch().getMatchStage() == MatchStage.STARTED) {
                Match match = bsgPlayer.getMatch();
                board.add(lines);
                board.add(separator + "&a");
                board.add("Time: " + ChatColor.GREEN + ((System.currentTimeMillis() - match.getTimeStarted()) / 1000) + "s");
                board.add(separator + "&b");
                for (UUID uuid : match.getAlivePlayers()) {
                    Player op = match.getPlayerReference().get(uuid);
                    board.add(ChatColor.GRAY + "➥ " + ChatColor.GREEN + op.getName() + " " + Math.round(op.getHealth()) + ChatColor.RED + "❤");
                }
                for (UUID uuid : match.getDead()) {
                    Player op = match.getPlayerReference().get(uuid);
                    board.add(ChatColor.GRAY + "➥ " + ChatColor.GRAY + op.getName());
                }
                board.add(separator + "&c");
                board.add(lines);

            } else if (bsgPlayer.hasMatch() && bsgPlayer.getMatch().getMatchStage() == MatchStage.ENDED) {
                Match match = bsgPlayer.getMatch();
                board.add(lines);
                board.add(separator + "&a");
                board.add("Time: " + ChatColor.GREEN + ((match.getTimeEnded() - match.getTimeStarted()) / 1000) + "s");
                board.add(separator + "&b");
                board.add(match.getWinners().size() == 1 ? "Winner" : "Winners");
                for (UUID winner : match.getWinners()) {
                    Player op = match.getPlayerReference().get(winner);
                    board.add(ChatColor.GOLD + "♚ " + ChatColor.YELLOW + op.getName());

                }

                board.add(separator + "&c");
                board.add(lines);

            }  else {
                board.add(lines);
                board.add(separator + "&c");
                board.add("Kills: &a" + bsgPlayer.getKills());
                board.add("Wins: &a" + bsgPlayer.getWins());
                board.add("Streak: &a" + bsgPlayer.getStreak());
                board.add(separator);
                board.add("Blitz Score: &c" + bsgPlayer.getElo());
                board.add("Blitz Rank: &cN/A");


                board.add(separator);

                board.add(lines);

                if (bsgPlayer.getNick() != null && bsgPlayer.getNick().isNicked()) {
                    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(ChatColor.RED + "You're currently nicked " + ChatColor.GRAY + "(in-game only)"), (byte) 2);
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                }
            }
            if(bsgPlayer.hasMatch()){
                bsgPlayer.getMatch().entityTeleport();
            }
            board.update(p);
            core.getTabUtil().setForPlayer(p, "&e&lBLITZ DUELS\n&r", "\n&eIn match: &r" + core.getMatchManager().getMatchCount() + "\n&ePlayers: &r" + Bukkit.getOnlinePlayers().size() + "\n\n&etest.blitzsg.club");



        }
    }

    public static String convert(final int seconds) {
        final int h = seconds / 3600;
        final int i = seconds - h * 3600;
        final int m = i / 60;
        final int s = i - m * 60;
        String timeF = "";


        if (m < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + m + ":";
        if (s < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + s;
        return timeF;
    }

    private String format(final double data) {
        final int minutes = (int) (data / 60.0);
        final int seconds = (int) (data % 60.0);
        final String str = String.format("%02d:%02d", minutes, seconds);
        return str;
    }


    public ScoreboardHandler getScoreboardHandler() {
        return this.scoreboardHandler;
    }

    private double roundHalfDown(double d) {
        double f = 0.5;
        return f * Math.round(d / f);
    }
}
