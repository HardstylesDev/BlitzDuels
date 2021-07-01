package me.hardstyles.blitz.party;

import com.google.common.collect.ImmutableList;

import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PartyChatCommand extends Command {


    public PartyChatCommand(){
        super("pc", ImmutableList.of("partychat"), 0);
    }



    private String joined(String[] args) {
        StringBuilder a = new StringBuilder();
        for (String part : args) {
            if (!a.toString().equalsIgnoreCase("")) a.append(" ");
            a.append(part);
        }
        return a.toString();
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Invalid message. /pc <message>");
            return;
        }
        IPlayer sgPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());
        if (sgPlayer.getParty() == null) {
            p.sendMessage(ChatColor.RED + "You're not part of a party.");
            return;
        }
        String format = ChatColor.BLUE + "Party > " + sgPlayer.getRank().getPrefix() + p.getName() + (sgPlayer.getRank().getPrefix().equalsIgnoreCase(ChatColor.GRAY + "") ? ChatColor.GRAY + ": " : ChatColor.WHITE + ": ") + joined(args).replaceAll("%", "%%");
        OfflinePlayer memberPlayer;
        for (UUID member : sgPlayer.getParty().getMembers()) {
            memberPlayer = Bukkit.getOfflinePlayer(member);
            if (memberPlayer.isOnline()) {
                memberPlayer.getPlayer().sendMessage(format);
            }
        }
    }
}