package me.hardstyles.blitz.party;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyCommand implements CommandExecutor {

    public static HashMap<UUID, Long> cooldown = new HashMap<>();
    final private Core core;

    public PartyCommand(Core core) {
        this.core = core;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(ChatColor.GREEN + "/party <player>");
            p.sendMessage(ChatColor.GREEN + "/party kick/remove <player>");
            p.sendMessage(ChatColor.GREEN + "/party invite <player>");
            p.sendMessage(ChatColor.GREEN + "/party disband");
            p.sendMessage(ChatColor.GREEN + "/party transfer <player>");
            p.sendMessage(ChatColor.GREEN + "/party match");
            return true;
        }
        IPlayer sgPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());
        if (args[0].equalsIgnoreCase("disband")) {
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not part of a party.");
                return true;
            }
            if (sgPlayer.getParty().getOwner() != p.getUniqueId()) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not the owner of the party.");
                return true;
            }

            OfflinePlayer memberPlayer;
            for (UUID member : sgPlayer.getParty().getMembers()) {

                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "The party you were in was disbanded");
                }

                IPlayer sgMember = core.getPlayerManager().getPlayer(member);
                sgMember.setParty(null);
            }

            return true;
        }
        if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("remove")) {
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not part of a party.");
                return true;
            }
            if (sgPlayer.getParty().getOwner() != p.getUniqueId()) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not the owner of this party.");
                return true;
            }
            if (args.length == 1) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "/party kick <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !sgPlayer.getParty().has(target)) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "That player is not in your party.");
                return true;
            }
            target.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "You were kicked from the party.");
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            sgTarget.setParty(null);
            sgPlayer.getParty().removeMember(target);
            OfflinePlayer memberPlayer;
            for (UUID member : sgPlayer.getParty().getMembers()) {

                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "" + sgTarget.getRank().getPrefix() + target.getName() + ChatColor.YELLOW + " was kicked from the party");
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
            p.sendMessage(ChatColor.YELLOW + "Owner - " + Bukkit.getOfflinePlayer(sgPlayer.getParty().getOwner()).getPlayer().getDisplayName());


            OfflinePlayer memberPlayer;
            for (UUID member : sgPlayer.getParty().getMembers()) {
                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.getUniqueId() != sgPlayer.getParty().getOwner()) {
                    p.sendMessage(ChatColor.YELLOW + "Member - " + memberPlayer.getPlayer().getDisplayName() + " " + (memberPlayer.isOnline() ? ChatColor.GREEN + "●" : ChatColor.RED + "●"));
                }
            }

            p.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------");
            return true;
        }
        if (args[0].equalsIgnoreCase("leave")) {
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not in a party");
                return true;
            }
            if (sgPlayer.getParty().getOwner() == p.getUniqueId()) {
                p.chat("/p disband");
                return true;
            }
            Party party = sgPlayer.getParty();
            party.removeMember(p);
            sgPlayer.setParty(null);
            OfflinePlayer memberPlayer;
            for (UUID member : party.getMembers()) {
                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + sgPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " left the party.");
                }
            }
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "You've left the party.");

            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (sgPlayer.getParty() != null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're are already in a party");
                return true;
            }
            Party party = new Party(p.getUniqueId());
            party.addMember(p);
            sgPlayer.setParty(party);
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "You've created a party.");
            return true;
        }
        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "/party accept <player>");
                return true;
            }
            if (sgPlayer.getParty() != null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're already in a party.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "Couldn't find that player.");
                return true;
            }
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            if (sgTarget.getParty() == null || !sgTarget.getParty().getInvited().contains(p.getUniqueId())) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You don't have a pending invite from " + target.getName());
                return true;
            }
            Party party = sgTarget.getParty();
            if (party.getMembers().contains(p.getUniqueId())) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're already in a party.");
                return true;
            }
            party.getMembers().add(p.getUniqueId());
            sgPlayer.setParty(party);

            OfflinePlayer memberPlayer;
            for (UUID member : party.getMembers()) {
                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + sgPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " joined the party!");
                }
            }


            party.getInvited().remove(p);
            return true;
        }
        if (args[0].equalsIgnoreCase("invite")) {
            if (sgPlayer.getParty() == null) {
                p.chat("/p create");
            }
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not in a party.");
                return true;
            }
            if (sgPlayer.getParty().getOwner() != p.getUniqueId()) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You don't have permission to invite players to this party.");
                return true;
            }
            if (args.length == 1) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "/party invite <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "Couldn't find that player.");
                return true;
            }
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            if (sgTarget.getParty() != null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "" + target.getName() + " is already in a party");
                return true;
            }
            if (sgPlayer.getParty().getInvited().contains(target.getUniqueId())) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "" + target.getName() + " You've already invited this player to the party.");
                return true;
            }
            Party party = sgPlayer.getParty();
            if (party.getMembers().contains(target.getUniqueId())) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "This player is already in your party.");
                return true;
            }
            party.invitePlayer(target);


            target.sendMessage(ChatColor.BLUE + "Party > " + sgPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " has invited you to their party!");

            String json = "[\"\",{\"text\":\"[ACCEPT]\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party accept %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to accept " + sgPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + "'s party invite\"}},{\"text\":\" \",\"bold\":true},{\"text\":\"[DECLINE]\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party decline %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to decline " + sgPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + "'s party invite\"}},{\"text\":\"\\n \"}]";

            IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(json.replaceAll("%inviter%", p.getName()));
            PacketPlayOutChat packet = new PacketPlayOutChat(comp);
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);

            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "You've invited " + target.getName() + " to the party.");
            return true;
        }
        if (args[0].equalsIgnoreCase("match")) {
            if (sgPlayer.getParty() == null) {
                p.chat("/p create");
            }
            if (sgPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not in a party.");
                return true;
            }
            if (sgPlayer.getParty().getMembers().size() < 2) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You need at least 2 players in your party!");
                return true;
            }
            core.getQueueManager().startPartyMatch(sgPlayer.getParty());
            return true;
        }
        if (args.length > 0) {
            if (sgPlayer.getParty() == null) {
                p.chat("/p create");
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "Couldn't find that player.");
                return true;
            }
            if (sgPlayer.getParty().has(target)) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "That player is already in your party.");
                return true;
            }
            target.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "You've been added to " + p.getName() + "'s party");
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "You've added " + target.getName() + " to the party!");
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            sgTarget.setParty(sgPlayer.getParty());
            sgPlayer.getParty().addMember(target);
        }


        return true;
    }


}