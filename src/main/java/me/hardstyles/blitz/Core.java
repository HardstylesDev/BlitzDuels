package me.hardstyles.blitz;
 // MAKE SURE THE DATABASE IS LOADED BEFOREHAND
import lombok.Getter;
import lombok.Setter;
import me.elijuh.nametagapi.NametagAPI;
import me.hardstyles.blitz.arena.ArenaManager;
import me.hardstyles.blitz.arena.TestCommand;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.commands.impl.*;
import me.hardstyles.blitz.duels.DuelCommand;
import me.hardstyles.blitz.duels.DuelManager;
import me.hardstyles.blitz.kits.gui.LayoutGui;
import me.hardstyles.blitz.kits.gui.SlotGui;
import me.hardstyles.blitz.leaderboard.LeaderboardLoaderKills;
import me.hardstyles.blitz.leaderboard.LeaderboardLoaderStreak;
import me.hardstyles.blitz.leaderboard.LeaderboardLoaderWins;
import me.hardstyles.blitz.leaderboard.LeaderboardUpdater;
import me.hardstyles.blitz.match.MatchHandler;
import me.hardstyles.blitz.match.MatchManager;
import me.hardstyles.blitz.match.mobs.MatchMobHandler;
import me.hardstyles.blitz.nickname.Nickname;
import me.hardstyles.blitz.nickname.NicknameCommand;
import me.hardstyles.blitz.party.PartyChatCommand;
import me.hardstyles.blitz.party.PartyCommand;
import me.hardstyles.blitz.player.IPlayerHandler;
import me.hardstyles.blitz.player.IPlayerManager;
import me.hardstyles.blitz.punishments.PunishmentHandler;
import me.hardstyles.blitz.punishments.PunishmentManager;
import me.hardstyles.blitz.punishments.commands.*;
import me.hardstyles.blitz.punishments.redis.RedisManager;
import me.hardstyles.blitz.queue.QueueCommand;
import me.hardstyles.blitz.queue.QueueGui;
import me.hardstyles.blitz.queue.QueueManager;
import me.hardstyles.blitz.rank.RankCommand;
import me.hardstyles.blitz.rank.RankManager;
import me.hardstyles.blitz.scoreboard.ScoreboardManager;
import me.hardstyles.blitz.staff.FollowCommand;
import me.hardstyles.blitz.staff.StaffChatCommand;
import me.hardstyles.blitz.staff.StaffHandler;
import me.hardstyles.blitz.staff.StaffManager;
import me.hardstyles.blitz.staff.report.ReportCommand;
import me.hardstyles.blitz.staff.report.ReportsCommand;
import me.hardstyles.blitz.statistics.StatisticsManager;
import me.hardstyles.blitz.utils.ChestFiller;
import me.hardstyles.blitz.utils.EnchantListener;
import me.hardstyles.blitz.utils.ReflectionUtil;
import me.hardstyles.blitz.utils.database.Database;
import me.hardstyles.blitz.utils.entity.player.TabUtil;
import me.hardstyles.blitz.utils.world.VoidGenerator;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import org.bukkit.*;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


@Getter
public class Core extends JavaPlugin {

    public static String CORE_NAME = EnumChatFormat.GRAY + "[" + EnumChatFormat.RED + "B-SG" + EnumChatFormat.GRAY + "]: " + EnumChatFormat.WHITE;


    private static Core instance;
    @Setter
    private boolean disableQueues;
    private ChestFiller chestFiller;
    private MatchManager matchManager;
    private LeaderboardLoaderWins leaderboardLoaderWins;
    private LeaderboardLoaderKills leaderboardLoaderKills;
    private LeaderboardLoaderStreak leaderboardLoaderStreak;
    private LeaderboardUpdater leaderboardUpdater;

    private DuelManager duelManager;
    private IPlayerManager playerManager;
    private ScoreboardManager scoreboardManager;
    private RankManager rankManager;
    private TabUtil tabUtil;

    private Database data;
    private RedisManager redisManager;

    private PunishmentManager punishmentManager;
    private StatisticsManager statisticsManager;
    private QueueManager queueManager;
    private QueueGui queueGui;
    private LayoutGui layoutGui;
    private SlotGui slotGui;

    private StaffManager staffManager;
    private Location lobbySpawn;
    private ArenaManager arenaManager;

    public Core() {
        instance = this;
    }

    public void onEnable() {

        instance = this;

        

        new NametagAPI(this);
        new WorldCreator("arena").generator(new VoidGenerator()).createWorld();

        chestFiller = new ChestFiller();
        data = new Database();
        redisManager = new RedisManager();


        playerManager = new IPlayerManager(this);
        statisticsManager = new StatisticsManager(this);
        rankManager = new RankManager();
        queueManager = new QueueManager(this);
        matchManager = new MatchManager(this);
        queueGui = new QueueGui(this);
        layoutGui = new LayoutGui(this);
        scoreboardManager = new ScoreboardManager(this);
        punishmentManager = new PunishmentManager(this);
        arenaManager = new ArenaManager(this);

        leaderboardUpdater = new LeaderboardUpdater(this);
        leaderboardLoaderWins = new LeaderboardLoaderWins(this);
        leaderboardLoaderKills = new LeaderboardLoaderKills(this);
        leaderboardLoaderStreak = new LeaderboardLoaderStreak(this);

        duelManager = new DuelManager(this);

        tabUtil = new TabUtil(this);
        slotGui = new SlotGui(this);
        staffManager = new StaffManager();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


        // this.getCommand("world").setExecutor(new WorldCommand());

        this.getCommand("fw").setExecutor(new FireworkCommand());
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("nick").setExecutor(new NicknameCommand(this));
        this.getCommand("world").setExecutor(new WorldCommand(this));
        this.getCommand("rename").setExecutor(new RenameCommand(this));

        // this.getCommand("duel").setExecutor(new DuelCommand(this));

        //temporary
        new MassResetLayoutsCommand();

        new QueueCommand();
        new StaffChatCommand();
        new ReportCommand();
        new ReportsCommand();
        new FollowCommand();
        new PartyCommand();
        new DuelCommand();
        new HubCommand();
        new RankCommand();
        new PartyChatCommand();
        new MessageCommand();
        new SpectatorCommand();
        new IgnoreCommand();

        //punishments
        new AltsCommand();
        new BanCommand();
        new HistoryCommand();
        new IPBanCommand();
        new IPUnBanCommand();
        new KickCommand();
        new MuteCommand();
        new TempBanCommand();
        new TempMuteCommand();
        new UnBanCommand();
        new UnMuteCommand();

        //Register Handlers:
        getServer().getPluginManager().registerEvents(new MatchHandler(this), this);
        getServer().getPluginManager().registerEvents(new MatchMobHandler(this), this);
        getServer().getPluginManager().registerEvents(new IPlayerHandler(this), this);
        getServer().getPluginManager().registerEvents(new EnchantListener(this), this);
        getServer().getPluginManager().registerEvents(new PunishmentHandler(this), this);
        getServer().getPluginManager().registerEvents(new StaffHandler(this), this);

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

        lobbySpawn = new Location(Bukkit.getWorld("world"), 0.5, 80, 0.5, 180, 0);


        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(player -> {

                statisticsManager.load(player.getUniqueId());
                playerManager.addPlayer(player.getUniqueId(), getPlayerManager().getPlayer(player.getUniqueId()));
                playerManager.hub(player);
                NametagAPI.setNametag(player.getName(), playerManager.getPlayer(player.getUniqueId()).getRank().getPrefix(), "", playerManager.getPlayer(player.getUniqueId()).getRank().getPosition());
            });
        }

        scoreboardManager.runTaskTimer(this, 20, 20);
    }

    public void onDisable() {
        try {
            CommandMap map = (CommandMap) ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap").get(Bukkit.getServer());
            ReflectionUtil.unregisterCommands(map, Command.getRegisteredCommands());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        statisticsManager.saveAll();
        data.getDataSource().close();
        
        redisManager.shutdown();
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

    public static Core i() {
        return instance;
    }


}
