package me.hardstyles.blitz;

import me.hardstyles.blitz.arena.ArenaManager;
import me.hardstyles.blitz.arena.TestCommand;
import me.hardstyles.blitz.kits.IItemManager;
import me.hardstyles.blitz.kits.gui.LayoutGui;
import me.hardstyles.blitz.kits.gui.SlotGui;
import me.hardstyles.blitz.leaderboard.LeaderboardLoader;
import me.hardstyles.blitz.leaderboard.LeaderboardUpdater;
import me.hardstyles.blitz.match.MatchHandler;
import me.hardstyles.blitz.match.MatchManager;
import me.hardstyles.blitz.match.mobs.MatchMobHandler;
import me.hardstyles.blitz.nickname.NicknameCommand;
import me.hardstyles.blitz.party.PartyChatCommand;
import me.hardstyles.blitz.party.PartyCommand;
import me.hardstyles.blitz.player.IPlayerHandler;
import me.hardstyles.blitz.player.IPlayerManager;
import me.hardstyles.blitz.punishments.ACBan;
import me.hardstyles.blitz.punishments.PunishmentManager;
import me.hardstyles.blitz.punishments.commands.Unban;
import me.hardstyles.blitz.queue.QueueCommand;
import me.hardstyles.blitz.queue.QueueGui;
import me.hardstyles.blitz.queue.QueueManager;
import me.hardstyles.blitz.rank.RankCommand;
import me.hardstyles.blitz.rank.RankManager;
import me.hardstyles.blitz.scoreboard.ScoreboardManager;
import me.hardstyles.blitz.statistics.StatisticsManager;
import me.hardstyles.blitz.utils.*;
import me.hardstyles.blitz.utils.database.Database;
import me.hardstyles.blitz.utils.database.ItemSerializer;
import me.hardstyles.blitz.utils.entity.player.TabUtil;
import me.hardstyles.blitz.utils.nametag.NametagManager;
import me.hardstyles.blitz.utils.world.VoidGenerator;
import me.hardstyles.blitz.utils.world.WorldCommand;
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
    private ChestFiller chestFiller;
    private MatchManager matchManager;
    private LeaderboardLoader leaderboardLoader;
    private LeaderboardUpdater leaderboardUpdater;

    private IPlayerManager iPlayerManager;
    private ScoreboardManager scoreboardManager;
    private RankManager rankManager;
    private TabUtil tabUtil;
    private ItemSerializer itemSerializer;

    private PunishmentManager punishmentManager;
    private StatisticsManager statisticsManager;
    private QueueManager queueManager;
    private QueueGui queueGui;
    private LayoutGui layoutGui;
    private SlotGui slotGui;
    private IItemManager IItemManager;

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


        karhuAnticheat = new KarhuAnticheat(this);
        System.out.println("d");
        chestFiller = new ChestFiller(this);
        database = new Database();
        iPlayerManager = new IPlayerManager(this);
        statisticsManager = new StatisticsManager(this);
        rankManager = new RankManager();
        queueManager = new QueueManager(this);
        matchManager = new MatchManager(this);
        IItemManager = new IItemManager(this);
        queueGui = new QueueGui(this);
        layoutGui = new LayoutGui(this);
         scoreboardManager = new ScoreboardManager(this);

        nametagManager = new NametagManager();
        punishmentManager = new PunishmentManager(this);
        arenaManager = new ArenaManager(this);

        leaderboardUpdater = new LeaderboardUpdater(this);
        leaderboardLoader =new LeaderboardLoader(this);
        //Register Commands::
        tabUtil = new TabUtil(this);
        itemSerializer = new ItemSerializer(this);
        slotGui = new SlotGui(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


        // this.getCommand("world").setExecutor(new WorldCommand());

        this.getCommand("fw").setExecutor(new FireworkCommand());
        this.getCommand("l").setExecutor(new HubCommand(this));
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("acban").setExecutor(new ACBan(this));
        this.getCommand("unban").setExecutor(new Unban());
        this.getCommand("partychat").setExecutor(new PartyChatCommand(this));
        this.getCommand("party").setExecutor(new PartyCommand(this));
        this.getCommand("rank").setExecutor(new RankCommand(this));
        this.getCommand("nick").setExecutor(new NicknameCommand(this));
        this.getCommand("queue").setExecutor(new QueueCommand(this));
        this.getCommand("world").setExecutor(new WorldCommand(this));
        this.getCommand("rename").setExecutor(new RenameCommand(this));

        //Register Handlers:
        getServer().getPluginManager().registerEvents(new MatchHandler(this), this);
        getServer().getPluginManager().registerEvents(new MatchMobHandler(this), this);
        getServer().getPluginManager().registerEvents(new IPlayerHandler(this), this);
        getServer().getPluginManager().registerEvents(new EnchantListener(this), this);

        getServer().getPluginManager().registerEvents(queueGui, this);
        getServer().getPluginManager().registerEvents(layoutGui, this);
        getServer().getPluginManager().registerEvents(slotGui, this);
        getServer().getPluginManager().registerEvents(scoreboardManager.getScoreboardHandler(), this);



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


        scoreboardManager.runTaskTimer(this, 20, 20);


        lobbySpawn =new Location(Bukkit.getWorld("world"), 0.5, 80, 0.5, 180, 0);
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

    public ChestFiller getChestFiller(){return chestFiller;}

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

    public QueueGui getQueueGui(){
        return queueGui;
    }

    public LayoutGui getKitGui() {
        return layoutGui;
    }

    public MatchManager getMatchManager(){
        return matchManager;
    }
    public LeaderboardUpdater getLeaderboardUpdater(){
        return leaderboardUpdater;
    }
    public LeaderboardLoader getLeaderboardLoader(){
        return leaderboardLoader;
    }

    public IItemManager getItemHandler() {
        return IItemManager;
    }

    public TabUtil getTabUtil() {
        return tabUtil;
    }

    public ItemSerializer getItemSerializer() {
        return itemSerializer;
    }

    public SlotGui getSlotGui() {
        return slotGui;
    }
}
