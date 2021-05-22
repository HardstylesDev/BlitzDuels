package me.hardstyles.blitz.match;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Match {
    Arena arena;
    private boolean isInProgress;
    private String pre = ChatColor.GREEN + "Match " + ChatColor.WHITE + "> ";
    private MatchStage matchStage;
    final HashSet<UUID> players;
    final private Core core;
    final private HashSet<UUID> alivePlayers;
    final private HashSet<Location> chests;
    final private HashSet<UUID> dead;
    final private HashMap<UUID, UUID> attacks;
    private HashSet<UUID> winners;
    private HashMap<UUID, HashSet<Entity>> entities;
    private HashMap<UUID, Player> playerReference;
    private HashSet<Location> blocksPlaced;
    private long timeStarted;
    private long timeEnded;

    public Match(Core core, Arena arena) {

        this.core = core;
        this.matchStage = MatchStage.GRACE;

        this.playerReference = new HashMap<>();
        this.alivePlayers = new HashSet<>();
        this.blocksPlaced = new HashSet<>();
        this.entities = new HashMap<>();
        this.players = new HashSet<>();
        this.attacks = new HashMap<>();
        this.winners = new HashSet<>();
        this.chests = new HashSet<>();
        this.dead = new HashSet<>();
        this.isInProgress = true;
        this.timeStarted = 0;
        this.timeEnded = 0;

        this.arena = arena;

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
            iPlayer.setMatch(this);
            Player p = playerReference.get(uuid);
            if (!p.isOnline()) {
                arena.setOccupied(false);
                return;
            }

            alivePlayers.add(uuid);
            core.getPlayerManager().reset(p);

            p.teleport(arena.getSpawns().get(pos));
            for (Entity nearbyEntity : arena.getSpawns().get(pos).getWorld().getNearbyEntities(arena.getSpawns().get(pos), 250, 100, 250)) {
                if (!(nearbyEntity instanceof Player)) {
                    nearbyEntity.remove();
                }
            }

            p.getInventory().addItem(new ItemBuilder(Material.BOOK).name("&rKit #1").amount(1).make());
            pos++;
            if (pos == arena.getSpawns().size()) {
                pos = 0;
            }
        }

        startCooldown();
    }

    public void startCooldown() {
        send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.GREEN + "15" + ChatColor.YELLOW + " second!");
        core.getServer().getScheduler().runTaskLater(core, () -> {
            if (matchStage == MatchStage.GRACE) {
                send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.GREEN + "10" + ChatColor.YELLOW + " second!");
            }
        }, 20 * 5);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            if (matchStage == MatchStage.GRACE) {
                send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.GREEN + "5" + ChatColor.YELLOW + " second!");
            }
        }, 20 * 10);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            if (matchStage == MatchStage.GRACE) {
                send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.YELLOW + "3" + ChatColor.YELLOW + " second!");
            }

        }, 20 * 12);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            if (matchStage == MatchStage.GRACE) {
                send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.GOLD + "2" + ChatColor.YELLOW + " second!");
            }

        }, 20 * 13);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            if (matchStage == MatchStage.GRACE) {
                send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.RED + "1" + ChatColor.YELLOW + " second!");
            }
        }, 20 * 14);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            if (matchStage == MatchStage.GRACE) {
                send("");
                send(pre + ChatColor.YELLOW + "Started! Good luck!");
                matchStage = MatchStage.STARTED;
                timeStarted = System.currentTimeMillis();
            }
        }, 20 * 15);
    }

    public HashSet<UUID> getAlivePlayers() {
        return this.alivePlayers;
    }

    public HashMap<UUID, UUID> getAttacks() {
        return this.attacks;
    }

    public void send(String s) {
        for (UUID uuid : playerReference.keySet()) {
            if (playerReference.get(uuid).isOnline()) {
                playerReference.get(uuid).sendMessage(s);
            }
        }
    }


    public void setInProgress(boolean a) {
        this.isInProgress = a;
    }

    public boolean isInProgress() {
        return this.isInProgress;
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
        core.getStatisticsManager().saveAsync(player);

        if (attacks.get(p.getUniqueId()) == null) {
            send(player.getRank().getChatColor() + p.getName() + ChatColor.YELLOW + " was killed!");
        } else {
            IPlayer killer = core.getPlayerManager().getPlayer(attacks.get(p.getUniqueId()));
            killer.addKill();
            send(player.getRank().getChatColor() + p.getName() + ChatColor.YELLOW + " was killed by " + killer.getRank().getChatColor() + playerReference.get(attacks.get(p.getUniqueId())).getName() + ChatColor.YELLOW + "!");
            playerReference.get(killer.getUuid()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 12, 1));

            core.getStatisticsManager().saveAsync(killer);
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
        playerReference.remove(uuid);
        alivePlayers.remove(uuid);
        dead.add(uuid);
        players.remove(uuid);
        attacks.remove(uuid);
    }

    public void finish() {
        timeEnded = System.currentTimeMillis();
        for (Player player : playerReference.values()) {

            for (Location location : blocksPlaced) {
                location.getBlock().setType(Material.AIR);
            }

            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "Game over!");
            player.getInventory().clear();
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
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Winner: " + winner.getRank().getChatColor() + playerReference.get(wU).getName());

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

    }

    public void end() {


    }

    public HashSet<Location> getBlocksPlaced() {
        return blocksPlaced;
    }

    public MatchStage getMatchStage() {
        return this.matchStage;
    }

    public HashMap<UUID, Player> getPlayerReference() {
        return playerReference;
    }

    public HashSet<UUID> getDead() {
        return dead;
    }

    public long getTimeStarted() {
        return this.timeStarted;
    }

    public long getTimeEnded() {
        return timeEnded;
    }

    public HashSet<UUID> getWinners() {
        return winners;
    }

    public HashMap<UUID, HashSet<Entity>> getEntities() {
        return entities;
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
