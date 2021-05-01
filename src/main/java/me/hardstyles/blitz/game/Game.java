package me.hardstyles.blitz.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.map.Map;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import me.hardstyles.blitz.utils.ItemBuilder;
import me.hardstyles.blitz.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.*;

public class Game {

    private boolean speedMode = false;
    private NextEvent nextEvent;
    private boolean canFindStar = false;

    public static enum GameMode {
        LOADING, INACTIVE, WAITING,
        STARTING, INGAME, RESETING
    }

    public static enum NextEvent {
        STAR, REFILL, DEATHMATCH, ENDING
    }

    private HashSet<Player> allPlayers;
    private ArrayList<Player> alivePlayers;
    private ArrayList<Player> deadPlayers;

    private GameMode gameMode;

    private me.hardstyles.blitz.map.Map map;
    private Player winner;

    private HashMap<Player, Boolean> votes;
    private ArrayList<Location> openedChests;
    private ArrayList<Location> starChests;
    private HashSet<Location> spawnUsed;
    private boolean isDeathmatchStarting = false;

    private int countdownTime, gameTime, deathmatchCountDownTime;

    private double borderSize;
    private double borderShrinkBy;

    public Game() {
        gameMode = GameMode.LOADING;
        map = BlitzSG.getInstance().getArenaManager().getRandomArena();

        this.openedChests = new ArrayList<>();
        this.starChests = new ArrayList<>();

        BlitzSG.getInstance().getArenaManager().fixSpawns(map);
        if (map == null || map.getSpawns().get(0).getWorld() == null) {
            return;
        }
        map.setInUse(true);
        BlitzSG.getInstance().getGameManager().addGame(this);
        allPlayers = new HashSet<Player>();
        alivePlayers = new ArrayList<Player>();
        deadPlayers = new ArrayList<Player>();
        votes = new HashMap<Player, Boolean>();
        spawnUsed = new HashSet<Location>();
        gameMode = GameMode.WAITING;

        // for (Location loc : arena.getSpawns()) {
//
        //     loc.getBlock().setType(Material.AIR);
        // }
        // borderSize = Math.max(arena.getArenaMaxCorner().getBlockX(), arena.getArenaMaxCorner().getBlockZ()) - Math.max(arena.getCenter().getBlockX(), arena.getCenter().getBlockZ());
        // borderShrinkBy = -0.00625;
        // arena.getArenaWorld().getWorldBorder().setSize(borderSize * 2);
        // arena.getArenaWorld().getWorldBorder().setCenter(arena.getCenter().clone().add(0.5, 0, 0.5));
        // arena.getArenaWorld().getWorldBorder().setSize(borderSize * 2);
    }

    public void addPlayer(Player p) {
        if (map == null || map.getSpawns().get(0).getWorld() == null) {
            p.sendMessage(BlitzSG.CORE_NAME + ChatColor.YELLOW + "Couldn't find an available arena!");
            return;
        }
        BlitzSGPlayer uhcPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        uhcPlayer.getGameEntities().clear();
        if (uhcPlayer.isInGame()) {

            //BlitzSG.send(p, BlitzSG.CORE_NAME + "&cYou are already in a game!");

            return;
        }
        if (map.getSpawns().size() < alivePlayers.size() + 1) {
            BlitzSG.send(p, BlitzSG.CORE_NAME + "&cThe game is already full!");
            return;
        }

        uhcPlayer.setGame(this);
        BlitzSG.getInstance().getNametagManager().update();

        allPlayers.add(p);
        alivePlayers.add(p);
        p.teleport(map.getLobby().clone().add(0.5, 0, 0.5));
        msgAll(BlitzSG.CORE_NAME + "&7" + uhcPlayer.getRank(true).getChatColor() + p.getName() + " &ehas joined (&b" + alivePlayers.size() + "&e/&b" + map.getSpawns().size() + "&e)!");
        if (alivePlayers.size() >= 2 && gameMode.equals(GameMode.WAITING)) {
            startLobbyCountdown();
        }
        resetPlayer(p);
        p.setFlying(false);
        p.setAllowFlight(false);

    }

    public void teleportSpawn(Player p) {
        Location playerSpawn = null;
        for (Location l : map.getSpawns())
            if (!spawnUsed.contains(l)) {
                playerSpawn = l;
                break;
            }
        //createCage(playerSpawn);

        spawnUsed.add(playerSpawn);
        BlitzSGPlayer blitzSGPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        blitzSGPlayer.setGameSpawn(playerSpawn.clone().add(0.5, 1.0, 0.5));

        p.teleport(playerSpawn.clone().add(0.5, 1.0, 0.5));


    }

    public void removePlayer(Player p) {
        if (gameMode == GameMode.INGAME) {
            BlitzSG.send(p, BlitzSG.CORE_NAME + "&cYou can't leave while the game is running!");
            //killPlayer(p);
            return;
        }
        BlitzSGPlayer uhcPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        uhcPlayer.setGame(null);
        spawnUsed.remove(map.getSpawns().get(alivePlayers.indexOf(p)));
        alivePlayers.remove(p);
        allPlayers.remove(p);
        msgAll(BlitzSG.CORE_NAME + "&7" + p.getName() + " &ehas left (&b" + alivePlayers.size() + "&e/&b" + map.getSpawns().size() + "&e)!");
        resetPlayer(p);
        p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 100.5, 0.5, 90, 0)); //todo change back
        BlitzSG.getInstance().getBlitzSGPlayerManager().setLobbyInventoryAndNameTag(p);
        BlitzSG.send(p, BlitzSG.CORE_NAME + "&cYou have left the game!");
    }

    private boolean startedCountdown = false;

    public void startLobbyCountdown() {
        if (startedCountdown)
            return;
        startedCountdown = true;
        countdownTime = speedMode ? 6 : 26; // 21


        new BukkitRunnable() {
            public void run() {
                countdownTime--;
                if (alivePlayers.size() < 2) {
                    this.cancel();
                    gameMode = GameMode.WAITING;
                    startedCountdown = false;
                    msgAll("&cWe don't have enough players! Countdown cancelled.");
                    return;
                }
                if (countdownTime == 0) {

                    for (Player alivePlayer : alivePlayers) {
                        setPregameInventory(alivePlayer);
                        teleportSpawn(alivePlayer);
                    }

                    startCountDown();
                    this.cancel();


                    return;
                }
                if (countdownTime % 10 == 0 || countdownTime <= 10) {
                    if (countdownTime <= 10)
                        msgAll(BlitzSG.CORE_NAME + "&e" + countdownTime + " &eseconds until the game starts!");
                }
            }
        }.runTaskTimer(BlitzSG.getInstance(), 0, 20);
    }

    public void startCountDown() {
        Bukkit.getServer().setWhitelist(true);
        for (Player alivePlayer : Bukkit.getOnlinePlayers()) {
            for (Player dickplayer : Bukkit.getOnlinePlayers())
                alivePlayer.showPlayer(dickplayer);
        }
        Jedis jedisResource = BlitzSG.getInstance().getJedisPool().getResource();
        jedisResource.set("canJoin", "false");
        jedisResource.close();
        gameMode = GameMode.STARTING;
        countdownTime = speedMode ? 6 : 31;
        ; // 31

        new BukkitRunnable() {
            public void run() {
                countdownTime--;
                if (countdownTime == 30) {
                    BlitzSG.broadcast("", Bukkit.getWorld("world"));
                    BlitzSG.broadcast("&eA BSG &egame on the map &a" + map.getName() + " &eis bound to start in &b" + countdownTime + " &eseconds. Use &6/bsg join &eto enter the game.", Bukkit.getWorld("world"));
                    BlitzSG.broadcast("", Bukkit.getWorld("world"));
                }
                if (alivePlayers.size() < 2) {
                    this.cancel();
                    endGame(true);
                    return;
                }
                if (countdownTime == 0) {
                    startGame();
                    this.cancel();
                    return;
                }
                // if (countdownTime == 1){
                //     for (Player p : alivePlayers) {

                //         BlitzSGPlayer bsgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
                //         p.teleport(bsgPlayer.getGameSpawn());
                //     }
                // }
                if (countdownTime % 10 == 0 || countdownTime <= 5) {
                    if (countdownTime <= 5)
                        msgAll(BlitzSG.CORE_NAME + "&eYou will be able to move in &c" + countdownTime + " &eseconds!");
                    else if (countdownTime <= 15)
                        msgAll(BlitzSG.CORE_NAME + "&eYou will be able to move in &6" + countdownTime + " &eseconds!");
                    else if (countdownTime <= 25)
                        msgAll(BlitzSG.CORE_NAME + "&eYou will be able to move in &e" + countdownTime + " &eseconds!");
                    else if (countdownTime <= 30) {
                        msgAll(BlitzSG.CORE_NAME + "&eYou will be able to move in " + countdownTime + " seconds! Choose a kit by right clicking the bow!");

                    }
                }
            }
        }.runTaskTimer(BlitzSG.getInstance(), 0, 20);
    }

    public void startGame() {
        nextEvent = NextEvent.STAR;
        gameMode = GameMode.INGAME;
        map.getArenaWorld().setTime(0);
        for (Player p : alivePlayers) {
            p.closeInventory();
            resetPlayer(p);

            BlitzSG.getInstance().getKarhuAnticheat().setEnabled(true);
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 2, 1);

            // p.sendMessage("&aThe game has started, Good Luck!");
            BlitzSGPlayer bsgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());

            if (bsgPlayer.getSelectedKit() == null)
                bsgPlayer.setSelectedKit(BlitzSG.getInstance().getKitManager().getKit("Knight"));
            BlitzSG.send(p, BlitzSG.CORE_NAME + "&eYou will get the items for your " + bsgPlayer.getSelectedKit().getName() + " kit in 60 seconds.");
            BlitzSG.send(p, BlitzSG.CORE_NAME + "&6The Blitz Star will be released in 5 minutes!");
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60 * 20, 2));
            p.getInventory().setItem(0, new ItemBuilder(Material.COMPASS).name("&rTracking Device").make());


        }
        for (Location l : map.getSpawns())
            if (spawnUsed.contains(l))
                //removeCage(l);
                gameTime = 0;
        new BukkitRunnable() {
            public void run() {
                if (alivePlayers.size() < 2) {
                    this.cancel();
                    endGame(false);
                    return;
                }
                if (gameTime > 53 && gameTime < 59)
                    msgAll(BlitzSG.CORE_NAME + "&eYou will get your items in " + (59 - gameTime) + " seconds!");
                if (gameTime == 59) {
                    msgAll("&eThe grace period has ended, PvP is now enabled!");
                    for (Player p : alivePlayers) {
                        p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        BlitzSGPlayer bsgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
                        bsgPlayer.getSelectedKit().giveKit(p, bsgPlayer.getKitLevel(bsgPlayer.getSelectedKit()));
                        BlitzSG.send(p, BlitzSG.CORE_NAME + "&eYou got your " + bsgPlayer.getSelectedKit().getName() + " kit!");
                    }
                    msgAll(BlitzSG.CORE_NAME + "&6The Blitz Star will be released in 4 minutes!");
                    // kit.giveKit(p, BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId()).getKitLevel(kit));

                }
                if (gameTime == 119)
                    msgAll(BlitzSG.CORE_NAME + "&6The Blitz Star will be released in 3 minutes!");
                if (gameTime == 179)
                    msgAll(BlitzSG.CORE_NAME + "&6The Blitz Star will be released in 2 minutes!");
                if (gameTime == 239)
                    msgAll(BlitzSG.CORE_NAME + "&6The Blitz Star will be released in 1 minutes!");
                if (gameTime == 299) {
                    msgAll(BlitzSG.CORE_NAME + "&6The Blitz Star has been hidden in a chest! Find it to activate your Blitz!");
                    canFindStar = true;

                    starChests = new ArrayList<>();
                    if (!isDeathmatchStarting)
                        nextEvent = NextEvent.REFILL;
                }

                if (gameTime == 479) {
                    //msgAll("&cSudden Death has begun, all players will now take continuous damage until the Game End.");

                }
				/*if(gameTime >= 479) {
					for(Player p : players)
						p.damage(1);
				}*/
                if (gameTime == 599) {
                    refillChests();
                    nextEvent = NextEvent.DEATHMATCH;
                }
                if (gameTime == 839 || gameTime == 869)
                    //msgAll(BlitzSG.CORE_NAME + "&eDeathmatch begins in " + (gameTime == 839 ? 60 : 30) + " seconds!");
                    if (!isDeathmatchStarting) {
                        isDeathmatchStarting = true;
                        startDeathmatchCounter(gameTime);
                    }
                // if (gameTime > 893 && gameTime < 899)
                //     msgAll(BlitzSG.CORE_NAME + "&eDeathmatch begins in " + (899 - gameTime) + " second" + (((899 - gameTime) == 1) ? "" : "s") + "!");
                // if (gameTime == 899) {
                //     nextEvent = NextEvent.ENDING;
                //     msgAll(BlitzSG.CORE_NAME + "&eDeathmatch started! You cannot damage anyone for 15 seconds!");
                // }

                if (gameTime == 1199) {
                    this.cancel();
                    endGame(true);
                    return;
                }
                gameTime++;
                if (alivePlayers.size() > 1) {
                    for (Player alivePlayer : alivePlayers) {

                        compassTarget(alivePlayer);
                        mobTeleport(alivePlayer);
                    }
                }
            }
        }.runTaskTimer(BlitzSG.getInstance(), speedMode ? 4 : 20, speedMode ? 4 : 20);
    }

    private int deathmatchStartTime = 0;
    private boolean startedDeathmatch = false;

    public void startDeathmatchCounter(int currentTime) {
        if (startedDeathmatch)
            return;
        startedDeathmatch = true;
        deathmatchCountDownTime = currentTime;
        isDeathmatchStarting = true;
        //default = 599
        // 620
        String bs = ChatColor.BLUE + "[GAME] ";
        new BukkitRunnable() {
            public void run() {
                if (deathmatchStartTime == 15) {
                    msgAll(BlitzSG.CORE_NAME + "&eDeathmatch begins in 30 seconds!");
                    msgAll(BlitzSG.CORE_NAME + "&eThe Blitz Star can no longer be used!");
                }
                if (deathmatchStartTime > 39 && deathmatchStartTime < 45)
                    msgAll(BlitzSG.CORE_NAME + "&eDeathmatch begins in " + (45 - deathmatchStartTime) + " second" + (((45 - deathmatchStartTime) == 1) ? "" : "s") + "!");


                if (deathmatchStartTime == 45) {
                    startDeathmatch();
                    msgAll(BlitzSG.CORE_NAME + "&eDeathmatch started! You cannot damage anyone for 15 seconds!");
                }
                if (deathmatchStartTime > 54 && deathmatchStartTime < 60)
                    msgAll(BlitzSG.CORE_NAME + "&eYou will be able to damage players in " + (60 - deathmatchStartTime) + " second" + (((60 - deathmatchStartTime) == 1) ? "" : "s") + "!");
                if (deathmatchStartTime == 60) {
                    BlitzSG.getInstance().getKarhuAnticheat().setEnabled(true);
                    msgAll(BlitzSG.CORE_NAME + "&eKill! Kill! Kill!");
                }
                if (deathmatchStartTime == 240) {

                    this.cancel();
                    endGame(true);
                    return;
                }

                deathmatchCountDownTime++;
                deathmatchStartTime++;

            }
        }.runTaskTimer(BlitzSG.getInstance(), speedMode ? 4 : 20, speedMode ? 4 : 20);


    }

    private void startDeathmatch() {
        if (map.getDeathmatch() == null)
            return;
        Location dm = map.getDeathmatch();
        if (map.getDeathmatchDistance() == 0) {
            System.out.println("I took this path, looks nice or msth");
            alivePlayers.forEach(player -> player.teleport(dm));
            return;
        }
        int dist = map.getDeathmatchDistance();
        Location[] locations = new Location[]{dm.clone().add(dist, 0, 0), dm.clone().add(-dist, 0, 0), dm.clone().add(0, 0, dist), dm.clone().add(0, 0, -dist)};
        int a = 0;
        for (Player alivePlayer : alivePlayers) {
            if (a == 4) a = 0;
            alivePlayer.teleport(locations[a]);
            a++;
        }
        BlitzSG.getInstance().getKarhuAnticheat().setEnabled(false);
        deadPlayers.forEach(player -> player.teleport(dm));
    }

    public void mobTeleport(Player p) {
        BlitzSGPlayer blitzSGPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        if (blitzSGPlayer.getGameEntities().size() >= 1) {
            List<Entity> removeList = new ArrayList<>();
            blitzSGPlayer.getGameEntities().forEach(entity -> {
                        if (entity.isDead())
                            removeList.add(entity);
                        if (p.getLocation().distance(entity.getLocation()) > 15) {
                            entity.teleport(p.getLocation());
                        }
                    }
            );
            blitzSGPlayer.getGameEntities().forEach(entity -> {

                for (Entity entityList : entity.getNearbyEntities(15, 15, 15))
                    if (entityList instanceof Player) {
                        Player potentialTarget = (Player) entityList;
                        if (!blitzSGPlayer.getGame().isDead(potentialTarget) && potentialTarget != p) {
                            if (entity instanceof Monster)
                                if (((Monster) entity).getTarget() == null)
                                    ((Monster) entity).setTarget(potentialTarget);
                                else if (entity instanceof Golem)
                                    if (((Golem) entity).getTarget() == null)
                                        ((Golem) entity).setTarget(potentialTarget);
                            continue;
                        }
                    }
            });
            removeList.forEach(entity -> blitzSGPlayer.getGameEntities().remove(entity));
        }
    }


    public void compassTarget(Player p) {
        Player result = null;
        double lastDistance = 500;

        for (Player pl : p.getWorld().getPlayers()) {
            if (pl == p || p.getGameMode() != org.bukkit.GameMode.SURVIVAL || p.isFlying())
                continue;
            double distance = pl.getLocation().distance(p.getLocation());
            if (distance < lastDistance) {
                lastDistance = distance;
                result = pl;
            }
        }
        if (result != null)
            p.setCompassTarget(result.getLocation());

    }

    public void killPlayer(Player p) {

        alivePlayers.remove(p);
        if (alivePlayers.size() <= 3) {
            isDeathmatchStarting = true;
            startDeathmatchCounter(gameTime);
        }
        msgAll(BlitzSG.CORE_NAME + "&eThere are &c" + alivePlayers.size() + " &eplayers remaning!");

        deadPlayers.add(p);
        //SpeedUHCPlayer uhcPlayer = SpeedUHC.getInstance().getSpeedUHCPlayerManager().getUhcPlayer(p.getUniqueId());
    }

    public void endGame(boolean draw) {
        if (gameMode == GameMode.RESETING)
            return;
        gameMode = GameMode.RESETING;
        try {

            msgAll("&7&m------------------------------");
            msgAll("                   &f&lBlitz SG     ");
            msgAll("&7");
            if (draw) {
                msgAll("                  &e&lDRAW! ");
            } else {
                winner = alivePlayers.get(0);

                BlitzSGPlayer winnerSGPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(winner.getUniqueId());
                winnerSGPlayer.addWin();
                msgAll("           &e&lWinner &7- " + winnerSGPlayer.getRank(true).getChatColor() + winner.getName());
            }
            msgAll("&7");

            if (BlitzSG.getInstance().getGameManager().getTopKillers(this).size() >= 1) {
                BlitzSGPlayer firstKiller = BlitzSG.getInstance().getGameManager().getTopKillers(this).get(1);
                msgAll("      &e&l1st Killer &7- " + firstKiller.getRank(true).getChatColor() + Bukkit.getOfflinePlayer(BlitzSG.getInstance().getGameManager().getTopKillers(this).get(1).getUuid()).getName() + " &7- "
                        + firstKiller.getGameKills());
            }
            if (BlitzSG.getInstance().getGameManager().getTopKillers(this).size() >= 2) {
                BlitzSGPlayer secondKiller = BlitzSG.getInstance().getGameManager().getTopKillers(this).get(2);
                msgAll("      &6&l2nd Killer &7- " + secondKiller.getRank(true).getChatColor() + Bukkit.getOfflinePlayer(BlitzSG.getInstance().getGameManager().getTopKillers(this).get(2).getUuid()).getName() + " &7- "
                        + secondKiller.getGameKills());
            }
            if (BlitzSG.getInstance().getGameManager().getTopKillers(this).size() >= 3) {
                BlitzSGPlayer thirdKiller = BlitzSG.getInstance().getGameManager().getTopKillers(this).get(3);

                msgAll("      &c&l3rd Killer &7- " + thirdKiller.getRank(true).getChatColor() + Bukkit.getOfflinePlayer(BlitzSG.getInstance().getGameManager().getTopKillers(this).get(3).getUuid()).getName() + " &7- "
                        + thirdKiller.getGameKills());
            }
            msgAll("&7&m------------------------------");


        } catch (Exception ignored) {
            msgAll("&7&m------------------------------");ignored.printStackTrace();
        }
        if (!draw) {
            BlitzSG.getInstance().getBlitzSGPlayerManager().handleWinElo(this);
            BlitzSGPlayer blitzSGWinner = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(winner.getUniqueId());

            int coins = 75 * blitzSGWinner.getRank().getMultiplier();
            blitzSGWinner.addCoins(coins);
            winner.sendMessage(ChatColor.GOLD + "+" + coins + " Coins (Win)");
        }
        Bukkit.getScheduler().runTaskAsynchronously(BlitzSG.getInstance(), () -> BlitzSG.getInstance().getStatisticsManager().save());


        new BukkitRunnable() {
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF("lobby");
                    onlinePlayer.sendPluginMessage(BlitzSG.getInstance(), "BungeeCord", out.toByteArray());
                }
            }
        }.runTaskLater(BlitzSG.getInstance(), 200);
        new BukkitRunnable() {
            public void run() {
                resetGame();
                Bukkit.getServer().shutdown();
            }
        }.runTaskLater(BlitzSG.getInstance(), 220);
        //  Bukkit.getScheduler().runTaskAsynchronously(BlitzSG.getInstance(), () -> BlitzSG.getInstance().getStatisticsManager().save());

    }

    public void resetGame() {
        this.startedCountdown = false;
        BlitzSG.getInstance().getGameManager().removeGame(this);
        for (Player p : allPlayers) {
            BlitzSGPlayer uhcPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
            if (uhcPlayer != null)
                uhcPlayer.setGame(null);
            resetPlayer(p);
            p.teleport(BlitzSG.lobbySpawn);

            BlitzSG.getInstance().getBlitzSGPlayerManager().setLobbyInventoryAndNameTag(p);
            //if (uhcPlayer != null)

            //    p.setPlayerListName(uhcPlayer.getRank(true).getPrefix() + p.getName() + BlitzSG.getInstance().getEloManager().getEloLevel(uhcPlayer.getElo()).getPrefix()
            //            + " [" + uhcPlayer.getElo() + "]");
        }
        allPlayers.clear();
        alivePlayers.clear();
        deadPlayers.clear();
        map.resetArena();
    }

    //private void createCage(Location location) {
    //	for(int x = -2; x <= 2; x = x +1) {
    //		for(int z = -2; z <= 2; z = z +1) {
    //			for(int y = 0; y <= 4; y = y +1) {
    //				Location loc = location.clone();
    //				loc.add(x, y, z).getBlock().setType(Material.GLASS);
    //			}
    //		}
    //	}
    //	for(int x = -1; x <= 1; x = x +1) {
    //		for(int z = -1; z <= 1; z = z +1) {
    //			for(int y = 1; y <= 3; y = y +1) {
    //				Location loc = location.clone();
    //				loc.add(x, y, z).getBlock().setType(Material.AIR);
    //			}
    //		}
    //	}
    //}
//
    //private void removeCage(Location location) {
    //	for(int x = -2; x <= 2; x = x +1) {
    //		for(int z = -2; z <= 2; z = z +1) {
    //			for(int y = 0; y <= 4; y = y +1) {
    //				Location loc = location.clone();
    //				loc.add(x, y, z).getBlock().setType(Material.AIR);
    //			}
    //		}
    //	}
    //}
    private void refillChests() {
        this.openedChests.clear();


        msgAll(BlitzSG.CORE_NAME + "&eAll chests have been refilled!");
    }

    private void setPregameInventory(Player p) {
        //p.getInventory().setItem(0, ItemUtils.buildItem(new ItemStack(Material.BOW), "&aKit Selector &7(Right Click)"
        //, Arrays.asList("&7Right Click to select a kit")));
        p.getInventory().setItem(0, ItemUtils.buildItem(new ItemStack(Material.BOW), "&aKit Selector &7(Right Click)"
                , Arrays.asList("&7Right Click to vote for the game settings")));
        //p.getInventory().setItem(8, ItemUtils.buildItem(new ItemStack(Material.BARRIER), "&cExit Game &7(Right Click)"
        //        , Arrays.asList("&7Right Click to leave the game")));
    }

    private void resetPlayer(Player p) {
        BlitzSGPlayer uhcPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        if (uhcPlayer == null)
            return;
        uhcPlayer.resetGameKills();
        uhcPlayer.setGameTaunt(0);
        ((CraftPlayer) p).getHandle().getDataWatcher().watch(9, (byte) 0);

        p.getInventory().clear();
        p.getInventory().setArmorContents((ItemStack[]) null);
        p.setFoodLevel(20);
        p.setSaturation(10);
        p.setMaxHealth(20);
        p.setHealth(20);
        p.setFireTicks(0);
        p.setExp(0);
        p.setLevel(0);
        p.setGameMode(org.bukkit.GameMode.SURVIVAL);
        for (PotionEffect pe : p.getActivePotionEffects())
            p.removePotionEffect(pe.getType());
    }

    public void msgAll(String msg) {
        for (Player p : allPlayers)
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public HashSet<Player> getAllPlayers() {
        return allPlayers;
    }

    public ArrayList<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public ArrayList<Player> getDeadPlayers() {
        return deadPlayers;
    }

    public boolean isHeadGame() {
        if (getTrueVotes() > getFalseVotes())
            return true;
        return false;
    }

    public HashMap<Player, Boolean> getVotes() {
        return votes;
    }

    public int getTrueVotes() {
        int v = 0;
        for (boolean b : votes.values())
            if (b)
                v++;
        return v;
    }

    public int getFalseVotes() {
        int v = 0;
        for (boolean b : votes.values())
            if (!b)
                v++;
        return v;
    }

    public int getVotingPercentage() {
        if (votes.size() == 0)
            return 0;
        return (int) ((double) getTrueVotes() * 100 / (double) votes.size());
    }

    public boolean getVote(Player p) {
        return this.votes.get(p);
    }

    public void setVote(Player p, boolean vote) {
        this.votes.put(p, vote);
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public NextEvent getNextEvent() {
        return nextEvent;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public boolean canFindStar() {
        return canFindStar;
    }

    public void setFindStar(boolean b) {
        canFindStar = false;
    }

    public Map getArena() {
        return map;
    }

    public boolean isDeathmatchStarting() {
        return isDeathmatchStarting;
    }

    public Player getWinner() {
        return winner;
    }

    public int getGameTime() {
        return gameTime;
    }

    public int getDeathmatchStartTime() {
        return deathmatchStartTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
    }

    public boolean isDead(Player p) {
        if (deadPlayers.contains(p))
            return true;
        return false;
    }

    public ArrayList<Location> getOpenedChests() {
        return openedChests;
    }

    public ArrayList<Location> getStarChests() {
        return starChests;
    }


}
