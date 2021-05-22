package me.hardstyles.blitz.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import me.hardstyles.blitz.Core;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.hardstyles.blitz.utils.ItemUtils;

public class IPlayerManager {


	private HashMap<UUID, IPlayer> bsgPlayers;
final private Core core;
	public IPlayerManager(Core core) {
		this.core = core;
		bsgPlayers = new HashMap<UUID, IPlayer>();
	}

	public HashMap<UUID, IPlayer> getBsgPlayers() {
		return bsgPlayers;
	}

	public IPlayer getPlayer(UUID uuid) {
		return bsgPlayers.get(uuid);
	}

	public void addBsgPlayer(UUID uuid, IPlayer uhcPlayer) {
		bsgPlayers.put(uuid, uhcPlayer);
	}

	public void removeBsgPlayer(UUID uuid) {
		bsgPlayers.remove(uuid);
	}

	
	public void setLobbyInventoryAndNameTag(Player p) {
		p.getInventory().clear();
		p.getInventory().setItem(3, ItemUtils.buildItem(new ItemStack(Material.EMERALD), "&aShop &7(Right-Click)", Arrays.asList("§7Right-Click to open the shop")));
		p.getInventory().setItem(5, ItemUtils.buildItem(new ItemStack(Material.PAINTING), "&e&lYour Stats &7(Right-Click)", Arrays.asList("§7Right-Click to view your stats")));
		p.getInventory().setItem(7, ItemUtils.buildItem(new ItemStack(Material.SKULL_ITEM), "&c???", Arrays.asList("§7Coming soon...")));

		core.getNametagManager().update();


		//ArrayList<NametagEdit> nametagEdits = BlitzSG.getInstance().getRankManager().getNametags();
		//for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//
		//	for (NametagEdit nametagEdit : nametagEdits) {
		//		if (nametagEdit.getRequiredRank() == bsgPlayer.getRank())
		//			nametagEdit.addPlayer(onlinePlayer);
		//	}
//
		//}
		//for (NametagEdit nametagEdit : nametagEdits) {
		//	nametagEdit.updateAll();
		//}
	}

	public void hub(Player p){
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

		hubIntentory(p);
		p.teleport(core.getLobbySpawn());
	}
	public void hubIntentory(Player p){
		p.getInventory().setItem(1, ItemUtils.buildItem(new ItemStack(Material.IRON_SWORD), "&eSolo Queue", Arrays.asList("§7Right-Click to join the queue")));
		p.getInventory().setItem(2, ItemUtils.buildItem(new ItemStack(Material.IRON_SWORD, 2), "&eTeams Queue", Arrays.asList("§cSoon")));

	}
	public void reset(Player p){
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
		if(killerUhc.getElo() > 0)
			eloChange = Math.sqrt(victimUhc.getElo()/killerUhc.getElo()) + 1;
		else if(killerUhc.getElo() == 0)
			eloChange = Math.sqrt(victimUhc.getElo()/1) + 1;
		
		victimUhc.removeElo((int) eloChange);
		//victim.sendMessage("§c-" + (int)eloChange + " §7ELO §c(\u25bc" + victimUhc.getElo() + ")");
		killerUhc.addElo((int) eloChange);
		//killer.sendMessage("§a+" + (int)eloChange + " §7ELO §a(\u25b2" + killerUhc.getElo() + ")");
	}
	
	//public void handleDeathElo(Player victim) {
	//	BlitzSGPlayer victimUhc = this.getBsgPlayer(victim.getUniqueId());
	//	Game g = victimUhc.getGame();
	//
	//	double allPlayerElo = 0;
	//	for(Player pl : g.getAllPlayers())
	//		if(pl.getUniqueId() != victim.getUniqueId())
	//			allPlayerElo += BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(pl.getUniqueId()).getElo();
	//	double eloChange = 0;
	//	if(allPlayerElo > 0)
	//		eloChange = ((victimUhc.getElo() * 0.1)/((allPlayerElo/(g.getAllPlayers().size()-1)))) * 4 + 1;
	//	else if(allPlayerElo == 0)
	//		eloChange = (victimUhc.getElo() * 0.1) * 4 + 1;
	//
	//	victimUhc.removeElo((int) eloChange);
	//	//victim.sendMessage("§c-" + (int)eloChange + " §7ELO §c(\u25bc" + victimUhc.getElo() + ")");
	//}
	
	/*public void handleWinElo(Game g) {
		SpeedUHCPlayer uhcPlayer = SpeedUHC.getInstance().getSpeedUHCPlayerManager().getUhcPlayer(g.getWinner().getUniqueId());
		double allPlayerElo = 0;
		for(Player pl : g.getAllPlayers())
			allPlayerElo += SpeedUHC.getInstance().getSpeedUHCPlayerManager().getUhcPlayer(pl.getUniqueId()).getElo();
		double eloToAdd = (allPlayerElo/(uhcPlayer.getElo() ^ g.getAllPlayers().size())) * 10 + 1;
		if(uhcPlayer.getElo() == 0)
			eloToAdd = (allPlayerElo/g.getAllPlayers().size()) * 10 + 1;
		uhcPlayer.addElo((int) eloToAdd);
		g.getWinner().sendMessage("§a+" + (int)eloToAdd + " §7ELO §a(\u25b2" + uhcPlayer.getElo() + ")");
	}*/
	
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
	

