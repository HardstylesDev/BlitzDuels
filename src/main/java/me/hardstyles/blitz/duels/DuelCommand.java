package me.hardstyles.blitz.duels;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;


public class DuelCommand implements CommandExecutor {

    final private Core core;

    public DuelCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        IPlayer player = core.getPlayerManager().getPlayer(p.getUniqueId());
        if (player == null) {
            p.kickPlayer(ChatColor.AQUA + "Oopsie Daisy");
            return true;
        }
        if (player.getMatch() != null) {
            p.sendMessage(ChatColor.RED + "Can't use this command while in a match");
            return true;
        }
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            return true;
        }
        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Usage: /duel accept <player>");
                return true;
            }
            Player target = core.getServer().getPlayer(args[1]);
            if (target == null) {
                p.sendMessage(ChatColor.RED + "Couldn't find that player");
                return true;
            }
            if (!target.isOnline()) {
                p.sendMessage(ChatColor.RED + "Player is not online");
                return true;
            }
            UUID requester = null;
            DuelRequest duelRequest = null;
            for (DuelRequest request : core.getDuelManager().getRequests()) {
                if (request.getSender() == target.getUniqueId()) {
                    requester = request.getTarget();
                    duelRequest = request;
                    break;
                }
            }
            core.getDuelManager().getRequests().remove(duelRequest);

            if (requester == null) {
                p.sendMessage(ChatColor.RED + "Player hasn't challenged you to a duel");
                return true;
            }
            Arena arena = core.getArenaManager().getNext();
            if (arena == null) {
                p.sendMessage(ChatColor.RED + "There's no available arena");
                target.sendMessage(ChatColor.RED + "There's no available arena");
                return true;
            }
            Match match = new Match(core, arena);
            match.add(p.getUniqueId());
            match.add(target.getUniqueId());
            match.start();
            return true;
        }
        if (args[0].equalsIgnoreCase("decline")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Usage: /duel decline <player>");
                return true;
            }
            Player target = core.getServer().getPlayer(args[1]);
            if (target == null) {
                p.sendMessage(ChatColor.RED + "Couldn't find that player");
                return true;
            }
            if (!target.isOnline()) {
                p.sendMessage(ChatColor.RED + "Player is not online");
                return true;
            }
            UUID requester = null;
            for (DuelRequest request : core.getDuelManager().getRequests()) {
                if (request.getSender() == target.getUniqueId()) {
                    requester = request.getTarget();
                    core.getDuelManager().getRequests().remove(request);
                    break;
                }
            }
            if (requester == null) {
                p.sendMessage(ChatColor.RED + "Player hasn't challenged you to a duel");
                return true;
            }
            target.sendMessage(ChatColor.DARK_GREEN + "Duel > " + player.getRank(true).getPrefix() + p.getName() + ChatColor.YELLOW + " has declined your duel request");
            p.sendMessage(ChatColor.DARK_GREEN + "Duel > " + ChatColor.YELLOW + "Duel declined.");
            return true;
        }
        Player arg = Bukkit.getPlayer(args[0]);
        if (arg == null || !arg.isOnline()) {
            p.sendMessage(ChatColor.RED + "Can't find that player!");
            return true;
        }

        IPlayer target = core.getPlayerManager().getPlayer(arg.getUniqueId());
        if (target == null) {
            p.sendMessage(ChatColor.RED + "Can't find that player!");
            return true;
        }
        if (target.getMatch() != null) {
            p.sendMessage(ChatColor.RED + "Player is already in a match!");
            return true;
        }


        core.getDuelManager().getRequests().add(new DuelRequest(p.getUniqueId(), arg.getUniqueId()));
        p.sendMessage(ChatColor.YELLOW + "You've sent a duel request to " + target.getRank(true).getPrefix() + arg.getName() + ChatColor.YELLOW + "!");
        String json = "[\"\",{\"text\":\"[ACCEPT]\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/duel accept %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to accept " + player.getRank(true).getPrefix() + p.getName() + ChatColor.YELLOW + "'s duel request\"}},{\"text\":\" \",\"bold\":true},{\"text\":\"[DECLINE]\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/duel decline %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to decline " + player.getRank(true).getPrefix() + p.getName() + ChatColor.YELLOW + "'s duel request\"}}]";

        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(json.replaceAll("%inviter%", p.getName()));
        PacketPlayOutChat packet = new PacketPlayOutChat(comp);
        arg.sendMessage(ChatColor.DARK_GREEN + "Duel > " + player.getRank(true).getPrefix() + p.getName() + ChatColor.YELLOW + " has challenged you to a duel!");

        ((CraftPlayer) arg).getHandle().playerConnection.sendPacket(packet);

        return true;
    }

}

