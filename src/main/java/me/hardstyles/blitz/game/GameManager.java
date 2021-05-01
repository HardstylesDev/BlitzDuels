package me.hardstyles.blitz.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import org.bukkit.entity.Player;

public class GameManager {
	
	private HashSet<Game> games;
	
	public GameManager() {
		games = new HashSet<Game>();
	}
	
	public Game getAvailableGame() {
		for(Game g : games) {
			if(g.getGameMode() == Game.GameMode.WAITING || g.getGameMode() == Game.GameMode.STARTING) {
				return g;
			}
		}
		if(BlitzSG.getInstance().getArenaManager().getRandomArena() != null){
			Game game = new Game();
			return game;
		}
		return null;
	}
	
	public ArrayList<Game> getRunningGames() {
		ArrayList<Game> runningGames = new ArrayList<Game>();
		for(Game g : games) {
			if(g.getGameMode() != Game.GameMode.RESETING)
				runningGames.add(g);
		}
		return runningGames;
	}
	
	public void addGame(Game g) {
		games.add(g);
	}
	
	public void removeGame(Game g) {
		games.add(g);
	}
    
    public HashMap<Integer, BlitzSGPlayer> getTopKillers(Game g) {
    	Comparator<BlitzSGPlayer> killSorter = new Comparator<BlitzSGPlayer>() {
			@Override
			public int compare(BlitzSGPlayer a, BlitzSGPlayer b) {
				if(a.getGameKills() > b.getGameKills()) return -1;
				else if(a.getGameKills() < b.getGameKills()) return 1;
				return 0;
			}
    	};
    	HashMap<Integer, BlitzSGPlayer> map = new HashMap<Integer, BlitzSGPlayer>();
    	ArrayList<BlitzSGPlayer> kitPlayers = new ArrayList<BlitzSGPlayer>();
    	for(Player kp : g.getAllPlayers()) {
    		kitPlayers.add(BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(kp.getUniqueId()));
    	}
    	kitPlayers.sort(killSorter);
    	for(BlitzSGPlayer kp : kitPlayers) {
    		map.put(kitPlayers.indexOf(kp)+1, kp);
    	}
		return map;
    }

}
