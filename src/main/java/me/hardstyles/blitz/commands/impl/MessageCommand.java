package me.hardstyles.blitz.commands.impl;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageCommand extends Command {
    public MessageCommand() {
        super("message", ImmutableList.of(""));
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {

    }
}
