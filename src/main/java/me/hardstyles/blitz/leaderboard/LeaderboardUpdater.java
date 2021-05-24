package me.hardstyles.blitz.leaderboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hardstyles.blitz.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardUpdater {
    private final Core core;
    public LeaderboardUpdater(Core core){
        this.core = core;
    }
    public void update() {
        core.getServer().getScheduler().runTaskAsynchronously(core, () -> {
            HashMap<JsonObject, Integer> sorted = getSorted(getData());
            int limiter = 0;
            for (JsonObject jsonObject : sorted.keySet()) {
                String s = ChatColor.YELLOW + "" + ChatColor.BOLD + (limiter+1) + ChatColor.YELLOW + ". " + core.getRankManager().getRankByName(jsonObject.get("rank").getAsString()).getChatColor() + jsonObject.get("name").getAsString() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "" + ChatColor.BOLD+ jsonObject.get("kills").getAsInt();
                core.getLeaderboardLoader().getArmorstands().get(9 - limiter).setCustomName(s);


                limiter++;
                if(limiter == 10) return;

            }
          //  for (Integer integer : core.getLeaderboardLoader().getArmorstands().keySet()) {
          //      core.getLeaderboardLoader().getArmorstands().get(integer).setCustomName("Int: " + integer + " ");
          //  }
          //  for (JsonObject jsonObject : sorted.keySet()) {
          //      System.out.println(jsonObject.get("name").getAsString() + " kills: " + jsonObject.get("kills").getAsInt());
          //  }
        });
    }

    private HashMap<JsonObject, Integer> getSorted(HashMap<JsonObject, Integer> unSortedMap) {
        LinkedHashMap<JsonObject, Integer> reverseSortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }




    private HashMap<JsonObject, Integer> getData(){
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
}