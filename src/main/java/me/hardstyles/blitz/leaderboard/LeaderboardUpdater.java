package me.hardstyles.blitz.leaderboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hardstyles.blitz.Core;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LeaderboardUpdater {
    private final Core core;

    public LeaderboardUpdater(Core core) {
        this.core = core;
    }

    public void update() {
        core.getServer().getScheduler().runTaskAsynchronously(core, () -> {
            HashMap<JsonObject, Integer> sorted = getSorted(getKillsData());
            int limiter = 0;
            for (JsonObject jsonObject : sorted.keySet()) {
                String s = ChatColor.YELLOW + "" + ChatColor.BOLD + (limiter + 1) + ChatColor.YELLOW + ". " + core.getRankManager().getRankByName(jsonObject.get("rank").getAsString()).getChatColor() + jsonObject.get("name").getAsString() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "" + ChatColor.BOLD + jsonObject.get("kills").getAsInt();
                core.getLeaderboardLoaderKills().getArmorstands().get(9 - limiter).setCustomName(s);


                limiter++;
                if (limiter == 10) return;

            }
        });

        core.getServer().getScheduler().runTaskAsynchronously(core, () -> {
            HashMap<JsonObject, Integer> sorted = getSorted(getWinsData());
            int limiter = 0;
            for (JsonObject jsonObject : sorted.keySet()) {
                String s = ChatColor.YELLOW + "" + ChatColor.BOLD + (limiter + 1) + ChatColor.YELLOW + ". " + core.getRankManager().getRankByName(jsonObject.get("rank").getAsString()).getChatColor() + jsonObject.get("name").getAsString() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "" + ChatColor.BOLD + jsonObject.get("wins").getAsInt();
                core.getLeaderboardLoaderWins().getArmorstands().get(9 - limiter).setCustomName(s);
                limiter++;
                if (limiter == 10) return;

            }
        });

        core.getServer().getScheduler().runTaskAsynchronously(core, () -> {
            HashMap<JsonObject, Integer> sorted = getSorted(getStreakData());
            int limiter = 0;
            for (JsonObject jsonObject : sorted.keySet()) {
                String s = ChatColor.YELLOW + "" + ChatColor.BOLD + (limiter + 1) + ChatColor.YELLOW + ". " + core.getRankManager().getRankByName(jsonObject.get("rank").getAsString()).getChatColor() + jsonObject.get("name").getAsString() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "" + ChatColor.BOLD + jsonObject.get("streak").getAsInt();
                core.getLeaderboardLoaderStreak().getArmorstands().get(9 - limiter).setCustomName(s);
                limiter++;
                if (limiter == 10) return;

            }
        });

    }

    private HashMap<JsonObject, Integer> getSorted(HashMap<JsonObject, Integer> unSortedMap) {
        LinkedHashMap<JsonObject, Integer> reverseSortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }


    private HashMap<JsonObject, Integer> getKillsData() {
        HashMap<JsonObject, Integer> objects = new HashMap<>();

        try {
            Connection conn = Core.getInstance().getData().getConnection();
            String sql = "select * from data;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JsonObject jsonObject = new JsonParser().parse(rs.getString("data")).getAsJsonObject();
                objects.put(jsonObject, jsonObject.get("kills").getAsInt());
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return objects;
    }

    private HashMap<JsonObject, Integer> getWinsData() {
        HashMap<JsonObject, Integer> objects = new HashMap<>();

        try {
            Connection conn = Core.getInstance().getData().getConnection();
            String sql = "select * from data;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JsonObject jsonObject = new JsonParser().parse(rs.getString("data")).getAsJsonObject();
                objects.put(jsonObject, jsonObject.get("wins").getAsInt());
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return objects;
    }
    private HashMap<JsonObject, Integer> getStreakData() {
        HashMap<JsonObject, Integer> objects = new HashMap<>();
        try {
            Connection conn = Core.getInstance().getData().getConnection();
            String sql = "select * from data;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JsonObject jsonObject = new JsonParser().parse(rs.getString("data")).getAsJsonObject();
                if(jsonObject.has("streak")) {
                    objects.put(jsonObject, jsonObject.get("streak").getAsInt());
                }
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return objects;
    }
}
