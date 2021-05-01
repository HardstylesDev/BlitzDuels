package me.hardstyles.blitz.arena.arenaloader;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockToPlace {

    final public Location location;
    final public Block block;
    final public Material material;
    final public Byte blockdata;

    public BlockToPlace(Location location, Block block, Material material, Byte blockdata){
        this.location = location;
        this.block = block;
        this.material = material;
        this.blockdata = blockdata;
    }
}
