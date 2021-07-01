package me.hardstyles.blitz.duels;

import com.google.common.collect.ImmutableList;
import me.hardstyles.blitz.arena.Arena;
import me.hardstyles.blitz.commands.Command;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DuelsCommand extends Command {

    private final Map<UUID, Long> cooldowns = new HashMap<>();


    public DuelsCommand() {
        super("duel", ImmutableList.of("dual", "1v1", "match", "fight"), 0);

    }

    public DuelsCommand(String name, List<String> aliases, int position) {
        super(name, aliases, position);
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {
        if (iPlayer == null) {
            p.kickPlayer(ChatColor.AQUA + "Oopsie Daisy");
            return;
        }
        if (iPlayer.getMatch() != null) {
            p.sendMessage(ChatColor.RED + "Can't use this command while in a match");
            return;
        }
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            return;
        }
        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Usage: /duel accept <player>");
                return;
            }
            Player target = core.getServer().getPlayer(args[1]);
            if (target == null) {
                p.sendMessage(ChatColor.RED + "Couldn't find that player");
                return;
            }
            if (!target.isOnline()) {
                p.sendMessage(ChatColor.RED + "Player is not online");
                return;
            }
            UUID requester = core.getDuelManager().getRequests().remove(p.getUniqueId());

            if (requester == null) {
                p.sendMessage(ChatColor.RED + "Player hasn't challenged you to a duel");
                return;
            }
            Arena arena = core.getArenaManager().getNext();
            if (arena == null) {
                p.sendMessage(ChatColor.RED + "There's no available arena");
                target.sendMessage(ChatColor.RED + "There's no available arena");
                return;
            }
            Match match = new Match(core, arena);
            match.add(p.getUniqueId());
            match.add(target.getUniqueId());
            match.start();
        } else if (args[0].equalsIgnoreCase("decline")) {
            if (args.length == 1) {
                p.sendMessage(ChatColor.RED + "Usage: /duel decline <player>");
                return;
            }
            Player target = core.getServer().getPlayer(args[1]);
            if (target == null) {
                p.sendMessage(ChatColor.RED + "Couldn't find that player");
                return;
            }
            if (!target.isOnline()) {
                p.sendMessage(ChatColor.RED + "Player is not online");
                return;
            }
            if (core.getDuelManager().getRequests().remove(p.getUniqueId()) == null) {
                p.sendMessage(ChatColor.RED + "Player hasn't challenged you to a duel");
                return;
            }
            target.sendMessage(ChatColor.DARK_GREEN + "Duel > " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " has declined your duel request");
            p.sendMessage(ChatColor.DARK_GREEN + "Duel > " + ChatColor.YELLOW + "Duel declined.");
        } else {
            Player arg = Bukkit.getPlayer(args[0]);
            IPlayer target = arg == null ? null : core.getPlayerManager().getPlayer(arg.getUniqueId());
            if (target == null) {
                p.sendMessage(ChatColor.RED + "Can't find that player!");
                return;
            }
            if (target.getUuid().equals(p.getUniqueId())) {
                p.sendMessage(ChatColor.RED + "You cannot duel yourself!");
                return;
            }
            if (target.getMatch() != null) {
                p.sendMessage(ChatColor.RED + "Player is already in a match!");
                return;
            }

            long remainingTime = 30000 - (System.currentTimeMillis() - cooldowns.getOrDefault(p.getUniqueId(), 0L));
            if (remainingTime > 0) {
                p.sendMessage(ChatColor.RED + "You are on cooldown for " + (remainingTime / 1000) + " more seconds.");
                return;
            }
            cooldowns.put(p.getUniqueId(), System.currentTimeMillis());
            core.getDuelManager().getRequests().put(target.getUuid(), p.getUniqueId());
            p.sendMessage(ChatColor.YELLOW + "You've sent a duel request to " + target.getRank().getPrefix() + arg.getName() + ChatColor.YELLOW + "!");
            String json = "[\"\",{\"text\":\"[ACCEPT]\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/duel accept %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to accept " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + "'s duel request\"}},{\"text\":\" \",\"bold\":true},{\"text\":\"[DECLINE]\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/duel decline %inviter%\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\" " + ChatColor.YELLOW + "Click here to decline " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + "'s duel request\"}}]";

            IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(json.replaceAll("%inviter%", p.getName()));
            PacketPlayOutChat packet = new PacketPlayOutChat(comp);
            arg.sendMessage(ChatColor.DARK_GREEN + "Duel > " + iPlayer.getRank().getPrefix() + p.getName() + ChatColor.YELLOW + " has challenged you to a duel!");

            ((CraftPlayer) arg).getHandle().playerConnection.sendPacket(packet);
        }

    }

}
