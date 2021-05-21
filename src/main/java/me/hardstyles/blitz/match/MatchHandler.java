package me.hardstyles.blitz.match;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;

public class MatchHandler implements Listener {

    final private Core core;

    public MatchHandler(Core core) {
        this.core = core;
    }


    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        IPlayer player = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (player.getMatch() == null) {

            return;
        }
        Match match = player.getMatch();
        if (!match.getAlive().contains(player.getUuid())) {

            return;
        }
        match.onDeath(p.getUniqueId());

    }
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) e.getEntity();
        IPlayer ivictim = core.getPlayerManager().getPlayer(victim.getUniqueId());
        Match match = ivictim.getMatch();
        if (match == null) {
            e.setCancelled(true);
            return;
        }
        if(match.getMatchStage() != MatchStage.STARTED){
            e.setCancelled(true);
            return;
        }
        if (e.getFinalDamage() >= victim.getHealth()) {
            e.setCancelled(true);
            match.onDeath(victim.getUniqueId());
        }
    }
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) e.getEntity();
        IPlayer ivictim = core.getPlayerManager().getPlayer(victim.getUniqueId());
        Match match = ivictim.getMatch();
        if (match == null) {
            return;
        }

        if (!match.getAlive().contains(victim.getUniqueId())) {
            return;
        }
        if (e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            match.getAttacks().put(victim.getUniqueId(), attacker.getUniqueId());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        IPlayer ivictim = core.getPlayerManager().getPlayer(event.getEntity().getUniqueId());
        Match match = ivictim.getMatch();
        if (match == null || !match.isInProgress()) {
            return;
        }
        match.onDeath(ivictim.getUuid());
        event.getEntity().spigot().respawn();
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (iPlayer.hasMatch()) {
            if (e.getBlock().getType() == Material.CAKE_BLOCK) {
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (iPlayer.hasMatch()) {
            if (e.getBlockPlaced().getType() == Material.TNT) {
                e.getBlockPlaced().setType(Material.AIR);
                e.getPlayer().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);
                return;
            }
            if (e.getBlock().getType() == Material.CAKE_BLOCK || e.getBlock().getType() == Material.RAILS) {
                iPlayer.getMatch().getBlocksPlaced().add(e.getBlock().getLocation());
                return;
            }
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void saturation(FoodLevelChangeEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getEntity().getUniqueId());
        if (iPlayer.hasMatch()) {
            if(iPlayer.getMatch().getMatchStage() != MatchStage.STARTED){
                e.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.blockList().clear();
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent e) {
        e.blockList().clear();

    }

}
