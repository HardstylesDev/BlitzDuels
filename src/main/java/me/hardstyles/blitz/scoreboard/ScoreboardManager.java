package me.hardstyles.blitz.scoreboard;

import me.hardstyles.blitz.Core;

import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

public class ScoreboardManager extends BukkitRunnable {
    private final ScoreboardHandler scoreboardHandler;
    private final String lines;
    private final String separator;

    public ScoreboardManager() {
        this.scoreboardHandler = new ScoreboardHandler();
        this.lines = "&7&m---------------------";
        this.separator = "&f";
    }

    public void run() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ScoreboardHelper board = this.scoreboardHandler.getScoreboard(p);
            IPlayer bsgPlayer = Core.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
            board.clear();
            Date now = new Date();
            if (true) {
                board.add(separator);
                board.add("Kills: &a" + bsgPlayer.getKills());
                board.add("Wins: &a" + bsgPlayer.getWins());
                board.add("Blitz Score: &c" + bsgPlayer.getElo());
                board.add("Blitz Rank: &cN/A");


                board.add(separator);

                board.add("Coins: &a" + bsgPlayer.getCoins());
                board.add("Unlocks: &cN/A");

                board.add(separator);
                board.add("&ewww.hypixel.net");

            }
            board.update(p);

            if (bsgPlayer.getNick() != null && bsgPlayer.getNick().isNicked()) {
                PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(ChatColor.RED + "You're currently nicked " + ChatColor.GRAY + "(in-game only)"), (byte) 2);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
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
}
