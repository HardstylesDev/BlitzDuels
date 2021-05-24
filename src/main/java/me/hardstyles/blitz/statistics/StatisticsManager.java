package me.hardstyles.blitz.statistics;

import com.google.gson.*;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.nickname.Nick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StatisticsManager {

   final private Core core;
    public StatisticsManager(Core core) {
        this.core = core;
    }

    public void save() {
        for (IPlayer bsgPlayer : Core.getInstance().getPlayerManager().getBsgPlayers().values()) {
            save(bsgPlayer);
        }
    }

    public void saveAsync(IPlayer e) {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            this.save(e);

        });
    }
    public void saveAsync(Player e) {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            this.save(core.getPlayerManager().getPlayer(e.getUniqueId()));

        });
    }
    public void save(IPlayer bsgPlayer) {
        JsonObject jsonObject = bsgPlayer.getJsonObject();
        jsonObject.addProperty("uuid", bsgPlayer.getUuid().toString());
        jsonObject.addProperty("name", bsgPlayer.getName());
        jsonObject.addProperty("ip_adress", bsgPlayer.getIp());
        if (bsgPlayer.getRank() != null)
            jsonObject.addProperty("rank", bsgPlayer.getRank().getRank());
        jsonObject.addProperty("kills", bsgPlayer.getKills());
        jsonObject.addProperty("wins", bsgPlayer.getWins());
        jsonObject.addProperty("deaths", bsgPlayer.getDeaths());
        jsonObject.addProperty("coins", bsgPlayer.getCoins());
        jsonObject.addProperty("elo", bsgPlayer.getElo());
        jsonObject.addProperty("streak", bsgPlayer.getStreak());

        jsonObject.addProperty("ffa_deaths", bsgPlayer.getFfaDeaths());
        jsonObject.addProperty("ffa_kills", bsgPlayer.getFfaKills());
        jsonObject.addProperty("ffa_streak", bsgPlayer.getFfaStreak());
       if(bsgPlayer.doesHideOthers())
            jsonObject.addProperty("hide_others",bsgPlayer.doesHideOthers());

        if (bsgPlayer.getCustomTag() != null)
            jsonObject.addProperty("custom_tag", bsgPlayer.getCustomTag());


        if (bsgPlayer.getNick() != null) {
            jsonObject.add("nick", nickToJson(bsgPlayer));

        }

        bsgPlayer.setJsonObject(jsonObject);
        insert(bsgPlayer.getUuid().toString(), jsonObject);

    }


    private JsonObject nickToJson(IPlayer p) {
        JsonObject j = new JsonObject();
        j.addProperty("name", p.getNick().getNickName());
        j.addProperty("value", p.getNick().getSkinValue());
        j.addProperty("signature", p.getNick().getSkinSignature());
        j.addProperty("nicked", p.getNick().isNicked());
        return j;

    }


    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private void insert(String uuid, JsonObject jsonObject) {
        try {
            Connection connection = core.getData().getConnection();
            String command = String.format("REPLACE INTO `data`(`uuid`, `data`) VALUES (?,?)");
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, gson.toJson(jsonObject));
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    public void load() {
        try {
            Connection conn = core.getData().getConnection();
            String sql = "select * from data;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                IPlayer iPlayer = new IPlayer(UUID.fromString(rs.getString("uuid")));
                if (iPlayer == null)
                    continue;
                JsonObject jsonObject = new JsonParser().parse(rs.getString("data")).getAsJsonObject();

                if (jsonObject.has("name")) iPlayer.setName(jsonObject.get("name").getAsString());
                if (jsonObject.has("rank"))
                    iPlayer.setRank(core.getRankManager().getRankByName(jsonObject.get("rank").getAsString()));
                iPlayer.setKills(jsonObject.get("kills").getAsInt());
                iPlayer.setDeaths(jsonObject.get("deaths").getAsInt());
                iPlayer.setWins(jsonObject.get("wins").getAsInt());
                iPlayer.setCoins(jsonObject.get("coins").getAsInt());
                iPlayer.setStreak(jsonObject.get("streak").getAsInt());
                iPlayer.setElo(jsonObject.get("elo").getAsInt());
                if(jsonObject.has("hide_others"))
                    iPlayer.setHideOthers(jsonObject.get("hide_others").getAsBoolean());
                if(jsonObject.has("custom_tag"))
                    iPlayer.setCustomTag(jsonObject.get("custom_tag").getAsString());
                //if (rs.getString("nickname") != null && !rs.getString("nickname").equalsIgnoreCase("")) {
                //    blitzSGPlayer.setNick(new Nick(rs.getString("nickname"), null, null, !rs.getString("nickname").equalsIgnoreCase("")));
                //}
                 if (jsonObject.has("nick") && jsonObject.get("nick") != null && jsonObject.get("nick").getAsJsonObject() != null && jsonObject.get("nick").getAsJsonObject().has("name") && jsonObject.get("nick").getAsJsonObject() != null && jsonObject.get("nick").getAsJsonObject().get("value") != null && jsonObject.get("nick").getAsJsonObject().get("signature") != null) {
                    iPlayer.setNick(new Nick(jsonObject.get("nick").getAsJsonObject().get("name").getAsString(), jsonObject.get("nick").getAsJsonObject().get("value").getAsString(), jsonObject.get("nick").getAsJsonObject().get("signature").getAsString(), jsonObject.get("nick").getAsJsonObject().get("nicked").getAsBoolean()));
                }

            }
            rs.close();
            ps.close();
            conn.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }


    public void load(UUID uuid) {
        try {
            Connection conn = core.getData().getConnection();
            String sql = "select * from data WHERE uuid = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                IPlayer iPlayer = new IPlayer(UUID.fromString(rs.getString("uuid")));
                if (iPlayer == null)
                    continue;
                JsonObject jsonObject = new JsonParser().parse(rs.getString("data")).getAsJsonObject();


                if (jsonObject.has("name")) iPlayer.setName(jsonObject.get("name").getAsString());
                if (jsonObject.has("rank"))
                    iPlayer.setRank(core.getRankManager().getRankByName(jsonObject.get("rank").getAsString()));

                iPlayer.setKills(jsonObject.get("kills").getAsInt());
                iPlayer.setDeaths(jsonObject.get("deaths").getAsInt());
                iPlayer.setWins(jsonObject.get("wins").getAsInt());
                iPlayer.setStreak(jsonObject.has("streak") ? jsonObject.get("streak").getAsInt() : 0);
                iPlayer.setCoins(jsonObject.get("coins").getAsInt());
                iPlayer.setElo(jsonObject.get("elo").getAsInt());
                if(jsonObject.has("custom_tag"))
                    iPlayer.setCustomTag(jsonObject.get("custom_tag").getAsString());
                if(jsonObject.has("hide_others"))
                    iPlayer.setHideOthers(jsonObject.get("hide_others").getAsBoolean());

                if (jsonObject.has("nick") && jsonObject.get("nick") != null && jsonObject.get("nick").getAsJsonObject() != null && jsonObject.get("nick").getAsJsonObject().has("name") && jsonObject.get("nick").getAsJsonObject() != null && jsonObject.get("nick").getAsJsonObject().get("value") != null && jsonObject.get("nick").getAsJsonObject().get("signature") != null) {
                    iPlayer.setNick(new Nick(jsonObject.get("nick").getAsJsonObject().get("name").getAsString(), jsonObject.get("nick").getAsJsonObject().get("value").getAsString(), jsonObject.get("nick").getAsJsonObject().get("signature").getAsString(), jsonObject.get("nick").getAsJsonObject().get("nicked").getAsBoolean()));
                }

                iPlayer.setFfaDeaths(asInt(jsonObject, "ffa_deaths"));
                iPlayer.setFfaKills(asInt(jsonObject, "ffa_kills"));
                iPlayer.setFfaStreak(asInt(jsonObject, "ffa_streak"));

            }
            rs.close();
            ps.close();
            conn.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }
    private int asInt(JsonObject j,String v){
        if(j.has(v)){
            return j.get(v).getAsInt();
        }
        return 0;
    }

}
