package me.hardstyles.blitz.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Party {
    final private ArrayList<UUID> members;
    final private ArrayList<UUID> invited;
    final private UUID owner;

    public Party(UUID owner) {
        this.owner = owner;
        this.members = new ArrayList<>();
        this.invited = new ArrayList<>();
    }

    public void addMember(Player p) {
        this.members.add(p.getUniqueId());
    }

    public void removeMember(Player playerToRemove) {
        this.members.remove(playerToRemove.getUniqueId());
    }

    public boolean has(Player playerToCheck) {
        return this.members.contains(playerToCheck.getUniqueId());
    }

    public ArrayList<UUID> getMembers() {
        return this.members;
    }

    public UUID getOwner() {
        return (this.owner);
    }

    public ArrayList<UUID> getInvited() {
        return this.invited;
    }

    public void invitePlayer(Player toInvite) {
        this.invited.add(toInvite.getUniqueId());
    }

    public void removeInvite(Player removed) {
        this.invited.remove(removed.getUniqueId());
    }
}
