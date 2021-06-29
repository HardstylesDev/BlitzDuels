package me.hardstyles.blitz.scoreboard;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Preconditions;

@Getter
public class ScoreboardHelper {
    private final List<ScoreboardText> list;
    private final Scoreboard scoreBoard;
    private final Objective objective;
    private final String tag;
    private int lastSentCount = -1;
    
    public ScoreboardHelper(final Scoreboard scoreBoard, final String title) {
        this.list = new ArrayList<>();
        Preconditions.checkState(title.length() <= 32, "title can not be more than 32");
        this.tag = ChatColor.translateAlternateColorCodes('&', title);
        this.scoreBoard = scoreBoard;
       (this.objective = this.getOrCreateObjective(this.tag, "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void add(String paramString) {
        paramString = ChatColor.translateAlternateColorCodes('&', paramString);
        ScoreboardText localScoreboardInput;
        if (paramString.length() <= 16) {
            localScoreboardInput = new ScoreboardText(paramString, "");
        }
        else {
            String str1 = paramString.substring(0, 16);
            String str2 = paramString.substring(16);
            if (str1.endsWith("§")) {
                str1 = str1.substring(0, str1.length() - 1);
                str2 = "§" + str2;
            }
            String str3 = ChatColor.getLastColors(str1);
            str2 = str3 + str2;
            localScoreboardInput = new ScoreboardText(str1, StringUtils.left(str2, 16));
        }
        this.list.add(localScoreboardInput);
    }
    
    public void update(final Player paramPlayer) {
        paramPlayer.setScoreboard(this.scoreBoard);
        for (int i = 0; i < this.list.size(); ++i) {
            Team localTeam = this.getOrCreateTeam(String.valueOf(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14)))) + i, i);
            ScoreboardText localScoreboardInput = this.list.get(this.list.size() - i - 1);
            localTeam.setPrefix(localScoreboardInput.getLeft());
            localTeam.setSuffix(localScoreboardInput.getRight());
            this.objective.getScore(this.getNameForIndex(i)).setScore(i + 1);
        }
        if (this.lastSentCount != -1) {
            for (int i = this.list.size(), j = 0; j < this.lastSentCount - i; ++j) {
                this.remove(i + j);
            }
        }
        this.lastSentCount = this.list.size();
    }
    
    public void remove(final int paramInt) {
        final String str = this.getNameForIndex(paramInt);
        this.scoreBoard.resetScores(str);
        final Team localTeam = this.getOrCreateTeam(String.valueOf(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14)))) + paramInt, paramInt);
        localTeam.unregister();
    }
    
    public Team getOrCreateTeam(final String team, final int i) {
        Team value = this.scoreBoard.getTeam(team);
        if (value == null) {
            value = this.scoreBoard.registerNewTeam(team);
            value.addEntry(this.getNameForIndex(i));
        }
        return value;
    }
    
    public Objective getOrCreateObjective(final String objective, final String type) {
        Objective value = this.scoreBoard.getObjective(objective);
        if (value == null) {
            value = this.scoreBoard.registerNewObjective(objective, type);
        }
        value.setDisplayName(objective);
        return value;
    }
    
    public String getNameForIndex(final int index) {
        return String.valueOf(String.valueOf(ChatColor.values()[index].toString())) + ChatColor.RESET;
    }

    @Getter
    @AllArgsConstructor
    public static class ScoreboardText {
        private final String left;
        private final String right;
    }
}
