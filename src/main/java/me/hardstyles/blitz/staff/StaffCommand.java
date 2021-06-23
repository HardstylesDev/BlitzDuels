package me.hardstyles.blitz.staff;

import com.google.common.collect.ImmutableList;
import me.hardstyles.blitz.player.IPlayer;
import org.bukkit.entity.Player;

import java.util.List;


public class StaffCommand extends me.hardstyles.blitz.utils.Command {

    public StaffCommand() {
        super("staff", ImmutableList.of(), 6);
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player p, IPlayer iPlayer, String[] args) {

    }
}

