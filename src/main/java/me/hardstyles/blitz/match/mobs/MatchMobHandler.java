package me.hardstyles.blitz.match.mobs;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.Match;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.EntitySnowman;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityMountEvent;

public class MatchMobHandler implements Listener {
    final private Core core;

    public MatchMobHandler(Core core) {
        this.core = core;
    }

    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent e) {
        if (e.getEntity() instanceof Snowman && e.getNewState().getType() == Material.SNOW) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHorseDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Snowman) {
            if (e.getCause() == EntityDamageEvent.DamageCause.MELTING) {
                e.setCancelled(true);
            }
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID)
            if (e.getEntity() instanceof Horse)
                e.getEntity().remove();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BREEDING) || event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void mobSpawnEvent(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() == null)
            return;
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (!iPlayer.hasMatch())
            return;
        Match match = iPlayer.getMatch();
        if (!iPlayer.getMatch().getAlivePlayers().contains(iPlayer.getUuid()))
            return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem().getType() == Material.MONSTER_EGG) {
            EntityType entityType = ((SpawnEgg) e.getPlayer().getItemInHand().getData()).getSpawnedType();
            if (entityType != null) {
                e.setCancelled(true);
                ItemStack itemInHand = e.getPlayer().getItemInHand();
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                e.getPlayer().setItemInHand(itemInHand);


                Entity entity = e.getClickedBlock().getWorld().spawn(e.getClickedBlock().getLocation().add(0, 1, 0), entityType.getEntityClass());
                if (entity instanceof Wolf) {
                    ((Wolf) entity).setAngry(false);
                    ((Wolf) entity).setAdult();
                    entity.setCustomNameVisible(false);

                    ((Wolf) entity).setTamed(true);
                    ((Wolf) entity).setOwner(e.getPlayer());
                    ((Wolf) entity).setCollarColor(DyeColor.RED);

                } else if (entity instanceof Slime) {
                    ((Slime) entity).setSize(3);
                    ((Slime) entity).setHealth(6);

                } else if (entity instanceof MagmaCube) {
                    ((MagmaCube) entity).setSize(3);
                    ((MagmaCube) entity).setHealth(6);

                } else if (entity instanceof Horse) {
                    ((Horse) entity).setVariant(Horse.Variant.HORSE);
                    ((Horse) entity).setColor(Horse.Color.BLACK);
                    ((Horse) entity).setStyle(Horse.Style.NONE);

                    ((Horse) entity).setTamed(true);
                    ((Horse) entity).setAdult();
                    ((Horse) entity).getInventory().setSaddle(new ItemStack(Material.SADDLE));
                    ((Horse) entity).setMaxHealth(30);
                    ((Horse) entity).setJumpStrength(0.8);
                }
                entity.setCustomName(null);
                entity.setCustomNameVisible(false);
                match.addEntity(iPlayer.getUuid(), entity);


                for (Entity entityList : entity.getNearbyEntities(15, 15, 15))
                    if (entityList instanceof Player) {
                        Player potentialTarget = (Player) entityList;
                        if (!match.getDead().contains(potentialTarget) && potentialTarget != e.getPlayer())
                            if (entity instanceof Monster)
                                ((Monster) entity).setTarget(potentialTarget);
                            else if (entity instanceof Wolf)
                                ((Wolf) entity).setTarget(potentialTarget);

                    }
            } else {


                //it's a snowman.

                e.setCancelled(true);
                ItemStack itemInHand = e.getPlayer().getItemInHand();
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                e.getPlayer().setItemInHand(itemInHand);


                Entity entity = e.getClickedBlock().getWorld().spawn(e.getClickedBlock().getLocation().add(0, 1, 0), EntityType.SNOWMAN.getEntityClass());
                ((Snowman) entity).setHealth(2);
                entity.setCustomName(null);
                entity.setCustomNameVisible(false);
                ((Snowman) entity).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 1, false, false));
                match.addEntity(iPlayer.getUuid(), entity);
                for (Entity entityList : entity.getNearbyEntities(15, 15, 15))
                    if (entityList instanceof Player) {
                        Player potentialTarget = (Player) entityList;
                        if (!match.getDead().contains(potentialTarget) && potentialTarget != e.getPlayer()) {
                            ((Golem) entity).setTarget(potentialTarget);
                            ((Golem) entity).launchProjectile(Snowball.class);
                        }

                    }
            }
        }

    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player) {
            Player target = (Player) e.getTarget();
            IPlayer iPlayer = core.getPlayerManager().getPlayer(target.getUniqueId());
            if (!iPlayer.hasMatch())
                return;
            Match match = iPlayer.getMatch();
            if (match.getEntities().values().contains(e.getEntity())) {
                e.setCancelled(true);
                for (Entity entity : e.getEntity().getNearbyEntities(15, 15, 15))
                    if (entity instanceof Player) {
                        Player potentialTarget = (Player) entity;
                        if (!match.getDead().contains(potentialTarget) && potentialTarget != target)
                            e.setTarget(potentialTarget);

                    }
            }
        }
    }

    @EventHandler
    public void mobRide(EntityMountEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player target = (Player) e.getEntity();
        IPlayer iPlayer = core.getPlayerManager().getPlayer(target.getUniqueId());
        if (!iPlayer.hasMatch())
            return;
        Match match = iPlayer.getMatch();
        if (match.getEntities().values().contains(e.getMount()))
            return;
        Location loc = e.getEntity().getLocation();
        loc.setPitch(e.getEntity().getLocation().getPitch());
        loc.setYaw(e.getEntity().getLocation().getYaw());

        e.setCancelled(true);
        e.getEntity().teleport(loc);

    }

    @EventHandler
    public void onBurn(EntityCombustEvent e) {
        if (e.getEntity() instanceof Zombie || e.getEntity() instanceof Skeleton)
            e.setCancelled(true);
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Snowman) {
            Vector v = e.getEntity().getVelocity();

            for (int a = 0; a < 2; ++a)
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        shoot((Entity) e.getEntity().getShooter());

                    }
                }.runTaskLater(core, a * 7);
        }
    }

    public void shoot(Entity shooter) {
        Vector vector = ((Snowman) shooter).getTarget().getLocation().toVector().subtract(shooter.getLocation().toVector()).normalize();


        Entity snowball = shooter.getWorld().spawn(((Snowman) shooter).getEyeLocation().add(Math.random() - 0.5, Math.random() - 0.2, Math.random() - 0.5), Snowball.class);
        ((Snowball) snowball).setShooter((ProjectileSource) shooter);
        snowball.setVelocity(vector);

    }

}