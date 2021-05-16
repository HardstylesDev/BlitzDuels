package me.hardstyles.blitz.match;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
    final private HashSet<UUID> alive;
    final private HashMap<UUID, UUID> attacks;
    private HashMap<UUID, Entity> entities;
    private HashMap<UUID, Player> playerReference;
    private HashSet<Location> blocksPlaced;

    public Match(Core core, Arena arena) {
        this.matchStage = MatchStage.GRACE;
        this.core = core;
        this.isInProgress = true;
        this.playerReference = new HashMap<>();
        this.blocksPlaced = new HashSet<>();
        this.entities = new HashMap<>();
        this.players = new HashSet<>();
        this.attacks = new HashMap<>();
        this.alive = new HashSet<>();
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
            Player p = Bukkit.getPlayer(uuid);
            if (!p.isOnline()) {
                Bukkit.broadcastMessage("Aborted");
                arena.setOccupied(false);
                return;
            }
            IPlayer iPlayer = core.getPlayerManager().getPlayer(uuid);
            iPlayer.setMatch(this);
            alive.add(uuid);
            p.teleport(arena.getSpawns().get(pos));
            p.setFoodLevel(20);
            p.setHealth(20);
            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));

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
            send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.GREEN + "10" + ChatColor.YELLOW + " second!");
        }, 20 * 5);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.GREEN + "5" + ChatColor.YELLOW + " second!");

        }, 20 * 10);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.YELLOW + "3" + ChatColor.YELLOW + " second!");

        }, 20 * 12);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.GOLD + "2" + ChatColor.YELLOW + " second!");

        }, 20 * 13);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            send(pre + ChatColor.YELLOW + "Starting in " + ChatColor.RED + "1" + ChatColor.YELLOW + " second!");
        }, 20 * 14);
        core.getServer().getScheduler().runTaskLater(core, () -> {
            send("");
            send(pre + ChatColor.YELLOW + "Started! Good luck!");
            matchStage = MatchStage.STARTED;
        }, 20*15);
    }

    public HashSet<UUID> getAlive() {
        return this.alive;
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
        p.getWorld().strikeLightningEffect(p.getLocation());
        IPlayer player = core.getPlayerManager().getPlayer(uuid);

        player.addDeath();
        getAlive().remove(p.getUniqueId());
        if (attacks.get(p.getUniqueId()) == null) {
            send(player.getRank().getChatColor() + p.getName() + ChatColor.YELLOW + " was killed!");
        } else {
            IPlayer killer = core.getPlayerManager().getPlayer(attacks.get(p.getUniqueId()));
            killer.addKill();
            send(player.getRank().getChatColor() + p.getName() + ChatColor.YELLOW + " was killed by " + killer.getRank().getChatColor() + playerReference.get(attacks.get(p.getUniqueId())).getName() + ChatColor.YELLOW + "!");
        }
        if (getAlive().size() <= 1) {
            matchStage = MatchStage.ENDED;
            finish();
        }
        player.setMatch(null);
    }


    public void leave(UUID uuid) {
        if (!players.contains(uuid)) {
            return;
        }
        playerReference.remove(uuid);
        alive.remove(uuid);
        players.remove(uuid);
        attacks.remove(uuid);
    }

    public void finish() {
        for (Player player : playerReference.values()) {
            for (Location location : blocksPlaced) {
                location.getBlock().setType(Material.AIR);
            }

            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "Game over!");
            UUID wU = null;
            for (UUID uuid : alive) {
                wU = uuid;
                break;
            }
            if (wU == null) {
                return;
            }
            IPlayer winner = core.getPlayerManager().getPlayer(wU);
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Winner: " + winner.getRank().getChatColor() + playerReference.get(wU).getName());

        }
        core.getServer().getScheduler().runTaskLater(core, ()-> {
            for(Player player : playerReference.values()){
                player.teleport(core.getLobbySpawn());
            }
        }, 20*5);

    }

    public void end() {


    }

    public HashSet<Location> getBlocksPlaced() {
        return blocksPlaced;
    }

    public MatchStage getMatchStage() {
        return this.matchStage;
    }
}
