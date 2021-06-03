package me.hardstyles.blitz.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.utils.ItemBuilder;
import me.hardstyles.blitz.utils.ItemUtils;
import net.minecraft.server.v1_8_R3.ItemArmor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
            return;
        }

        if (!match.getAlivePlayers().contains(victim.getUniqueId())) {
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
    public void onIgniteEevent(BlockIgniteEvent event) {
        event.setCancelled(true);
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
            if (iPlayer.getMatch().getMatchStage() != MatchStage.STARTED) {
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
        if (iPlayer.hasMatch()) {
            if (iPlayer.getMatch().getDead().contains(iPlayer.getUuid()) || iPlayer.getMatch().getMatchStage() == MatchStage.ENDED) {
                e.setCancelled(true);
                return;
            }
            Player p = e.getPlayer();
            if (p.getItemInHand() == null || p.getItemInHand().getItemMeta() == null || p.getItemInHand().getItemMeta().getDisplayName() == null) {
                return;
            }

            if (p.getItemInHand().getItemMeta().getDisplayName().contains("Default")) {
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

                p.getInventory().clear();

                p.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).name("&rPaladin's Iron Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).amount(1).make());
                p.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).name("&rWolftamer's Diamond Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).amount(1).make());
                p.getInventory().setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).name("&rPaladin's Iron Chestplate (X)").amount(1).make());
                p.getInventory().setLeggings(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).name("&rChain Leggings").amount(1).make());

                p.getInventory().addItem(new ItemStack(Material.FISHING_ROD, 1));
                p.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).amount(1).make());

                p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 12));
                PotionEffect[] effects = new PotionEffect[]{new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 0), new PotionEffect(PotionEffectType.SPEED, 20 * 8, 0)};
                ItemStack pot = ItemUtils.buildPotion(effects, (short) 16450);
                pot.setAmount(3);
                p.getInventory().addItem(pot);

                p.getInventory().addItem(new ItemBuilder(Material.MONSTER_EGG).durability(95).name("&rWolf Spawn Egg").amount(5).make());
                p.getInventory().addItem(new ItemBuilder(Material.MONSTER_EGG).durability(999).name("&rSnowman Spawn Egg").amount(4).make());
                return;
            }

            if (p.getItemInHand().getItemMeta().getDisplayName().contains("Custom Kit")) {


                int kitIndex = Integer.parseInt(p.getItemInHand().getItemMeta().getDisplayName().replaceAll(ChatColor.RESET + "Custom Kit #", ""));
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                p.getInventory().clear();

                JsonArray jsonArray = core.getPlayerManager().getPlayer(p.getUniqueId()).getLayouts().get(kitIndex);
                for (JsonElement jsonElement : jsonArray) {
                    ItemStack itemStack = core.getItemSerializer().getItemFromString(jsonElement.getAsString());
                    if (CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemArmor) {
                        if (itemStack.getType().name().endsWith("_HELMET")) {
                            p.getInventory().setHelmet(itemStack);
                        } else if (itemStack.getType().name().endsWith("_CHESTPLATE")) {
                            p.getInventory().setChestplate(itemStack);
                        } else if (itemStack.getType().name().endsWith("_LEGGINGS")) {
                            p.getInventory().setLeggings(itemStack);
                        } else if (itemStack.getType().name().endsWith("_BOOTS")) {
                            p.getInventory().setBoots(itemStack);
                        }
                        continue;
                    }

                    p.getInventory().addItem(itemStack);
                }

            }

        }

    }

    @EventHandler
    public void pickupSpectator(PlayerPickupItemEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (iPlayer.hasMatch()) {
            if (iPlayer.getMatch().getDead().contains(iPlayer.getUuid())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void dropSpectator(PlayerDropItemEvent e) {
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (iPlayer.hasMatch()) {
            if (iPlayer.getMatch().getDead().contains(iPlayer.getUuid()) || iPlayer.getMatch().getMatchStage() == MatchStage.ENDED) {
                e.setCancelled(true);
            }
        }
    }


}
