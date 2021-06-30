package me.hardstyles.blitz.party;

import com.google.common.collect.ImmutableList;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.match.match.TeamMatch;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PartyCommand extends me.hardstyles.blitz.utils.Command {

    private final Core core = Core.i();

    public PartyCommand() {
        super("party", ImmutableList.of("p"), 0);
    }

    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (args.length == 0) {
            p.sendMessage(ChatColor.GREEN + "/party <player>");
            p.sendMessage(ChatColor.GREEN + "/party kick/remove <player>");
            p.sendMessage(ChatColor.GREEN + "/party invite <player>");
            p.sendMessage(ChatColor.GREEN + "/party disband");
            p.sendMessage(ChatColor.GREEN + "/party transfer <player>");
            p.sendMessage(ChatColor.GREEN + "/party match");
            return;
        }
        if (args[0].equalsIgnoreCase("disband")) {
            if (iPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not part of a party.");
                return;
            }

            if (iPlayer.getParty().getOwner() != p.getUniqueId()) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not the owner of the party.");
                return;
            }
            if(iPlayer.hasMatch() && iPlayer.getMatch() instanceof TeamMatch){
                p.sendMessage("§cCan't leave the party while in a Team Match.");
                return;
            }
            OfflinePlayer memberPlayer;
            for (UUID member : iPlayer.getParty().getMembers()) {

                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "The party you were in was disbanded");
                }

                IPlayer sgMember = core.getPlayerManager().getPlayer(member);
                sgMember.setParty(null);
            }

            return;
        }
        if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("remove")) {
            if (iPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not part of a party.");
                return;
            }
            if(iPlayer.hasMatch() && iPlayer.getMatch() instanceof TeamMatch){
                p.sendMessage("§cCan't leave the party while in a Team Match.");
                return;
            }
            if (iPlayer.getParty().getOwner() != p.getUniqueId()) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not the owner of this party.");
                return;
            }
            if (args.length == 1) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "/party kick <player>");
                return;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !iPlayer.getParty().has(target)) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "That player is not in your party.");
                return;
            }
            target.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "You were kicked from the party.");
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            sgTarget.setParty(null);
            iPlayer.getParty().removeMember(target);
            OfflinePlayer memberPlayer;
            for (UUID member : iPlayer.getParty().getMembers()) {

                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "" + sgTarget.getRank().getPrefix() + target.getName() + ChatColor.YELLOW + " was kicked from the party");
                }
            }
            return;
        }
        if (args[0].equalsIgnoreCase("list")) {
            if (iPlayer.getParty() == null) {
                p.sendMessage(ChatColor.RED + "You're not part of a party.");
                return;
            }
            p.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------");
            p.sendMessage(ChatColor.YELLOW + "Owner - " + Bukkit.getOfflinePlayer(iPlayer.getParty().getOwner()).getPlayer().getName());


            OfflinePlayer memberPlayer;
            for (UUID member : iPlayer.getParty().getMembers()) {
                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.getUniqueId() != iPlayer.getParty().getOwner() && memberPlayer.isOnline()) {
                    p.sendMessage(ChatColor.YELLOW + "Member - " + memberPlayer.getPlayer().getName() + " " + (memberPlayer.isOnline() ? ChatColor.GREEN + "●" : ChatColor.RED + "●"));
                }
            }

            p.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------");
            return;
        }
        if (args[0].equalsIgnoreCase("leave")) {
            if (iPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not in a party");
                return;
            }
            if(iPlayer.hasMatch() && iPlayer.getMatch() instanceof TeamMatch){
                p.sendMessage("§cCan't leave the party while in a Team Match.");
                return;
            }
            if (iPlayer.getParty().getOwner() == p.getUniqueId()) {
                p.chat("/party disband");
                return;
            }
            Party party = iPlayer.getParty();
            party.removeMember(p);
            iPlayer.setParty(null);
            OfflinePlayer memberPlayer;
            for (UUID member : party.getMembers()) {
                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " left the party.");
                }
            }
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "You've left the party.");

            return;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (iPlayer.getParty() != null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're are already in a party");
                return;
            }
            Party party = new Party(p.getUniqueId());
            party.addMember(p);
            iPlayer.setParty(party);
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "You've created a party.");
            return;
        }
        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "/party accept <player>");
                return;
            }
            if (iPlayer.getParty() != null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're already in a party.");
                return;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "Couldn't find that player.");
                return;
            }
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            if (sgTarget.getParty() == null || !sgTarget.getParty().getInvited().contains(p.getUniqueId())) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You don't have a pending invite from " + target.getName());
                return;
            }
            Party party = sgTarget.getParty();
            if (party.getMembers().contains(p.getUniqueId())) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're already in a party.");
                return;
            }
            party.getMembers().add(p.getUniqueId());
            iPlayer.setParty(party);

            OfflinePlayer memberPlayer;
            for (UUID member : party.getMembers()) {
                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " joined the party!");
                }
            }


            party.getInvited().remove(p.getUniqueId());
        }
        if (args[0].equalsIgnoreCase("invite")) {
            if (iPlayer.getParty() == null) {
                p.chat("/party create");
            }
            if (iPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not in a party.");
                return;
            }
            if (iPlayer.getParty().getOwner() != p.getUniqueId()) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You don't have permission to invite players to this party.");
                return;
            }
            if (args.length == 1) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "/party invite <player>");
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "Couldn't find that player.");
                return;
            }
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            if (sgTarget.getParty() != null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "" + target.getName() + " is already in a party");
                return;
            }
            if (iPlayer.getParty().getInvited().contains(target.getUniqueId())) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "" + target.getName() + " You've already invited this player to the party.");
                return;
            }
            Party party = iPlayer.getParty();
            if (party.getMembers().contains(target.getUniqueId())) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "This player is already in your party.");
                return;
            }
            party.invitePlayer(target);


            target.sendMessage(ChatColor.BLUE + "Party > " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " has invited you to their party!");

            String json = "[\"\",{\"text\":\"[ACCEPT]\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party accept %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to accept " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + "'s party invite\"}},{\"text\":\" \",\"bold\":true},{\"text\":\"[DECLINE]\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party decline %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to decline " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + "'s party invite\"}},{\"text\":\"\\n \"}]";

            IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(json.replaceAll("%inviter%", p.getName()));
            PacketPlayOutChat packet = new PacketPlayOutChat(comp);
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);

            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "You've invited " + target.getName() + " to the party.");
            return;
        }
        if (args[0].equalsIgnoreCase("match")) {
            if (iPlayer.getParty() == null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're not in a party.");
                return;
            }
            if (iPlayer.getMatch() != null) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You're already in a match.");
                return;
            }
            if (iPlayer.getParty().getMembers().size() < 2) {
                p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "You need at least 2 players in your party!");
                return;
            }
            for (UUID uuid : iPlayer.getParty().getMembers()) {
                IPlayer member = core.getPlayerManager().getPlayer(uuid);
                if (member.getMatch() != null) {
                    p.sendMessage("§cAll of your party must be in the lobby! warp with /p warp.");
                    return;
                }
            }
            Arena arena = core.getArenaManager().getNext();


            if (arena == null) {
                p.sendMessage(ChatColor.RED + "No available arena!");
                return;
            }
            Match match = new Match(core,arena);
            for (UUID member : iPlayer.getParty().getMembers()) {
                match.add(member);
            }
            match.start();
            return;
        }
        if (args[0].equalsIgnoreCase("warp")) {
            if (iPlayer.getParty() == null) {
                p.sendMessage("§cYou are not in a party.");
                return;
            }
            for (UUID uuid : iPlayer.getParty().getMembers()) {
                IPlayer member = core.getPlayerManager().getPlayer(uuid);
                if (member.getMatch() != null) {
                    core.getPlayerManager().hub(Bukkit.getPlayer(uuid));
                }
            }
            p.sendMessage("§aYou have warped your party to the lobby!");
            return;
        }
        if (iPlayer.getParty() == null) {
            p.chat("/party create");
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "Couldn't find that player.");
            return;
        }
        if (iPlayer.getParty().has(target)) {
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.RED + "That player is already in your party.");
            return;
        }
        if (iPlayer.getRank().getPosition() > 5) {
            target.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "You've been added to " + p.getName() + "'s party");
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.YELLOW + "You've added " + target.getName() + " to the party!");
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            sgTarget.setParty(iPlayer.getParty());
            iPlayer.getParty().addMember(target);
        } else {
            iPlayer.getParty().invitePlayer(target);


            target.sendMessage(ChatColor.BLUE + "Party > " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " has invited you to their party!");

            String json = "[\"\",{\"text\":\"[ACCEPT]\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party accept %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to accept " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + "'s party invite\"}},{\"text\":\" \",\"bold\":true},{\"text\":\"[DECLINE]\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party decline %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to decline " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + "'s party invite\"}}]";

            IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(json.replaceAll("%inviter%", p.getName()));
            PacketPlayOutChat packet = new PacketPlayOutChat(comp);
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
            IPlayer sgTarget = core.getPlayerManager().getPlayer(target.getUniqueId());
            p.sendMessage(ChatColor.BLUE + "Party > " + ChatColor.GREEN + "You've invited " + target.getName() + " to the party.");
            OfflinePlayer memberPlayer;
            for (UUID member : iPlayer.getParty().getMembers()) {
                memberPlayer = Bukkit.getOfflinePlayer(member);
                if (memberPlayer.isOnline()) {
                    memberPlayer.getPlayer().sendMessage(ChatColor.BLUE + "Party > " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " invited " + sgTarget.getRank().getPrefix() + target.getName() + ChatColor.YELLOW + " to the party!");
                }
            }

        }
    }


    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return ImmutableList.of();
    }
}