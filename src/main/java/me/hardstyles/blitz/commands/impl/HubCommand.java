package me.hardstyles.blitz.commands.impl;

import com.google.common.collect.ImmutableList;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


public class HubCommand extends Command {



    public HubCommand() {
        super("hub", ImmutableList.of("lobby", "spawn", "exit", "leave", "l"), 0);

    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        core.getPlayerManager().hub(p);
    }


}

