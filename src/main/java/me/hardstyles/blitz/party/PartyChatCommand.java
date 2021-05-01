package me.hardstyles.blitz.party;

import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyChatCommand implements CommandExecutor {



    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player p = (Player) sender;
        if(args.length == 0){
            p.sendMessage(ChatColor.RED + "Invalid message. /pc <message>");
            return true;
        }
        BlitzSGPlayer blitzSGPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        if(blitzSGPlayer.getParty() == null){
            p.sendMessage(ChatColor.RED + "You're not part of a party.");
            return true;
        }
        for (Player member : blitzSGPlayer.getParty().getMembers()) {
            member.sendMessage(ChatColor.BLUE + "Party > " + blitzSGPlayer.getRank(true).getPrefix() + p.getName() + (blitzSGPlayer.getRank(true).getPrefix().equalsIgnoreCase(ChatColor.GRAY + "") ? ChatColor.GRAY + ": " : ChatColor.WHITE + ": ") + joined(args).replaceAll("%", "%%"));
        }
        return true;
    }


    private String joined(String[] args) {
        String a = "";
        for (String part : args) {
            if (a != "") a += " ";
            a += part;
        }
        return a;
    }
    }