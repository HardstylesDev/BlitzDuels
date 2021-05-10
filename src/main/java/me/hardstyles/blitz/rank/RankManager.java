package me.hardstyles.blitz.rank;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.rank.ranks.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RankManager {

    public static final Class<? extends Rank>[] RANKS = new Class[]{Default.class, Admin.class, Helper.class, Champion.class, Moderator.class, Vip.class, VipPlus.class, Mvp.class, MvpPlus.class, Youtuber.class};
    private final ArrayList<Rank> ranks = new ArrayList<>();


    public Rank getRank(Player p) {
        IPlayer bsgPlayer = Core.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        if (bsgPlayer.getRank() == null)
            return getRankByName("Default");
        return bsgPlayer.getRank();
    }
    public Rank getRank(Player p, boolean hideNick) {
        IPlayer iPlayer = Core.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        if (iPlayer.getNick() != null && !iPlayer.getNick().getNickName().equalsIgnoreCase("")) {
            return getRankByName("Default");
        }
        return getRank(p);
    }


    public void setRank(Player p, Rank rank) {
        IPlayer bsgPlayer = Core.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        bsgPlayer.setRank(rank);
    }

    public RankManager() {
        for (Class<? extends Rank> rankClass : RANKS) {
            try {
                this.ranks.add(rankClass.getConstructor().newInstance());

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public Rank getRankByName(String name) {
        for (Rank r : this.ranks) {
            if (!r.getRank().equalsIgnoreCase(name)) continue;
            return r;
        }
        return null;
    }


    public Set<Rank> getRanks() {
        HashSet<Rank> allModules = new HashSet<Rank>();
        allModules.addAll(this.ranks);
        return allModules;
    }
}
