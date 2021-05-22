package me.hardstyles.blitz.party;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyChatCommand implements CommandExecutor {

    final private Core core;
    public PartyChatCommand(Core core){
        this.core = core;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Invalid message. /pc <message>");
            return true;
        }
        IPlayer sgPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());
        if (sgPlayer.getParty() == null) {
            p.sendMessage(ChatColor.RED + "You're not part of a party.");
            return true;
        }
        String format = ChatColor.BLUE + "Party > " + sgPlayer.getRank(true).getPrefix() + p.getName() + (sgPlayer.getRank(true).getPrefix().equalsIgnoreCase(ChatColor.GRAY + "") ? ChatColor.GRAY + ": " : ChatColor.WHITE + ": ") + joined(args).replaceAll("%", "%%");
        OfflinePlayer memberPlayer;
        for (UUID member : sgPlayer.getParty().getMembers()) {
            memberPlayer = Bukkit.getOfflinePlayer(member);
            if (memberPlayer.isOnline()) {
                memberPlayer.getPlayer().sendMessage(format);
            }
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