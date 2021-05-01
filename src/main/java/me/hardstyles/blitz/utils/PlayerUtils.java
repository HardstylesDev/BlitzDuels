package me.hardstyles.blitz.utils;

import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import me.hardstyles.blitz.kit.Kit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerUtils {

    public static void savePlayerData() {
        for (BlitzSGPlayer uhcPlayer : BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayers().values()) {
            //new File(Kits.getInstance().getDataFolder() + "/players").mkdir();
            File f = new File(BlitzSG.getInstance().getDataFolder() + "/players/" + uhcPlayer.getUuid() + ".yml");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileConfiguration fc = new YamlConfiguration();
            try {
                fc.load(f);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            if (uhcPlayer.getRank() == null || uhcPlayer.getRank().getRank() == null)
                fc.set("Rank", "Default");
            else
                fc.set("Rank", uhcPlayer.getRank().getRank());
            if (uhcPlayer.getSelectedKit() == null)
                fc.set("SelectedKit", "Knight");
            else fc.set("SelectedKit", uhcPlayer.getSelectedKit().getName());

            if (uhcPlayer.getNick() == null)
                fc.set("Nickname", "");
            else fc.set("Nickname", uhcPlayer.getNick().getNickName());
            fc.set("ELO", uhcPlayer.getElo());
            fc.set("Wins", uhcPlayer.getWins());
            fc.set("Kills", uhcPlayer.getKills());
            fc.set("Deaths", uhcPlayer.getDeaths());
            fc.set("Coins", uhcPlayer.getCoins());
            for (Kit kit : BlitzSG.getInstance().getKitManager().getKits())
                fc.set("Kits." + kit.getName(), uhcPlayer.getKitLevel(kit));
            try {
                fc.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
           //fc.set("Stars", uhcPlayer.getStars());
           //try {
           //    fc.save(f);
           //} catch (IOException e) {
           //    e.printStackTrace();
           //}
        }
    }

    public static void loadPlayerData() {
        try {
            new File(BlitzSG.getInstance().getDataFolder() + "/players").mkdir();
            for (File f : new File(BlitzSG.getInstance().getDataFolder() + "/players").listFiles()) {
                System.out.println(f.getAbsolutePath());
                FileConfiguration fc = new YamlConfiguration();
                try {
                    fc.load(f);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                BlitzSGPlayer uhcPlayer = new BlitzSGPlayer(UUID.fromString(f.getName().replace(".yml", "")));
                uhcPlayer.loadStats(fc);
                //KitPlayer kp = new KitPlayer(UUID.fromString(f.getName().replace(".yml", "")), fc.getInt("Kills"), fc.getInt("Deaths"), fc.getBoolean("inArena"));
            }
        } catch (Exception e) {
        }
    }

}
