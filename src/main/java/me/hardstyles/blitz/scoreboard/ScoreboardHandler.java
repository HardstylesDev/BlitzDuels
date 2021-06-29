package me.hardstyles.blitz.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ScoreboardHandler implements Listener {
    private final Map<Player, ScoreboardHelper> boardHelper;
    
    public ScoreboardHandler() {
        this.boardHelper = new HashMap<>();
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            this.handleScoreboard(player);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.handleScoreboard(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.boardHelper.remove(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.boardHelper.remove(event.getPlayer());
    }
    
    private void handleScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(board);
        ScoreboardHelper helper = new ScoreboardHelper(board, "&e&lBLITZ DUELS");
        this.boardHelper.put(player, helper);
    }
    
    public ScoreboardHelper getScoreboard(final Player player) {
        return this.boardHelper.get(player);
    }
}
