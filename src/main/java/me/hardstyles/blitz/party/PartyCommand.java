package me.hardstyles.blitz.party;

import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyCommand implements CommandExecutor {

    public static HashMap<UUID, Long> cooldown = new HashMap<>();

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(ChatColor.GREEN + "/party <player>");
            p.sendMessage(ChatColor.GREEN + "/party kick/remove <player>");
            p.sendMessage(ChatColor.GREEN + "/party invite <player>");
            p.sendMessage(ChatColor.GREEN + "/party disband");
            p.sendMessage(ChatColor.GREEN + "/party transfer <player>");
            return true;
        }
        BlitzSGPlayer sgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        if (args[0].equalsIgnoreCase("disband")) {
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.RED + "You're not part of a party.");
                return true;
            }
            if (sgPlayer.getParty().getOwner() != p) {
                p.sendMessage(ChatColor.RED + "You're not the owner of the party.");
                return true;
            }

            for (Player member : sgPlayer.getParty().getMembers()) {
                member.sendMessage(ChatColor.YELLOW + "The party you were in was disbanded");
                BlitzSGPlayer sgMember = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(member.getUniqueId());
                sgMember.setParty(null);
            }

            return true;
        }
        if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("remove")) {
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.RED + "You're not part of a party.");
                return true;
            }
            if (sgPlayer.getParty().getOwner() != p) {
                p.sendMessage(ChatColor.RED + "You're not the owner of this party.");
                return true;
            }
            if (args.length == 1) {
                p.sendMessage(ChatColor.GREEN + "/party kick <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !sgPlayer.getParty().has(target)) {
                p.sendMessage(ChatColor.RED + "That player is not in your party.");
                return true;
            }
            target.sendMessage(ChatColor.YELLOW + "You were kicked from the party.");
            BlitzSGPlayer sgTarget = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(target.getUniqueId());
            sgTarget.setParty(null);
            sgPlayer.getParty().removeMember(target);
            for (Player member : sgPlayer.getParty().getMembers()) {
                if (member != sgPlayer.getParty().getOwner()) {
                    p.sendMessage(ChatColor.YELLOW + "" + target.getDisplayName() + " was kicked from the party");
                }
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.RED + "You're not part of a party.");
                return true;
            }
            p.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------");
            p.sendMessage(ChatColor.YELLOW + "Owner - " + sgPlayer.getParty().getOwner().getDisplayName());
            for (Player member : sgPlayer.getParty().getMembers()) {
                if (member != sgPlayer.getParty().getOwner()) {
                    p.sendMessage(ChatColor.YELLOW + "Member - " + member.getDisplayName());
                }
            }
            p.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------");
            return true;
        }
        if (args[0].equalsIgnoreCase("leave")) {
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.RED + "You're not in a party");
                return true;
            }
            if (sgPlayer.getParty().getOwner() == p) {
                p.chat("/p disband");
                return true;
            }
            Party party = sgPlayer.getParty();
            party.removeMember(p);
            sgPlayer.setParty(null);
            for (Player member : party.getMembers()) {
                member.sendMessage(ChatColor.YELLOW + "" + p.getName() + " left the party.");
            }
            p.sendMessage(ChatColor.YELLOW + "You've left the party.");
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (sgPlayer.getParty() != null) {
                p.sendMessage(ChatColor.RED + "You're are already in a party");
                return true;
            }
            Party party = new Party(p);
            party.addMember(p);
            sgPlayer.setParty(party);
            p.sendMessage(ChatColor.GREEN + "You've created a party.");
            return true;
        }
        if (args.length > 0) {
            if (sgPlayer.getParty() == null) {
                p.chat("/p create");
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(ChatColor.RED + "Couldn't find that player.");
                return true;
            }
            if (sgPlayer.getParty().has(target)) {
                p.sendMessage(ChatColor.RED + "That player is already in your party.");
                return true;
            }
            target.sendMessage(ChatColor.YELLOW + "You've been added to " + p.getName() + "'s party");
            p.sendMessage(ChatColor.YELLOW + "You've added " + target.getName() + " to the party!");
            BlitzSGPlayer sgTarget = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(target.getUniqueId());
            sgTarget.setParty(sgPlayer.getParty());
            sgPlayer.getParty().addMember(target);
        }


        return true;
    }


}