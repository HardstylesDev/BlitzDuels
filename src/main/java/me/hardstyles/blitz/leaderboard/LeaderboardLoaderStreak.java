package me.hardstyles.blitz.leaderboard;

import me.hardstyles.blitz.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class LeaderboardLoaderStreak {
    private Location base = new Location(Bukkit.getWorld("world"), 6.5, 78, -13.5);
    private final Core core;
    private HashMap<Integer, Entity> armorstands;

    public LeaderboardLoaderStreak(Core core) {
        this.core = core;

        armorstands = new HashMap<>();

        for (Entity nearbyEntity : base.getWorld().getNearbyEntities(base, 0.8, 8, 0.8)) {
            nearbyEntity.remove();
        }

        Location first = base.add(0, 1, 0);
        for (int a = 0; a < 10; a++) {
            armorstands.put(a, spawnArmorStand(first = first.add(0, .3, 0)));
        }
        ArmorStand armorStand = spawnArmorStand(first = first.add(0, .3, 0));
        armorStand.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + "Streaks");

        core.getLeaderboardUpdater().update();
    }

    public ArmorStand spawnArmorStand(Location location) {
        Entity armor = location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        ((ArmorStand) armor).setVisible(false);
        armor.setCustomNameVisible(true);
        ((ArmorStand) armor).setGravity(false);
        ((ArmorStand) armor).setMarker(true);
        return (ArmorStand) armor;
    }

    public HashMap<Integer, Entity> getArmorstands() {
        return armorstands;
    }


}
