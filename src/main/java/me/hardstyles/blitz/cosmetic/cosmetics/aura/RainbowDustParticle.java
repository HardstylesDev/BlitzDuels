package me.hardstyles.blitz.cosmetic.cosmetics.aura;

import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.cosmetic.Aura;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RainbowDustParticle extends Aura {
    public RainbowDustParticle() {
        super("RainbowDustParticle", "Rainbow Dust Particle Aura", "It's all colorful and happy and dusty!", BlitzSG.getInstance().getRankManager().getRankByName("VIP+"), new ItemStack(Material.REDSTONE, 1),7);
    }

    @Override
    public void uh(Player p) {
        Location loc = p.getLocation().clone().add(0, 1, 0);

        //p.playEffect(p.getLocation(),Effect.TILE_DUST, Material.WOOL.getId(),3,4,2);

        Bukkit.getOnlinePlayers().forEach(player1 -> p.spigot().playEffect(loc, Effect.COLOURED_DUST, 0, 1,  (float) .2, (float) 0.5, (float) .2, (float) .5, 4, 64));


    }
}
