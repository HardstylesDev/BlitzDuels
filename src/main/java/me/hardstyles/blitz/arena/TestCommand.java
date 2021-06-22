package me.hardstyles.blitz.arena;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.arenaloader.BlockToPlace;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TestCommand implements CommandExecutor {

    public static HashMap<UUID, Long> cooldown = new HashMap<>();

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player p = (Player) sender;
        for (Arena arena : Core.i().getArenaManager().getArenas()) {
            p.sendMessage("found: " + arena.getSpawns().get(0));
        }
       //ArrayList<Location> z = pasteSchematic(new File(args[0]), p.getLocation());
       //p.teleport(z.get(0));
        return true;
    }
    public  ArrayList<Location> pasteSchematic(File f, Location loc) {

        Bukkit.broadcastMessage("Started");
        ArrayList<Location> spawns = new ArrayList<>();
        try {

            FileInputStream fis = new FileInputStream(f);
            NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
            short width = nbt.getShort("Width");
            short height = nbt.getShort("Height");
            short length = nbt.getShort("Length");
            byte[] blocks = nbt.getByteArray("Blocks");
            byte[] blockdata = nbt.getByteArray("Data");
            fis.close();
            //paste
            ArrayList<BlockToPlace> blockToPlaceList = new ArrayList<>();
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    for (int z = 0; z < length; ++z) {

                        int index = y * width * length + z * width + x;
                        final Location l = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ());
                        int b = blocks[index] & 0xFF;//make the block unsigned, so that blocks with an id over 127, like quartz and emerald, can be pasted

                        Material m = Material.getMaterial(b);

                        if(m == Material.SIGN || m == Material.SIGN_POST || m == Material.WALL_SIGN){
                            System.out.println(m);

                            spawns.add(l);
                            continue;
                        }
                        blockToPlaceList.add(new BlockToPlace(l, l.getBlock(), m, blockdata[index]));
                    }
                }
            }
            Bukkit.broadcastMessage("Finished");

            slowSet(blockToPlaceList, 256);

        } catch (Exception e) {
        }

    return spawns;
    }

    public void slowSet(ArrayList<BlockToPlace> blocks, int limitPerTick) {
        ArrayList<Location> spawns = new ArrayList<>();
        int current = 0;
        int delay = 0;
        for (BlockToPlace block : blocks) {
            if (block.material == Material.AIR) {
                continue;
            }
            if(block.material == Material.SIGN || block.material == Material.SIGN_POST){
                continue;
            }
            current++;
            if (current > limitPerTick) {
                delay++;
                current = 0;
            }


            new BukkitRunnable() {
                @Override
                public void run() {
                    block.block.setType(block.material);
                    block.block.setData(block.blockdata);
                    Bukkit.broadcastMessage("placed" + block.material);
                }
            }.runTaskLater(Core.i(), delay);
        }
    }
}