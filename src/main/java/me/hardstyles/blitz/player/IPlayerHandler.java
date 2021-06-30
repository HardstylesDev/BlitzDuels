package me.hardstyles.blitz.player;

import me.elijuh.nametagapi.NametagAPI;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.nickname.Nickname;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

public class IPlayerHandler implements Listener {

    final private Core core;

    public IPlayerHandler(Core core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.getWorld().getName().equalsIgnoreCase("world") && p.getGameMode() != GameMode.CREATIVE) {
            if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
                e.setCancelled(true);
        }

        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        if (item.getItemMeta().getDisplayName().equals("§ePlay")) {
            core.getQueueGui().open(p);
        } else if (item.getItemMeta().getDisplayName().equals("§eKit Editor")) {
            core.getSlotGui().open(p);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        core.getQueueManager().getSoloQueues().remove(iPlayer.getUuid());
        if (iPlayer.getParty() != null) {
            core.getQueueManager().getTeamsQueues().remove(iPlayer.getParty());
        }

        Bukkit.getScheduler().runTask(core, () -> core.getPlayerManager().removeBsgPlayer(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        core.getStatisticsManager().load(e.getPlayer().getUniqueId());
        core.getServer().getScheduler().runTaskLater(core, () -> {

            Player p = e.getPlayer();

            core.getPlayerManager().hub(p);
            p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
            p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
            p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
            p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
            p.setFoodLevel(20);
            IPlayer iPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
            iPlayer.setName(p.getDisplayName());
            iPlayer.setIp(p.getAddress().getAddress().getHostAddress());
            p.setGameMode(GameMode.SURVIVAL);
            if (iPlayer.getNick() != null && iPlayer.getNick().isNicked()) {
                Nickname nickname = new Nickname();
                if (iPlayer.getNick().getSkinSignature() == null) {
                    iPlayer.getNick().setNicked(true);
                    p.kickPlayer(ChatColor.GREEN + "Re-applied nick, please rejoin");
                    String[] skin = nickname.prepareSkinTextures(p, iPlayer.getNick().getNickName());
                    iPlayer.getNick().setNicked(true);
                    iPlayer.getNick().setSkinValue(skin[0]);
                    iPlayer.getNick().setSkinSignature(skin[1]);
                } else {
                    nickname.setNick(p, iPlayer.getNick().getNickName(), true);
                }
            } else {
                if (iPlayer.getRank() == null) {
                    iPlayer.setRank(core.getRankManager().getRankByName("Default"));
                }
            }

            p.setPlayerListName(iPlayer.getRank().getPrefix() + p.getName());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                IPlayer all = core.getPlayerManager().getPlayer(onlinePlayer.getUniqueId());
                if (all.getParty() != null) {
                    if(all.getParty().getOwner().equals(p.getUniqueId())){
                        all.setParty(all.getParty());
                        all.getParty().setOwner(p.getUniqueId());
                    }
                    if (all.getParty().getMembers().contains(p.getUniqueId())) {
                        all.setParty(all.getParty());
                    }
                }
            }
            NametagAPI.setNametag(p.getName(), iPlayer.getRank().getPrefix(), "", iPlayer.getRank().getPosition());
            core.getPunishmentManager().updateData(iPlayer);
        }, 1L);
    }


    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        IPlayer uhcPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        e.setFormat(uhcPlayer.getRank().getPrefix() + e.getPlayer().getName() + (uhcPlayer.getRank().getPrefix().equalsIgnoreCase(ChatColor.GRAY + "") ? ChatColor.GRAY + ": " : ChatColor.WHITE + ": ") + e.getMessage().replaceAll("%", "%%"));
    }


    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {

        if (e.getItem().getItemStack().getType() != Material.POTION) {
            return;
        }
        if (e.getPlayer().getInventory().firstEmpty() == -1) {
            return;
        }
        e.setCancelled(true);
        e.getPlayer().getInventory().addItem(e.getItem().getItemStack());
        e.getItem().remove();
        e.getPlayer().playSound(e.getItem().getLocation(), Sound.ITEM_PICKUP, 0.1f, 1.5f);
    }

    @EventHandler
    public void damageEvent(EntityDamageEvent e) {
        if (e.getEntity().getWorld().getName().equalsIgnoreCase("world")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void foodEvent(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getEntity().getWorld().getName().equalsIgnoreCase("world")) {
            ((Player) e.getEntity()).setSaturation(20f);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        if (e.getBlock().getWorld().getName().equalsIgnoreCase("world") && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent e) {
        if (e.getBlock().getWorld().getName().equalsIgnoreCase("world") && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void dropEvent(PlayerDropItemEvent e) {
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void weather(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }


}
