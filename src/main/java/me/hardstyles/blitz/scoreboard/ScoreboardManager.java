package me.hardstyles.blitz.scoreboard;

import lombok.Getter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.match.Match;
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

@Getter
public class ScoreboardManager extends BukkitRunnable {
    private final ScoreboardHandler scoreboardHandler = new ScoreboardHandler();
    private final String lines;
    private final String separator;
    private final Core core;

    public ScoreboardManager(Core core) {
        this.core = core;
        this.lines = "&7&m---------------";
        this.separator = "&f";
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ScoreboardHelper board = this.scoreboardHandler.getScoreboard(p);
            IPlayer bsgPlayer = Core.i().getPlayerManager().getPlayer(p.getUniqueId());
            if(bsgPlayer == null || board == null){
                p.kickPlayer("relog plz plz");
                return;
            }
            board.getList().clear();

            if (bsgPlayer.hasMatch() && (bsgPlayer.getMatch().getMatchStage() == MatchStage.GRACE || bsgPlayer.getMatch().getMatchStage() == MatchStage.STARTED)) {
                Match match = bsgPlayer.getMatch();
                board.add(lines);
                board.add(separator + "&a");
                board.add("Time: " + ChatColor.GREEN + getTime(match));
                board.add(separator + "&b");
                for (UUID uuid : match.getAlivePlayers()) {
                    Player op = match.getPlayerReference().get(uuid);
                    board.add(ChatColor.GRAY + "➥ " + ChatColor.GREEN + op.getName() + " " + Math.floor(op.getHealth()) + ChatColor.RED + "❤");
                }
                for (UUID uuid : match.getDead()) {
                    Player op = match.getPlayerReference().get(uuid);
                    board.add(ChatColor.GRAY + "➥ " + ChatColor.RED + op.getName());
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
                    if (op != null) {
                        board.add(ChatColor.GOLD + "♚ " + ChatColor.YELLOW + op.getName());
                    }
                }

                board.add(separator + "&c");
                board.add(lines);

            } else {
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
            if (bsgPlayer.hasMatch()) {
                bsgPlayer.getMatch().entityTeleport();
            }
            board.update(p);
            core.getTabUtil().setForPlayer(p, "&e&lBLITZ DUELS\n&r", "\n&eIn match: &r" + core.getMatchManager().getMatchCount() + "\n&ePlayers: &r" + Bukkit.getOnlinePlayers().size() + "\n\n&etest.blitzsg.club");
        }
    }

    private String getTime(Match match) {
        if (match.getMatchStage() == MatchStage.GRACE) {
            return "Starting..";
        }

        long seconds = (System.currentTimeMillis() - match.getTimeStarted()) / 1000;
        long minutes = 0;
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }

        return (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }
}
