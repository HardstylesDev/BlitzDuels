package me.hardstyles.blitz.arena;

import lombok.Getter;
import me.hardstyles.blitz.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Arena {
    final private Core core;
    private boolean occupied;

   private ArrayList<Location> spawns;
    public Arena(Core core, String name){
        this.core =core;
        this.spawns = new ArrayList<>();
        this.occupied = false;
        FileConfiguration fc = new YamlConfiguration();
        try {
            fc.load(new File(core.getDataFolder() + "/" + name + ".yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        for (String str : fc.getConfigurationSection("Spawns").getKeys(false)) {
            spawns.add(new Location(Bukkit.getWorld("arena"),fc.getInt("Spawns." + str + ".X"), fc.getInt("Spawns." + str + ".Y"), fc.getInt("Spawns." + str + ".Z")));
        }
    }

    public void setOccupied(boolean b){
        this.occupied = b;
    }
    public boolean isOccupied(){
        return this.occupied;
    }
    public void recycle(){
        this.occupied = false;
    }

    public ArrayList<Location> getSpawns(){
        return this.spawns;
    }
}
