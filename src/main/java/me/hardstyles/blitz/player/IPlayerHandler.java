package me.hardstyles.blitz.player;

import me.elijuh.nametagapi.NametagAPI;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.nickname.Nickname;
import me.hardstyles.blitz.punishments.BannedPlayer;
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
    public void onConnect(PlayerLoginEvent e) {
        core.getServer().getScheduler().runTaskAsynchronously(core, () -> {
            BannedPlayer bannedPlayer = new BannedPlayer(core, e.getPlayer().getUniqueId());
            if (bannedPlayer.isBanned) {
                e.getPlayer().kickPlayer(bannedPlayer.reason);
            }
        });
    }


    @EventHandler
    public void onJoin(PlayerPreLoginEvent e) {
        core.getStatisticsManager().load(e.getUniqueId());
        core.getPlayerManager().addPlayer(e.getUniqueId(), core.getPlayerManager().getPlayer(e.getUniqueId()));


        //});

        System.out.println("Loaded players: " + core.getPlayerManager().getPlayers().size());
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Location location = event.getPlayer().getLocation();
        location.setWorld(Bukkit.getWorld("worldName"));
        Player p = event.getPlayer();

        if (p.getItemInHand().getType() == Material.NETHER_STAR) {
            if (p.getItemInHand().getItemMeta().getDisplayName().contains("Play")) {
                core.getServer().getScheduler().runTaskLater(core, () -> core.getQueueGui().open(p), 2);
            }
            return;
        }
        if (p.getItemInHand().getType() == Material.BOOK) {
            if (p.getItemInHand().getItemMeta().getDisplayName().contains("Kit Editor")) {
                core.getServer().getScheduler().runTaskLater(core, () -> core.getLayoutGui().open(p), 2);
            }

        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        e.setQuitMessage("");
        Bukkit.getScheduler().runTaskLater(core, () -> core.getPlayerManager().removeBsgPlayer(e.getPlayer().getUniqueId()), 4);

    }

    @EventHandler

    public void onJoin(PlayerJoinEvent e) {

        core.getServer().getScheduler().runTaskLater(core, () -> {

            Player p = e.getPlayer();

            core.getPlayerManager().hub(p);
            e.setJoinMessage("");
            p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
            p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
            p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
            p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
            p.setFoodLevel(20);
            IPlayer uhcPlayer;
            if (core.getPlayerManager().getPlayer(p.getUniqueId()) == null)
                uhcPlayer = new IPlayer(e.getPlayer().getUniqueId());
            uhcPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());
            uhcPlayer.setName(p.getDisplayName());
            uhcPlayer.setIp(p.getAddress().toString().split(":")[0].replaceAll("/", ""));
            p.setGameMode(GameMode.SURVIVAL);
            //p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 100.5, 0.5, 90, 0)); //todo change back
            //if (!(uhcPlayer.getRank() instanceof Default)) {
            //    p.setAllowFlight(true);
            //    p.setFlying(true);
            //}
            if (uhcPlayer.getNick() != null && uhcPlayer.getNick().isNicked()) {
                //e.setJoinMessage((ChatColor.YELLOW + uhcPlayer.getNick().getNickName() + " joined the game").replaceAll("  ", " "));
                Nickname nickname = new Nickname();
                if (uhcPlayer.getNick().getSkinSignature() == null) {
                    //e.setJoinMessage((ChatColor.YELLOW + uhcPlayer.getNick().getNickName() + " joined the game").replaceAll("  ", " "));
                    uhcPlayer.getNick().setNicked(true);
                    p.kickPlayer(ChatColor.GREEN + "Re-applied nick, please rejoin");
                    String[] skin = nickname.prepareSkinTextures(p, uhcPlayer.getNick().getNickName());
                    uhcPlayer.getNick().setNicked(true);
                    uhcPlayer.getNick().setSkinValue(skin[0]);
                    uhcPlayer.getNick().setSkinSignature(skin[1]);
                } else {
                    nickname.setNick(p, uhcPlayer.getNick().getNickName(), true);
                }
            } else {
                if (uhcPlayer.getRank() == null) {
                    uhcPlayer.setRank(core.getRankManager().getRankByName("Default"));
                }
            }

            p.setPlayerListName(uhcPlayer.getRank(true).getPrefix() + p.getName());

            core.getNametagManager().update();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                IPlayer iPlayer = core.getPlayerManager().getPlayer(onlinePlayer.getUniqueId());
                if (iPlayer.getParty() != null) {
                    if(iPlayer.getParty().getOwner().equals(p.getUniqueId())){
                        uhcPlayer.setParty(iPlayer.getParty());
                        uhcPlayer.getParty().setOwner(p.getUniqueId());
                    }
                    if (iPlayer.getParty().getMembers().contains(p.getUniqueId())) {
                        uhcPlayer.setParty(iPlayer.getParty());
                    }
                }
            }

         //   NametagAPI.setNametag(p.getName(), uhcPlayer.getRank().getPrefix(), "");



        }, 1l);



    }


    @EventHandler
    public void onAsyncChat(PlayerChatEvent e) {
        IPlayer uhcPlayer = core.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        e.setFormat(uhcPlayer.getRank(true).getPrefix() + e.getPlayer().getName() + (uhcPlayer.getRank(true).getPrefix().equalsIgnoreCase(ChatColor.GRAY + "") ? ChatColor.GRAY + ": " : ChatColor.WHITE + ": ") + e.getMessage().replaceAll("%", "%%"));
    }


    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        if (!(e.getItem().getItemStack().getType() == Material.POTION)) {
            return;
        }
        if (e.getPlayer().getInventory().firstEmpty() == -1) {
            return;
        }
        e.setCancelled(true);
        e.getPlayer().getInventory().addItem(e.getItem().getItemStack());
        e.getItem().remove();
        e.getPlayer().playSound(e.getItem().getLocation(), Sound.ITEM_PICKUP, (float) 0.1, (float) 1.5);
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
    public void interact(PlayerInteractEvent e) {
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("world") && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
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
