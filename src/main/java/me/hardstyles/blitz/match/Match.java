package me.hardstyles.blitz.match;

import lombok.Getter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Getter

public class Match {
    Arena arena;
    private boolean isInProgress;
    private String pre = ChatColor.GREEN + "Match " + ChatColor.WHITE + "> ";
    private MatchStage matchStage;
    final HashSet<UUID> players = new HashSet<>();
    final private Core core;
    final private HashSet<UUID> alivePlayers = new HashSet<>();
    final private HashSet<Location> chests = new HashSet<>();
    final private HashSet<UUID> dead = new HashSet<>();
    final private HashMap<UUID, UUID> attacks = new HashMap<>();
    private HashSet<UUID> winners = new HashSet<>();
    private HashMap<UUID, HashSet<Entity>> entities = new HashMap<>();
    private HashMap<UUID, Player> playerReference = new HashMap<>();
    private HashMap<UUID, Double> damageDone = new HashMap<>();
    private HashSet<Location> blocksPlaced = new HashSet<>();
    private long timeStarted, timeEnded;

    public Match(Core core, Arena arena) {

        this.core = core;
        this.matchStage = MatchStage.GRACE;
        this.isInProgress = true;
        this.arena = arena;
        core.getMatchManager().add();

    }


    public void add(Player player) {
        players.add(player.getUniqueId());
        playerReference.put(player.getUniqueId(), player);
    }

    public void add(UUID uuid) {

        players.add(uuid);
        playerReference.put(uuid, Bukkit.getPlayer(uuid));
    }

    public void start() {
        arena.setOccupied(true);
        int pos = 0;
        for (UUID uuid : players) {
            IPlayer iPlayer = core.getPlayerManager().getPlayer(uuid);
            if (iPlayer == null) {
                continue;
            }
            iPlayer.setMatch(this);
            Player p = playerReference.get(uuid);

            if (!p.isOnline()) {
                arena.setOccupied(false);
                return;
            }

            alivePlayers.add(uuid);
            core.getPlayerManager().reset(p);

            p.teleport(arena.getSpawns().get(pos));
            p.closeInventory();
            for (Entity nearbyEntity : arena.getSpawns().get(pos).getWorld().getNearbyEntities(arena.getSpawns().get(pos), 250, 100, 250)) {
                if (!(nearbyEntity instanceof Player) && !(nearbyEntity instanceof ArmorStand)) {
                    nearbyEntity.remove();
                }
            }

            p.getInventory().addItem(new ItemBuilder(Material.BOOK).name("&rDefault").amount(1).make());


            for (int i = 1; i < 7; i++) {
                if (iPlayer.getLayouts().get(i) != null) {
                    p.getInventory().addItem(new ItemBuilder(Material.BOOK).name("&rCustom Kit #" + i).amount(1).make());
                }
            }


            pos++;
            if (pos == arena.getSpawns().size()) {
                pos = 0;
            }
        }

        startCooldown();
    }


    public void startCooldown() {
        new BukkitRunnable() {
            private int timer = 10;

            @Override
            public void run() {
                timer--;

                if (timer == 0 || matchStage != MatchStage.GRACE) {
                    send(pre + ChatColor.YELLOW + "Started! Good luck!");
                    matchStage = MatchStage.STARTED;
                    timeStarted = System.currentTimeMillis();
                    cancel();
                }
                ChatColor cc;
                switch (timer) {
                    case 5:
                    case 10: {
                        cc = ChatColor.GREEN;
                        break;
                    }
                    case 3:
                        cc = ChatColor.YELLOW;
                        break;
                    case 2:
                        cc = ChatColor.GOLD;
                        break;
                    case 1:
                        cc = ChatColor.RED;
                        break;
                    default:
                        cc = null;
                }
                if (cc != null) {
                    send(pre + ChatColor.YELLOW + "Starting in " + cc + timer + ChatColor.YELLOW + " second!");
                    sound(Sound.NOTE_STICKS, 1, 1);
                }
            }
        }.runTaskTimerAsynchronously(core, 0L, 20L);
    }


    public void send(String s) {
        for (Map.Entry<UUID, Player> entry : playerReference.entrySet()) {
            Player p = entry.getValue();
            if (p == null) {
                continue;
            }
            if (p.isOnline()) {
                p.sendMessage(s);
            }
        }
    }

    public void sound(Sound sound, float volume, float pitch) {
        for (Map.Entry<UUID, Player> entry : playerReference.entrySet()) {
            Player p = entry.getValue();
            if (p == null) {
                continue;
            }
            if (p.isOnline()) {
                p.playSound(p.getLocation(), sound, volume, pitch);
            }

        }
    }


    public void onDeath(UUID uuid) {
        Player p = playerReference.get(uuid);
        p.spigot().setCollidesWithEntities(false);


        p.getWorld().strikeLightningEffect(p.getLocation());
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 1), true);
        p.setGameMode(GameMode.SURVIVAL);
        IPlayer player = core.getPlayerManager().getPlayer(uuid);
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null && i.getType() != Material.AIR) {
                p.getWorld().dropItemNaturally(p.getLocation(), i);
            }
        }
        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (i != null && i.getType() != Material.AIR) {
                p.getWorld().dropItemNaturally(p.getLocation(), i);
            }
        }
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);


        p.setAllowFlight(true);
        p.setFlying(true);

        p.setVelocity(new Vector(0, 1, 0));
        dead.add(p.getUniqueId());
        alivePlayers.remove(p.getUniqueId());

        for (UUID alivePlayer : alivePlayers) {
            Player alive = playerReference.get(alivePlayer);
            alive.hidePlayer(p);
        }
        for (UUID deadPlayer : dead) {
            Player dead = playerReference.get(deadPlayer);
            dead.showPlayer(p);
            p.showPlayer(dead);
        }
        player.addDeath();
        player.setStreak(0);
        core.getStatisticsManager().saveAsync(player);

        if (attacks.get(p.getUniqueId()) == null) {
            send(player.getRank(true).getChatColor() + p.getName() + ChatColor.YELLOW + " was killed!");
        } else {
            IPlayer killer = core.getPlayerManager().getPlayer(attacks.get(p.getUniqueId()));
            if (killer != null) {
                killer.addKill();
                send(player.getRank(true).getChatColor() + p.getName() + ChatColor.YELLOW + " was killed by " + killer.getRank().getChatColor() + playerReference.get(attacks.get(p.getUniqueId())).getName() + ChatColor.YELLOW + "!");
                playerReference.get(killer.getUuid()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 12, 1));
                core.getStatisticsManager().saveAsync(killer);
            }
        }
        if (getAlivePlayers().size() <= 1) {
            matchStage = MatchStage.ENDED;
            finish();
        }
        if (entities.containsKey(uuid)) {
            for (Entity entity : entities.get(uuid)) {
                entity.remove();
            }
        }
    }


    public void leave(UUID uuid) {
        if (!players.contains(uuid)) {
            return;
        }

        alivePlayers.remove(uuid);
        dead.add(uuid);
        players.remove(uuid);
        attacks.remove(uuid);
    }

    public void finish() {
        timeEnded = System.currentTimeMillis();
        UUID wU = null;
        for (UUID uuid : alivePlayers) {
            wU = uuid;
            break;
        }
        if (wU == null) {
            return;
        }
        this.winners.add(wU);
        IPlayer winner = core.getPlayerManager().getPlayer(wU);
        for (Player player : playerReference.values()) {

            if(!player.isOnline() || player == null){
                continue;
            }
            for (Location location : blocksPlaced) {
                location.getBlock().setType(Material.AIR);
            }

            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "Game over!");
            if (damageDone.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.GRAY + "Damage dealt: " + ChatColor.WHITE + (damageDone.get(player.getUniqueId()) * 2));
            }
            player.getInventory().clear();


            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Winner: " + winner.getRank(true).getChatColor() + playerReference.get(wU).getName());

            for (Entity nearbyEntity : player.getWorld().getNearbyEntities(player.getLocation(), 250, 100, 250)) {
                if (!(nearbyEntity instanceof Player)) {
                    nearbyEntity.remove();
                }
            }
            for (UUID uuid : dead) {
                Player dead = playerReference.get(uuid);
                for (UUID alivePlayer : alivePlayers) {
                    Player alive = playerReference.get(alivePlayer);
                    alive.showPlayer(dead);
                    dead.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
            arena.setOccupied(false);
        }
        winner.addWin();
        winner.setStreak(winner.getStreak() + 1);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            for (UUID uuid : entities.keySet()) {
                for (Entity entity : entities.get(uuid)) {
                    entity.remove();
                }
            }
        }, 10);

        core.getServer().getScheduler().runTaskLater(core, () -> {
            for (Player player : playerReference.values()) {
                if (!player.isOnline()) {
                    continue;
                }
                if (core.getPlayerManager().getPlayer(player.getUniqueId()).getMatch() == this) {
                    core.getPlayerManager().getPlayer(player.getUniqueId()).setMatch(null);
                }

                core.getPlayerManager().hub(player);
            }
        }, 20 * 5);
        core.getMatchManager().remove();
        core.getLeaderboardUpdater().update();
    }

    public void end() {
    }

    public void addEntity(UUID uuid, Entity entity) {
        if (!entities.containsKey(uuid)) {
            entities.put(uuid, new HashSet<>());
        }
        entities.get(uuid).add(entity);
    }

    public void entityTeleport() {
        for (UUID uuid : entities.keySet()) {
            Player owner = playerReference.get(uuid);
            for (Entity entity : entities.get(uuid)) {
                if (!entity.isValid() || entity.isDead()) continue;
                if (entity.getLocation().distance(owner.getLocation()) > 15) {
                    entity.teleport(owner.getLocation());
                }

            }
        }
    }


}
