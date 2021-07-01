package me.hardstyles.blitz.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Command extends org.bukkit.command.Command {
    private static final List<String> registeredCommands = new ArrayList<>();
    private final int position;
    private final String name;
    public final Core core = Core.i();
    private final List<String> aliases;

    public Command(String name) {
        this(name, Lists.newArrayList(), 0);
    }

    public Command(String name, List<String> aliases, int position) {
        super(name);
        setAliases(aliases);

        this.name = name;
        this.aliases = aliases;
        this.position = position;

        try {
            CommandMap map = (CommandMap) ReflectionUtil.getField(Bukkit.getServer().getClass(), "commandMap").get(Bukkit.getServer());
            ReflectionUtil.unregisterCommands(map, getName());
            ReflectionUtil.unregisterCommands(map, getAliases());
            map.register(getName(), "core", this);
            registeredCommands.add(name);
            registeredCommands.addAll(aliases);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            onConsole(sender, args);
            return true;
        }
        Player p = ((Player) sender);
        IPlayer iPlayer = Core.i().getPlayerManager().getPlayer(p.getUniqueId());


        if (iPlayer == null || iPlayer.getRank().getPosition() < position) {
            p.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        try {
            onExecute(p, iPlayer, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            IPlayer iPlayer = Core.i().getPlayerManager().getPlayer(p.getUniqueId());

            if (iPlayer == null || iPlayer.getRank().getPosition() < position) {
                return ImmutableList.of();
            }

            List<String> tabCompletion = onTabComplete(p, args);
            if (tabCompletion == null) {
                List<String> list = Lists.newArrayList();
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (StringUtil.startsWithIgnoreCase(all.getName(), args[0]) && p.canSee(all)) {
                        list.add(all.getName());
                    }
                }
                return list;
            }

            return tabCompletion;

        } else {
            return ImmutableList.of();
        }
    }

    public void onConsole(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
    }

    public static List<String> getRegisteredCommands() {
        return registeredCommands;
    }

    public abstract List<String> onTabComplete(Player player, String[] args);

    public abstract void onExecute(Player p, IPlayer iPlayer, String[] args);

}

