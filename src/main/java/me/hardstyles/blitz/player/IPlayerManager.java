package me.hardstyles.blitz.player;

import lombok.Getter;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.match.match.Match;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class IPlayerManager {
	private final HashMap<UUID, IPlayer> players = new HashMap<>();
	private final Core core;

	public IPlayerManager(Core core) {
		this.core = core;
	}

	public IPlayer getPlayer(UUID uuid) {
		return players.get(uuid);
	}

	public void addPlayer(UUID uuid, IPlayer uhcPlayer) {
		players.put(uuid, uhcPlayer);
	}

	public void removeBsgPlayer(UUID uuid) {
		players.remove(uuid);
	}

	public void hub(Player p) {
		p.spigot().setCollidesWithEntities(true);
		p.setFlying(false);
		p.setAllowFlight(false);
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(20);
		p.setSaturation(20);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.setFallDistance(0);
		p.getInventory().clear();
		p.updateInventory();
		p.getInventory().setArmorContents(null);
		p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
		((CraftPlayer) p).getHandle().getDataWatcher().watch(9, (byte) 0);

		hubInventory(p);

		IPlayer iPlayer = core.getPlayerManager().getPlayer(p.getUniqueId());
		if (iPlayer.getMatch() != null) {
			iPlayer.getMatch().onDeath(iPlayer.getUuid());
			iPlayer.getMatch().leave(iPlayer.getUuid());
			iPlayer.setMatch(null);
			for (Player other : Bukkit.getOnlinePlayers()) {
				other.showPlayer(p);
			}
		}

		p.teleport(core.getLobbySpawn());
	}

	public void hubInventory(Player p) {
		p.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name("&ePlay").make());
		p.getInventory().setItem(8, new ItemBuilder(Material.BOOK).name("&eKit Editor").make());
	}

	public void reset(Player p) {
		p.spigot().setCollidesWithEntities(true);
		p.setLevel(0);
		p.setExp(0);
		p.setExhaustion(0);
		p.setFlying(false);
		p.setAllowFlight(false);
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(20);
		p.setSaturation(20);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.setFallDistance(0);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
	}

	public void handleKillElo(Player victim, Player killer) {
		IPlayer victimUhc = this.getPlayer(victim.getUniqueId());
		IPlayer killerUhc = this.getPlayer(killer.getUniqueId());
		double eloChange = 0;
		if (killerUhc.getElo() > 0)
			eloChange = Math.sqrt(victimUhc.getElo() / killerUhc.getElo()) + 1;
		else if (killerUhc.getElo() == 0)
			eloChange = Math.sqrt(victimUhc.getElo() / 1) + 1;

		victimUhc.removeElo((int) eloChange);
		victim.sendMessage("§c-" + (int)eloChange + " §7ELO §c(\u25bc" + victimUhc.getElo() + ")");
		killerUhc.addElo((int) eloChange);
		killer.sendMessage("§a+" + (int)eloChange + " §7ELO §a(\u25b2" + killerUhc.getElo() + ")");
	}

	public void handleDeathElo(Player victim) {
		IPlayer victimUhc = this.getPlayer(victim.getUniqueId());
		Match g = victimUhc.getMatch();

		double allPlayerElo = 0;
		for(UUID pl : g.getPlayers())
			if(pl != victim.getUniqueId())
				allPlayerElo += core.getPlayerManager().getPlayer(pl).getElo();
		double eloChange = 0;
		if(allPlayerElo > 0)
			eloChange = ((victimUhc.getElo() * 0.1)/((allPlayerElo/(g.getPlayers().size()-1)))) * 4 + 1;
		else if(allPlayerElo == 0)
			eloChange = (victimUhc.getElo() * 0.1) * 4 + 1;

		victimUhc.removeElo((int) eloChange);
		victim.sendMessage("§c-" + (int)eloChange + " §7ELO §c(\u25bc" + victimUhc.getElo() + ")");
	}
	
	public void handleWinElo(Match g) {
		IPlayer iPlayer = core.getPlayerManager().getPlayer(g.getWinner());
		double allPlayerElo = 0;
		for(UUID pl : g.getPlayers())
			allPlayerElo += core.getPlayerManager().getPlayer(pl).getElo();
		double eloToAdd = (allPlayerElo/(iPlayer.getElo() ^ g.getPlayers().size())) * 10 + 1;
		if(iPlayer.getElo() == 0)
			eloToAdd = (allPlayerElo/g.getPlayers().size()) * 10 + 1;
		iPlayer.addElo((int) eloToAdd);
		core.getServer().getPlayer(g.getWinner()).sendMessage("§a+" + (int)eloToAdd + " §7ELO §a(\u25b2" + iPlayer.getElo() + ")");
	}

	//public void handleWinElo(Game g) {
	//	//Get Player
	//	try{
	//	BlitzSGPlayer uhcPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(g.getWinner().getUniqueId());
	//
	//	//Calculate ELO
	//	double allPlayerElo = 0;
	//	for(Player pl : g.getAllPlayers())
	//		if(pl.getUniqueId() != g.getWinner().getUniqueId())
	//			allPlayerElo += BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(pl.getUniqueId()).getElo();
	//	double eloChange = (((allPlayerElo * 0.5)/(g.getAllPlayers().size()-1))/uhcPlayer.getElo()) * 4 + 1;
	//	if(uhcPlayer.getElo() == 0)
	//		eloChange = 1;
	//
	//	//Apply ELO + Message Player
	//	uhcPlayer.addElo((int) eloChange);
	//	}catch (NullPointerException e){
	//		System.out.println("elo bug");
	//	}
	//	//g.getWinner().sendMessage("§a+" + (int)eloChange + " §7ELO §a(\u25b2" + uhcPlayer.getElo() + ")");
}
	

