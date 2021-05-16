package me.hardstyles.blitz;

import com.zaxxer.hikari.HikariDataSource;
import me.hardstyles.blitz.arena.ArenaManager;
import me.hardstyles.blitz.arena.TestCommand;
import me.hardstyles.blitz.match.MatchHandler;
import me.hardstyles.blitz.nickname.NicknameCommand;
import me.hardstyles.blitz.party.PartyChatCommand;
import me.hardstyles.blitz.party.PartyCommand;
import me.hardstyles.blitz.player.IPlayerHandler;
import me.hardstyles.blitz.player.IPlayerManager;
import me.hardstyles.blitz.punishments.ACBan;
import me.hardstyles.blitz.punishments.PunishmentManager;
import me.hardstyles.blitz.punishments.commands.Unban;
import me.hardstyles.blitz.queue.QueueCommand;
import me.hardstyles.blitz.queue.QueueManager;
import me.hardstyles.blitz.rank.RankCommand;
import me.hardstyles.blitz.rank.RankManager;
import me.hardstyles.blitz.scoreboard.ScoreboardManager;
import me.hardstyles.blitz.statistics.StatisticsManager;
import me.hardstyles.blitz.utils.EnchantListener;
import me.hardstyles.blitz.utils.FireworkCommand;
import me.hardstyles.blitz.utils.KarhuAnticheat;
import me.hardstyles.blitz.utils.VanillaCommands;
import me.hardstyles.blitz.utils.database.Database;
import me.hardstyles.blitz.utils.nametag.NametagManager;
import me.hardstyles.blitz.utils.world.VoidGenerator;
import me.hardstyles.blitz.utils.world.WorldCommand;
import me.liwk.karhu.api.KarhuAPI;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.io.File;

public class Core extends JavaPlugin {

    public static String CORE_NAME = EnumChatFormat.GRAY + "[" + EnumChatFormat.RED + "B-SG" + EnumChatFormat.GRAY + "]: " + EnumChatFormat.WHITE;

    private JedisPool pool;

    public static Core instance;
    private KarhuAnticheat karhuAnticheat;
    private NametagManager nametagManager;

    private IPlayerManager iPlayerManager;

    private ScoreboardManager scoreboardManager;
    private RankManager rankManager;

    private PunishmentManager punishmentManager;
    private StatisticsManager statisticsManager;
    private QueueManager queueManager;

    private HikariDataSource hikari;
    private  Location lobbySpawn;
    private ArenaManager arenaManager;

    private Database database;

    public Core() {
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
        iPlayerManager = new IPlayerManager();
        statisticsManager = new StatisticsManager(this);
        rankManager = new RankManager();
        queueManager = new QueueManager(this);

        // scoreboardManager = new ScoreboardManager();

        nametagManager = new NametagManager();
        punishmentManager = new PunishmentManager();
        arenaManager = new ArenaManager(this);

        //Register Commands::

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


        // this.getCommand("world").setExecutor(new WorldCommand());

        this.getCommand("fw").setExecutor(new FireworkCommand());
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("acban").setExecutor(new ACBan());
        this.getCommand("unban").setExecutor(new Unban());
        this.getCommand("partychat").setExecutor(new PartyChatCommand());
        this.getCommand("party").setExecutor(new PartyCommand(this));
        this.getCommand("rank").setExecutor(new RankCommand(this));
        this.getCommand("nick").setExecutor(new NicknameCommand(this));
        this.getCommand("queue").setExecutor(new QueueCommand(this));
        this.getCommand("world").setExecutor(new WorldCommand(this));

        //Register Handlers:
        getServer().getPluginManager().registerEvents(new MatchHandler(this), this);
        getServer().getPluginManager().registerEvents(new IPlayerHandler(this), this);
        getServer().getPluginManager().registerEvents(new EnchantListener(this), this);
        // getServer().getPluginManager().registerEvents(scoreboardManager.getScoreboardHandler(), this);


        getServer().setWhitelist(false);

        KarhuAPI.getEventRegistry().addListener(karhuAnticheat);


        World world = Bukkit.getWorld("world");
        File playerdataFolder = new File(world.getWorldFolder() + "/playerdata/");
        File[] contents = playerdataFolder.listFiles();
        if (contents != null) {
            for (File content : contents) {
                content.delete();
            }
        }
        new WorldCreator("arena").generator(new VoidGenerator()).createWorld();
        //Load Players:
        if (!Bukkit.getOnlinePlayers().isEmpty()) {

            Bukkit.getOnlinePlayers().forEach(player -> {
                //g/etServer().getPluginManager().callEvent(new PlayerLoginEvent(player));
                statisticsManager.load(player.getUniqueId());
                iPlayerManager.addBsgPlayer(player.getUniqueId(), getPlayerManager().getPlayer(player.getUniqueId()));
                //
            });
        }
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


        // scoreboardManager.runTaskTimer(this, 20, 20);

        //Load LobbySpawn:

        lobbySpawn = new Location(Bukkit.getWorld("world"), 0.5, 50.5, 0.5, 90, 0);
        nametagManager.update();


    }

    public void onDisable() {


    }

    public KarhuAnticheat getKarhuAnticheat() {
        return karhuAnticheat;
    }

    public Database getData() {
        return database;
    }


    public IPlayerManager getPlayerManager() {
        return iPlayerManager;
    }


    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }


    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
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

    public static Core getInstance() {
        return instance;
    }

    public QueueManager getQueueManager() {
        return this.queueManager;
    }

    public ArenaManager getArenaManager() {
        return this.arenaManager;
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
    public Location getLobbySpawn(){return lobbySpawn;}

}
