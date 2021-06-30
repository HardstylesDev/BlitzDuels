package me.hardstyles.blitz.match;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.kits.IItem;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.match.match.TeamMatch;
import me.hardstyles.blitz.party.Party;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class MatchHandler implements Listener {

    final private Core core;

    public MatchHandler(Core core) {
        this.core = core;
    }


    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        IPlayer player = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (player == null || player.getMatch() == null) {
            return;
        }

        Match match = player.getMatch();
        if (!match.getAlivePlayers().contains(player.getUuid())) {
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
        if (match.getMatchStage() != MatchStage.STARTED || !match.getAlivePlayers().contains(victim.getUniqueId())) {
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
            e.setCancelled(true);
            return;
        }

        if (!match.getAlivePlayers().contains(victim.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        if (!match.getAlivePlayers().contains(e.getDamager().getUniqueId()) && e.getDamager() instanceof Player)
        if (e.getDamager() == e.getEntity()) {
            e.setCancelled(true);
            return;
        }

        if (match instanceof TeamMatch) {
            Party party = ivictim.getParty();
            if (party != null) {
                if (party.getMembers().contains(e.getDamager().getUniqueId())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (e.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) e.getDamager();
            if (projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();
                if (match instanceof TeamMatch) {
                    Party party = ivictim.getParty();
                    if (party != null) {
                        if (party.getMembers().contains(shooter.getUniqueId())) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                if (shooter.equals(e.getEntity())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            match.getAttacks().put(victim.getUniqueId(), attacker.getUniqueId());
            long dmg = match.getDamageDone().getOrDefault(attacker.getUniqueId(), 0L);
            match.getDamageDone().put(attacker.getUniqueId(), dmg + Math.round(e.getFinalDamage()));
        }

    }

    @EventHandler
    public void onSplash(PotionSplashEvent e) {
        ThrownPotion potion = e.getPotion();
        if (potion.getEffects().stream().anyMatch(effect -> effect.getType() == PotionEffectType.HARM)) {
            Projectile projectile = e.getEntity();
            if (projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();
                IPlayer iPlayer = core.getPlayerManager().getPlayer(shooter.getUniqueId());
                Match match = iPlayer.getMatch();
                if (match instanceof TeamMatch && iPlayer.getParty() != null) {
                    for (UUID member : iPlayer.getParty().getMembers()) {
                        Player memberPlayer = match.getPlayerReference().getOrDefault(member, null);
                        if (memberPlayer != null) {
                            e.getAffectedEntities().remove(memberPlayer);
                        }
                    }
                }
                e.getAffectedEntities().remove(shooter);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        IPlayer ivictim = core.getPlayerManager().getPlayer(event.getEntity().getUniqueId());
        Match match = ivictim.getMatch();
        if (match == null || !match.isInProgress()) {
            return;
        }
        match.onDeath(ivictim.getUuid());
        event.getEntity().spigot().respawn();
    }

    @EventHandler
    public void onIgniteEevent(BlockIgniteEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (iPlayer.hasMatch()) {
            iPlayer.getMatch().getBlocksPlaced().add(e.getBlock().getLocation());

        }
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
    public void useItem(PlayerItemDamageEvent e) {
        if (e.getItem().getType() == Material.FLINT_AND_STEEL) {
            e.setDamage(16);
            if (e.getItem().getDurability() >= 48) {
                e.setDamage(15);
            }
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
            if (e.getBlock().getType() == Material.CAKE_BLOCK || e.getBlock().getType() == Material.RAILS || e.getBlock().getType() == Material.FIRE) {
                iPlayer.getMatch().getBlocksPlaced().add(e.getBlock().getLocation());
                return;
            }
            Bukkit.broadcastMessage("boooo");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void saturation(FoodLevelChangeEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getEntity().getUniqueId());
        if (iPlayer.hasMatch()) {
            if (iPlayer.getMatch().getMatchStage() != MatchStage.STARTED) {
                e.setFoodLevel(20);
            } else if (!iPlayer.getMatch().getAlivePlayers().contains(e.getEntity().getUniqueId())) {
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

    @EventHandler
    public void interactSpectator(PlayerInteractEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (iPlayer == null) {
            e.setCancelled(true);
            return;
        }
        if (iPlayer.hasMatch()) {
            if (!iPlayer.getMatch().getAlivePlayers().contains(iPlayer.getUuid()) || iPlayer.getMatch().getMatchStage() == MatchStage.ENDED) {
                e.setCancelled(true);
                return;
            }
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                    if (!iPlayer.getMatch().getChests().contains(e.getClickedBlock().getLocation())) {
                        Chest chest = (Chest) e.getClickedBlock().getState();
                        chest.getInventory().clear();
                        core.getChestFiller().generateChestLoot(chest.getInventory(), 3);
                        chest.update();
                        iPlayer.getMatch().getChests().add(e.getClickedBlock().getLocation());
                    }
                }
            }
            Player p = e.getPlayer();
            if (p.getItemInHand() == null || p.getItemInHand().getItemMeta() == null || p.getItemInHand().getItemMeta().getDisplayName() == null) {
                return;
            }

            if (p.getItemInHand().getItemMeta().getDisplayName().contains("Default")) {
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

                p.getInventory().clear();

                p.getInventory().setHelmet(IItem.PALADIN_HELMET.getItem());
                p.getInventory().setBoots(IItem.RANGER_BOOTS.getItem());
                p.getInventory().setChestplate(IItem.PALADIN_CHESTPLATE.getItem());
                p.getInventory().setLeggings(IItem.CHAIN_LEGGINGS.getItem());

                p.getInventory().addItem(IItem.ROD.getItem());
                p.getInventory().addItem(IItem.SHARP_STONE_SWORD.getItem());

                p.getInventory().addItem(IItem.STEAK.getItem());

            } else if (p.getItemInHand().getItemMeta().getDisplayName().contains("Custom Kit")) {
                int kitIndex = Integer.parseInt(p.getItemInHand().getItemMeta().getDisplayName().substring(14));
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                p.getInventory().clear();

                Bukkit.getScheduler().runTask(core, ()-> {
                    String[] layout = core.getPlayerManager().getPlayer(p.getUniqueId()).getLayouts().get(kitIndex).split(";");
                    for (String s : layout) {
                        IItem item;
                        try {
                            item = IItem.valueOf(s);
                        } catch (IllegalArgumentException ex) {
                            Bukkit.getLogger().info("non-existing ID in " + p.getName() + "'s layout #" + kitIndex);
                            continue;
                        }
                        if (item == IItem.BLANK) {
                            continue;
                        }
                        ItemStack itemStack = item.getItem();
                        if (itemStack.getType().name().endsWith("_HELMET")) {
                            p.getInventory().setHelmet(itemStack);
                        } else if (itemStack.getType().name().endsWith("_CHESTPLATE")) {
                            p.getInventory().setChestplate(itemStack);
                        } else if (itemStack.getType().name().endsWith("_LEGGINGS")) {
                            p.getInventory().setLeggings(itemStack);
                        } else if (itemStack.getType().name().endsWith("_BOOTS")) {
                            p.getInventory().setBoots(itemStack);
                        } else {
                            p.getInventory().addItem(itemStack);
                        }
                    }
                });
            }
        }

    }

    @EventHandler
    public void pickupSpectator(PlayerPickupItemEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (iPlayer.hasMatch()) {
            if (!iPlayer.getMatch().getAlivePlayers().contains(iPlayer.getUuid())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (iPlayer.hasMatch()) {
            if (!iPlayer.getMatch().getAlivePlayers().contains(iPlayer.getUuid()) || iPlayer.getMatch().getMatchStage() == MatchStage.ENDED || iPlayer.getMatch().getMatchStage() == MatchStage.GRACE) {
                e.setCancelled(true);
            }
        }

        //fixes people being able to use all of their kits LOL
        if (e.getItemDrop().getItemStack().getType() == Material.BOOK) {
            e.setCancelled(true);
        }
    }


}
