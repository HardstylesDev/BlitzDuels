package me.hardstyles.blitz.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Party {
    final private ArrayList<Player> members;
    final private Player owner;
    public Party(Player owner){
       this.owner = owner;
       this.members = new ArrayList<>();
    }

    public void addMember(Player p){
        this.members.add(p);
    }
    public void removeMember(Player playerToRemove){
        this.members.remove(playerToRemove);
    }
    public boolean has(Player playerToCheck){
        return this.members.contains(playerToCheck);
    }
    public ArrayList<Player> getMembers(){
        return this.members;
    }
    public Player getOwner(){
        return this.owner;
    }
}
