package me.syesstyles.blitz.commands.subcommands;

import me.syesstyles.blitz.BlitzSG;
import me.syesstyles.blitz.aaaaa.LoadStats;
import me.syesstyles.blitz.aaaaa.SaveStats;
import me.syesstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import me.syesstyles.blitz.cosmetic.Aura;
import me.syesstyles.blitz.gui.AuraGUI;
import me.syesstyles.blitz.gui.ShopStarGUI;
import me.syesstyles.blitz.kit.Kit;
import me.syesstyles.blitz.rank.Rank;
import me.syesstyles.blitz.rank.RankManager;
import me.syesstyles.blitz.utils.nickname.Nickname;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DebugCommand extends SubCommand {

    @Override
    public void runCommand(Player p, String[] args) {
        if (args.length == 1)
            return;
        if (args[1].equalsIgnoreCase("import")) {
            if (args.length > 2) {
                BlitzSG.send(p, "&aswag to &e" + args[2]);
                BlitzSG.getInstance().getArenaManager().loadArena(args[2]);
            }
        } else if (args[1].equalsIgnoreCase("name")) {
            if (args.length > 2) {
                BlitzSG.send(p, "&aChanging your nick to &e" + args[2]);
                new Nickname().setNick(p, args[2]);
            }

        } else if (args[1].equalsIgnoreCase("db")) {
            try {
                Connection con = BlitzSG.getInstance().getData().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select * from stats");
                while (rs.next())
                    System.out.println("" + rs.getString("uuid"));
                con.close();

            } catch (SQLException e) {
            }
        } else if (args[1].equalsIgnoreCase("kit")) {
            if (args.length > 2) {
                p.getInventory().clear();
                Kit kit = BlitzSG.getInstance().getKitManager().getKit(args[2]);
                if (kit == null) {
                    p.sendMessage("invalid kit");
                    return;
                }
                kit.giveKit(p, Integer.parseInt(args[3]));
            }
        } else if (args[1].equalsIgnoreCase("save")) {
            new SaveStats().save(p);
        } else if (args[1].equalsIgnoreCase("load")) {
            new LoadStats().load();

        } else if (args[1].equalsIgnoreCase("block")) {


        } else if (args[1].equalsIgnoreCase("star")) {
            ShopStarGUI.openGUI(p);
        } else if (args[1].equalsIgnoreCase("info")) {
            if (args.length > 2) {
                Player player = Bukkit.getPlayer(args[2]);
                BlitzSG.send(p, "Their username is " + player.getName());
                BlitzSG.send(p, "Their displayname is " + player.getDisplayName());
                BlitzSG.send(p, "Their uuid is " + player.getUniqueId());
                BlitzSG.send(p, "Their gameprofile id is " + ((CraftPlayer) player).getHandle().getProfile().getId());
            } else {
                BlitzSG.send(p, "Your username is " + p.getName());
                BlitzSG.send(p, "Your displayname is " + p.getDisplayName());
                BlitzSG.send(p, "Your uuid is " + p.getUniqueId());
                BlitzSG.send(p, "Your gameprofile id is " + ((CraftPlayer) p).getHandle().getProfile().getId());
            }

        } else if (args[1].equalsIgnoreCase("coins")) {
            if (args.length > 2) {
                Player player = Bukkit.getPlayer(args[2]);
                BlitzSGPlayer bsgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(player.getUniqueId());
                if (bsgPlayer == null)
                    p.sendMessage("Unknown player.");
                else {
                    bsgPlayer.addCoins(Integer.parseInt(args[3]));
                    p.sendMessage("Coins given.");
                }
            }
            if (args[1].equalsIgnoreCase("name")) {
                if (args.length > 2) {
                    BlitzSG.send(p, "&aChanging your nick to &e" + args[2]);
                    new Nickname().setNick(p, args[2]);
                }
            }
        } else if (args[1].equalsIgnoreCase("rank")) {
            if (args.length > 2) {
                BlitzSGPlayer bsgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
                Rank rank = BlitzSG.getInstance().getRankManager().getRankByName(args[2]);
                if (rank == null) {
                    BlitzSG.send(p, "&cthat rank doesn't exist.");

                    return;
                }
                bsgPlayer.setRank(rank);
                BlitzSG.send(p, "&aYou've set your rank to " + rank.getPrefix() + rank.getRank());
                BlitzSG.getInstance().getNametagManager().update();
            } else
                BlitzSG.send(p, "" + new RankManager().getRank(p));

        } else if (args[1].equalsIgnoreCase("aura")) {
            if (args.length > 2) {
                BlitzSGPlayer bsgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
                Aura aura = BlitzSG.getInstance().getCosmeticsManager().getAuraByName(args[2]);
                if(args[2].equalsIgnoreCase("null")){
                    BlitzSG.send(p, "&cAura has been reset.");
                    bsgPlayer.setAura(null);
                    return;
                }
                if (aura == null) {
                    BlitzSG.send(p, "&cthat aura doesn't exist.");

                    return;
                }
                BlitzSG.getInstance().getCosmeticsManager().add(p);
                bsgPlayer.setAura(aura);
                BlitzSG.send(p, "&aYou've set your aura to " + aura.getName());

            } else
                AuraGUI.openGUI(p);

        }
    }

    @Override
    public String getHelp() {
        return "§8\u2022 §f/bsg addspawn";
    }

    @Override
    public String getPermission() {
        return "bsg.admin";
    }

}
