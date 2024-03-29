package me.hardstyles.blitz.statistics;

import com.google.gson.*;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.nickname.Nick;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StatisticsManager {
    private final Core core;

    public StatisticsManager(Core core) {
        this.core = core;
    }

    public void saveAll() {
        for (IPlayer bsgPlayer : Core.i().getPlayerManager().getPlayers().values()) {
            save(bsgPlayer);
        }
    }

    public void saveAsync(IPlayer e) {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> this.save(e));
    }

    public void save(IPlayer iPlayer) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", iPlayer.getUuid().toString());
        jsonObject.addProperty("name", iPlayer.getName());
        jsonObject.addProperty("ip_adress", iPlayer.getIp());
        if (iPlayer.getRank() != null)
            jsonObject.addProperty("rank", iPlayer.getRank().getRank());
        jsonObject.addProperty("kills", iPlayer.getKills());
        jsonObject.addProperty("wins", iPlayer.getWins());
        jsonObject.addProperty("deaths", iPlayer.getDeaths());
        jsonObject.addProperty("coins", iPlayer.getCoins());
        jsonObject.addProperty("elo", iPlayer.getElo());
        jsonObject.addProperty("streak", iPlayer.getStreak());


       if(iPlayer.isHideOthers())
            jsonObject.addProperty("hide_others",iPlayer.isHideOthers());

        if (iPlayer.getCustomTag() != null)
            jsonObject.addProperty("custom_tag", iPlayer.getCustomTag());


        if (iPlayer.getNick() != null) {
            jsonObject.add("nick", nickToJson(iPlayer));

        }

        JsonObject layout = new JsonObject();

        iPlayer.getLayouts().forEach((integer, string) -> layout.addProperty(String.valueOf(integer), string));
        jsonObject.add("layouts", layout);

        StringBuilder builder = new StringBuilder();
        for (String uuid : iPlayer.getIgnoreList()) {
            builder.append(uuid).append(";");
        }

        jsonObject.addProperty("ignorelist", builder.toString());


        insert(iPlayer.getUuid().toString(), jsonObject);

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
            String command = "REPLACE INTO `data`(`uuid`, `data`) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, gson.toJson(jsonObject));
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public void load(UUID uuid) {
        try {
            IPlayer iPlayer = new IPlayer(uuid);
            Core.i().getPlayerManager().addPlayer(iPlayer.getUuid(), iPlayer);
            Connection conn = core.getData().getConnection();
            String sql = "select * from data WHERE uuid = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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

                if(jsonObject.has("layouts")) {
                    JsonObject layout = jsonObject.get("layouts").getAsJsonObject();
                    for (int i = 1; i < 7; i++) {
                        if (layout.has(String.valueOf(i))) {
                            JsonElement element = layout.get(String.valueOf(i));
                            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                                iPlayer.getLayouts().put(i, element.getAsString());
                            }
                        }
                    }
                }

                if (jsonObject.has("ignorelist")) {
                    String[] ignorelist = jsonObject.get("ignorelist").getAsString().split(";");
                    for (String ignoredUuid : ignorelist) {
                        iPlayer.getIgnoreList().add(ignoredUuid);
                    }
                }

            }
            rs.close();
            ps.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

}
