package me.hardstyles.blitz.gamestar.stars;

import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import me.hardstyles.blitz.gamestar.Star;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Imprison extends Star {
    public Imprison() {
        super("Imprison", Material.IRON_FENCE, "Prevent all opponents from moving!", 5000);
    }

    @Override
    public void run(Player p) {
        p.getInventory().remove(Material.NETHER_STAR);
        BlitzSGPlayer user = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        for (Player alivePlayer : user.getGame().getAlivePlayers()) {
            if (alivePlayer != p)
                alivePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 20, 9));
        }

    }
}