package me.hardstyles.blitz.commands.impl;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MassResetLayoutsCommand extends Command {

    public MassResetLayoutsCommand() {
        super("massresetlayouts", ImmutableList.of(), 8);
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        p.sendMessage("§cClearing all layouts...");
        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), ()-> {
            for (IPlayer player : Core.i().getPlayerManager().getPlayers().values()) {
                player.getLayouts().clear();
            }
            Map<String, String> storage = new HashMap<>();
            try (Statement statement = Core.i().getData().getConnection().createStatement()) {
                ResultSet set = statement.executeQuery("select * from data");
                while (set.next()) {
                    storage.put(set.getString("uuid"), set.getString("data"));
                }
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JsonParser parser = new JsonParser();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            storage.forEach((uuid, json) -> {
                JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                jsonObject.add("layouts", new JsonObject());
                try (PreparedStatement statement = Core.i().getData().getConnection().prepareStatement("update data set data = ? where uuid = ?")) {
                    statement.setString(1, gson.toJson(jsonObject));
                    statement.setString(2, uuid);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            p.sendMessage("§aSuccessfully reset all layouts.");
        });
    }

    @Override
    public void onConsole(CommandSender sender, String[] args) {
        sender.sendMessage("§cClearing all layouts...");
        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), ()-> {
            for (IPlayer player : Core.i().getPlayerManager().getPlayers().values()) {
                player.getLayouts().clear();
            }
            Map<String, String> storage = new HashMap<>();
            try (Statement statement = Core.i().getData().getConnection().createStatement()) {
                ResultSet set = statement.executeQuery("select * from data");
                while (set.next()) {
                    storage.put(set.getString("uuid"), set.getString("data"));
                }
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JsonParser parser = new JsonParser();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            storage.forEach((uuid, json) -> {
                JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                jsonObject.add("layouts", new JsonObject());
                try (PreparedStatement statement = Core.i().getData().getConnection().prepareStatement("update data set data = ? where uuid = ?")) {
                    statement.setString(1, gson.toJson(jsonObject));
                    statement.setString(2, uuid);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            sender.sendMessage("§aSuccessfully reset all layouts.");
        });
    }
}
