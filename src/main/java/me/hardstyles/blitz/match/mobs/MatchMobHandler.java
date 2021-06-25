package me.hardstyles.blitz.match.mobs;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.MatchStage;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.match.match.TeamMatch;
import me.hardstyles.blitz.player.IPlayer;
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

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

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
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if ((e.getEntity() instanceof Player)) {
            Player p = (Player) e.getEntity();
            IPlayer bsgPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());
            if (!bsgPlayer.hasMatch()) {
                return;
            }

            Match match = bsgPlayer.getMatch();
            if (match.getDead().contains(e.getDamager().getUniqueId())) {
                e.setCancelled(true);
                return;
            }
            if (e.getDamager() instanceof Player && !match.getAlivePlayers().contains(e.getDamager().getUniqueId())) {
                e.setCancelled(true);
                return;
            }
            if (!match.getAlivePlayers().contains(e.getEntity().getUniqueId())) {
                e.setCancelled(true);
                return;
            }


            if (e.getDamager() instanceof Projectile) {
                Entity shooter = (Entity) ((Projectile) e.getDamager()).getShooter();
                Player owner = null;
                for (Map.Entry<UUID, HashSet<Entity>> entry : match.getEntities().entrySet()) {
                    if (entry.getValue().contains(shooter)) {
                        owner = match.getPlayerReference().get(entry.getKey());
                        break;
                    }
                }
                if (owner == e.getEntity()) {
                    e.setCancelled(true);
                    return;
                }

            }

            if (match.getMatchStage() == MatchStage.STARTED) {
                if (match.getEntities().containsKey(p.getUniqueId())) {
                    if (match.getEntities().get(p.getUniqueId()).contains(e.getDamager()) || (e.getDamager() instanceof Snowball && match.getEntities().get(p.getUniqueId()).contains(((Snowball) e.getDamager()).getShooter())) || (e.getDamager() instanceof Arrow && bsgPlayer.getMatch().getEntities().get(p.getUniqueId()).contains(((Arrow) e.getDamager()).getShooter()))) {
                        e.setCancelled(true);
                    }
                }

                if (!(e.getDamager() instanceof Player)) {
                    if (e.getDamager() instanceof Arrow)
                        if (((Arrow) e.getDamager()).getShooter() instanceof Player) {
                            e.setDamage(e.getDamage() / 2);
                            return;
                        }
                    e.setDamage(e.getDamage() / 10);
                }
            }
        } else if (e.getDamager() instanceof Player) {
            IPlayer damager = core.getPlayerManager().getPlayer(e.getDamager().getUniqueId());
            if (damager == null) return;
            Match match = damager.getMatch();
            if (match == null) return;
            if (match.getDead().contains(damager.getUuid())) {
                e.setCancelled(true);

            }
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
                        if (!match.getDead().contains(potentialTarget.getUniqueId()) && potentialTarget != e.getPlayer())
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
            e.setCancelled(true);

            Player target = (Player) e.getTarget();
            IPlayer iPlayer = core.getPlayerManager().getPlayer(target.getUniqueId());
            if (!iPlayer.hasMatch())
                return;


            Match match = iPlayer.getMatch();
            Player owner = null;
            for (Map.Entry<UUID, HashSet<Entity>> entry : match.getEntities().entrySet()) {
                if (entry.getValue().contains(e.getEntity())) {
                    owner = match.getPlayerReference().get(entry.getKey());
                    break;
                }
            }
            if (owner == null) return;

            if (!iPlayer.getMatch().getAlivePlayers().contains(iPlayer.getUuid())) {
                targetNearby(match, owner, (Creature) e.getEntity());
                return;
            }

            if (match.getEntities().isEmpty() || !match.getEntities().containsKey(target.getUniqueId())) {
                return;
            }

            if (match.getEntities().containsKey(iPlayer.getUuid()) && match.getEntities().get(target.getUniqueId()).contains(e.getEntity())) {
                targetNearby(match, owner, (Creature) e.getEntity());
            }
        }

    }

    private void targetNearby(Match match, Player owner, Creature e) {
        IPlayer iOwner = core.getPlayerManager().getPlayer(owner.getUniqueId());
        if (iOwner == null) return;

        for (Entity entity : e.getLocation().getWorld().getNearbyEntities(e.getLocation(), 15, 15, 15)) {
            if (entity instanceof Player) {
                Player potentialTarget = (Player) entity;
                if (match.getAlivePlayers().contains(potentialTarget.getUniqueId()) && potentialTarget != owner) {
                    if (match instanceof TeamMatch) {
                        if (iOwner.getParty() != null && iOwner.getParty().getMembers().contains(potentialTarget.getUniqueId())) {
                            continue;
                        }
                    }
                    e.setTarget(potentialTarget);
                    break;
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
        if (match.getEntities().containsKey(iPlayer.getUuid()) && match.getEntities().get(e.getEntity().getUniqueId()).contains(e.getMount()))
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