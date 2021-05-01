package me.hardstyles.blitz;

import com.zaxxer.hikari.HikariDataSource;
import me.hardstyles.blitz.arena.TestCommand;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayerHandler;
import me.hardstyles.blitz.elo.EloManager;
import me.hardstyles.blitz.gui.GUIManager;
import me.hardstyles.blitz.kit.KitManager;
import me.hardstyles.blitz.map.MapHandler;
import me.hardstyles.blitz.map.MapManager;
import me.hardstyles.blitz.party.PartyChatCommand;
import me.hardstyles.blitz.party.PartyCommand;
import me.hardstyles.blitz.utils.*;
import me.liwk.karhu.api.KarhuAPI;
import me.hardstyles.blitz.statistics.StatisticsManager;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayerManager;
import me.hardstyles.blitz.cosmetic.CosmeticsManager;
import me.hardstyles.blitz.game.Game;
import me.hardstyles.blitz.game.GameHandler;
import me.hardstyles.blitz.game.GameManager;
import me.hardstyles.blitz.game.GameMobHandler;
import me.hardstyles.blitz.gamestar.StarManager;
import me.hardstyles.blitz.gui.InventoryHandler;
import me.hardstyles.blitz.punishments.ACBan;
import me.hardstyles.blitz.punishments.PunishmentManager;
import me.hardstyles.blitz.punishments.commands.Unban;
import me.hardstyles.blitz.rank.RankManager;
import me.hardstyles.blitz.scoreboard.ScoreboardManager;

import me.hardstyles.blitz.utils.bungee.BungeeMessaging;
import me.hardstyles.blitz.utils.database.Database;
import me.hardstyles.blitz.utils.nametag.NametagManager;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.io.File;

public class BlitzSG extends JavaPlugin {

    public static String CORE_NAME = EnumChatFormat.GRAY + "[" + EnumChatFormat.RED + "B-SG" + EnumChatFormat.GRAY + "]: " + EnumChatFormat.WHITE;

    private JedisPool pool;

    public static BlitzSG instance;
    private KarhuAnticheat karhuAnticheat;
    private NametagManager nametagManager;
    private MapManager mapManager;
    private BlitzSGPlayerManager blitzSGPlayerManager;
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    private RankManager rankManager;
    private EloManager eloManager;
    private GUIManager guiManager;
    private KitManager kitManager;
    private StarManager starManager;
    private PunishmentManager punishmentManager;
    private StatisticsManager statisticsManager;

    private HikariDataSource hikari;
    public static Location lobbySpawn;

    private Database database;
    private CosmeticsManager cosmeticsManager;

    public BlitzSG() {
        instance = this;
    }

    public void onEnable() {
        try {
            new VanillaCommands().remove();
        } catch (Exception exception) {
            exception.printStackTrace();
        }



        karhuAnticheat = new KarhuAnticheat();
        database = new Database();
        blitzSGPlayerManager = new BlitzSGPlayerManager();
        statisticsManager = new StatisticsManager();
        rankManager = new RankManager();
        kitManager = new KitManager();
        mapManager = new MapManager();

        gameManager = new GameManager();
        scoreboardManager = new ScoreboardManager();
        eloManager = new EloManager();
        guiManager = new GUIManager();
        starManager = new StarManager();


        nametagManager = new NametagManager();
        punishmentManager = new PunishmentManager();
        cosmeticsManager = new CosmeticsManager();
        cosmeticsManager.initializeAuras();
        //Register Commands::

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeMessaging());


        // this.getCommand("world").setExecutor(new WorldCommand());

        this.getCommand("fw").setExecutor(new FireworkCommand());
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("acban").setExecutor(new ACBan());
        this.getCommand("unban").setExecutor(new Unban());
        this.getCommand("taunt").setExecutor(new GameHandler());
        this.getCommand("wtfmap").setExecutor(new WTFMapCommand());
        this.getCommand("startdm").setExecutor(new StartDMCommand());
        this.getCommand("partychat").setExecutor(new PartyChatCommand());
        this.getCommand("party").setExecutor(new PartyCommand());

        //Register Handlers:
        getServer().getPluginManager().registerEvents(new MapHandler(), this);
        getServer().getPluginManager().registerEvents(new GameHandler(), this);
        getServer().getPluginManager().registerEvents(new GameMobHandler(), this);
        getServer().getPluginManager().registerEvents(new BlitzSGPlayerHandler(), this);
        getServer().getPluginManager().registerEvents(new EnchantListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryHandler(), this);
        getServer().getPluginManager().registerEvents(scoreboardManager.getScoreboardHandler(), this);

        getServer().setWhitelist(false);

        KarhuAPI.getEventRegistry().addListener(karhuAnticheat);



        World world =  Bukkit.getWorld("world");
        File playerdataFolder = new File(world.getWorldFolder() + "/playerdata/");
        File[] contents = playerdataFolder.listFiles();
        if(contents != null){
            for (File content : contents) {
                content.delete();
            }
        }

        //Load Players:
        //PlayerUtils.loadPlayerData();
        //new LoadStats().load();
        //statisticsManager.load();
        // System.out.println("looaded dataaa");
   //     for (Player p : getServer().getOnlinePlayers()) {
   //         statisticsManager.load(p.getUniqueId());
   //         BlitzSGPlayer bsgPlayer = blitzSGPlayerManager.getBsgPlayer(p.getUniqueId());
   //         blitzSGPlayerManager.addBsgPlayer(p.getUniqueId(), bsgPlayer);
   //         System.out.println(bsgPlayer);
   //         bsgPlayer.setName(p.getDisplayName());
   //         bsgPlayer.setIp(p.getAddress().toString().split(":")[0].replaceAll("/", ""));
   //          p.setPlayerListName(bsgPlayer.getRank(true).getPrefix() + p.getName() + BlitzSG.getInstance().getEloManager().getEloLevel(bsgPlayer.getElo()).getPrefix() + " [" + bsgPlayer.getElo() + "]");
   //     }


       // mapManager.loadArena(mapManager.getRandom());

        //Load Arena:
        //ArenaUtils.loadArenas();
        // arenaManager.loadArena("aelinstower");

        //Start Scoreboard:


        scoreboardManager.runTaskTimer(this, 20, 20);

        //Load LobbySpawn:

        lobbySpawn = new Location(Bukkit.getWorld("world"), 0.5, 50.5, 0.5, 90, 0);
        nametagManager.update();


    }

    public void onDisable() {

        for (Game g : gameManager.getRunningGames()) {

            g.resetGame();
            mapManager.removeArena(g.getArena());

        }
    }

    public KarhuAnticheat getKarhuAnticheat(){return karhuAnticheat;}
    public Database getData() {
        return database;
    }

    public MapManager getArenaManager() {
        return mapManager;
    }

    public BlitzSGPlayerManager getBlitzSGPlayerManager() {
        return blitzSGPlayerManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public EloManager getEloManager() {
        return eloManager;
    }

    public CosmeticsManager getCosmeticsManager() {
        return cosmeticsManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public StarManager getStarManager() {
        return starManager;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public JedisPool getJedisPool() {
        return pool;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public NametagManager getNametagManager() {
        return nametagManager;
    }

    public static BlitzSG getInstance() {
        return instance;
    }


    public static void broadcast(String message, World world) {
        if (world == null)
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        else
            world.getPlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static void broadcast(String message) {
        broadcast(message, null);
    }

    public static void send(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
