package me.hardstyles.blitz.gui;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.hardstyles.blitz.BlitzSG;
import me.hardstyles.blitz.kit.Kit;
import me.hardstyles.blitz.kit.KitUtils;
import me.hardstyles.blitz.blitzsgplayer.BlitzSGPlayer;
import me.hardstyles.blitz.cosmetic.Aura;
import me.hardstyles.blitz.cosmetic.Taunt;
import me.hardstyles.blitz.gamestar.Star;
import me.hardstyles.blitz.rank.ranks.Admin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryHandler implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        BlitzSG.getInstance().getGuiManager().setInGUI((Player)
                e.getPlayer(), false);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        BlitzSGPlayer bsgPlayer = BlitzSG.getInstance().getBlitzSGPlayerManager().getBsgPlayer(p.getUniqueId());
        if (!bsgPlayer.isInGame())
            if (!(bsgPlayer.getRank() instanceof Admin))
                e.setCancelled(true);
        if (BlitzSG.getInstance().getGuiManager().isInGUI(p))
            e.setCancelled(true);
        //if(e.getInventory().getName() != "§7Kit Selector")
        //return;

        if (e.getRawSlot() >= e.getInventory().getSize() || e.getRawSlot() <= -1)
            return;
        if (e.getInventory().getItem(e.getSlot()) == null)
            return;
        if (e.getInventory().getName() == "§8Kit Selector") {
            e.setCancelled(true);
            if (e.getInventory().getItem(e.getSlot()).getType() != Material.AIR) {
                if (!bsgPlayer.isInGame())
                    return;
                Kit kit = BlitzSG.getInstance().getKitManager().getKit(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName());
                if (kit == null)
                    return;

                //e.getWhoClicked().sendMessage("Selected: " + kit.getName());
                if (bsgPlayer.getKitLevel(kit) == 0) {
                    if (!(kit.getPrice(0) == 0) && !(kit.getRequiredRank().getPosition() <= bsgPlayer.getRank().getPosition())) {
                        BlitzSG.send((Player) e.getWhoClicked(), BlitzSG.CORE_NAME + "&cYou don't have this kit!");
                        return;
                    }
                }
                BlitzSG.send((Player) e.getWhoClicked(), BlitzSG.CORE_NAME + "&eYou have chosen the &a" + kit.getName() + KitUtils.getKitTag(bsgPlayer.getKitLevel(kit)) + " &ekit, You will get your items 60 seconds after the game starts.");
                bsgPlayer.setSelectedKit(kit);
                //if(e.isLeftClick())
                //	bsgPlayer.getGame().setVote(p, true);
                //else if(e.isRightClick())
                //	bsgPlayer.getGame().setVote(p, false);
                //p.getOpenInventory().setItem(13, ItemUtils.buildItem(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "§eEnable Player Heads?"
                //		, Arrays.asList("§7Left-Click to vote §aTrue", "§7Right-Click to vote §cFalse"
                //		, "§7", "§7Status:", "§a" + bsgPlayer.getGame().getTrueVotes()
                //		+ " §7/ §c" + bsgPlayer.getGame().getFalseVotes() + " §8("
                //				+ bsgPlayer.getGame().getVotingPercentage() + "%)")));
                //p.closeInventory();
            }
        } else if (e.getInventory().getName() == "§8Blitz Shop") {
            e.setCancelled(true);
            if (bsgPlayer.isInGame())
                return;
            if (e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().contains("Basic Kit"))
                ShopKitBasicGUI.openGUI(p);
            if (e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().contains("Auras"))
                AuraGUI.openGUI(p);
            if (e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().contains("Taunt"))
                TauntGUI.openGUI(p);
            if (e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().contains("Advanced Kit"))
                ShopKitAdvancedGUI.openGUI(p);
            if (e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().contains("Blitz Powerups"))
                ShopStarGUI.openGUI(p);
            return;
        } else if (e.getInventory().getName().contains("Kit Upgrades")) {
            e.setCancelled(true);
            if (bsgPlayer.isInGame())
                return;
            if (BlitzSG.getInstance().getKitManager().getKit(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName()) == null)
                return;
            Kit kit = BlitzSG.getInstance().getKitManager().getKit(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName());
            if (bsgPlayer.getKitLevel(kit) == 0 && e.getInventory().getName() == "§8Basic Kit Upgrades") {
                if (bsgPlayer.getCoins() < kit.getPrice(bsgPlayer.getKitLevel(kit) + 1)) {
                    p.sendMessage("§cYou don't have enough coins to purchase this upgrade!");
                    return;
                }
                p.sendMessage(ChatColor.GOLD + "You purchased " + ChatColor.GREEN + kit.getName() + KitUtils.getKitTag(bsgPlayer.getKitLevel(kit) + 2) + "");
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

                bsgPlayer.removeCoins(kit.getPrice(bsgPlayer.getKitLevel(kit) + 1));
                bsgPlayer.setKitLevel(kit, bsgPlayer.getKitLevel(kit) + 2);
                p.closeInventory();
                Bukkit.getScheduler().runTaskAsynchronously(BlitzSG.getInstance(), () -> BlitzSG.getInstance().getStatisticsManager().save(bsgPlayer));

                return;
            }
            if (bsgPlayer.getKitLevel(kit) == 0 && kit.getRequiredRank().getPosition() <= bsgPlayer.getRank().getPosition()) {
                p.sendMessage(ChatColor.GOLD + "You unlocked the " + ChatColor.GREEN + kit.getName() + ChatColor.GOLD + " kit!");
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                bsgPlayer.setKitLevel(kit, 1);
                p.closeInventory();
                Bukkit.getScheduler().runTaskAsynchronously(BlitzSG.getInstance(), () -> BlitzSG.getInstance().getStatisticsManager().save(bsgPlayer));
                return;
            }
            if (kit.getPrice(bsgPlayer.getKitLevel(kit)) == -1) {
                p.sendMessage("§cYou already have this kit at max level!!");
                return;
            }
            if (bsgPlayer.getCoins() < kit.getPrice(bsgPlayer.getKitLevel(kit))) {
                p.sendMessage("§cYou don't have enough coins to purchase this upgrade!");
                return;
            }
            p.sendMessage(ChatColor.GOLD + "You purchased " + ChatColor.GREEN + kit.getName() + KitUtils.getKitTag(bsgPlayer.getKitLevel(kit) + 1) + "");
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

            bsgPlayer.removeCoins(kit.getPrice(bsgPlayer.getKitLevel(kit)));
            bsgPlayer.setKitLevel(kit, bsgPlayer.getKitLevel(kit) + 1);
            p.closeInventory();
            Bukkit.getScheduler().runTaskAsynchronously(BlitzSG.getInstance(), () -> BlitzSG.getInstance().getStatisticsManager().save(bsgPlayer));
        } else if (e.getInventory().getName() == "§8Blitz Star Shop") {
            e.setCancelled(true);
            if (bsgPlayer.isInGame())
                return;
            if (BlitzSG.getInstance().getStarManager().getStar(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName())) == null)
                return;
            Star star = BlitzSG.getInstance().getStarManager().getStar(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName()));
            if (star.getPrice() == 0) {
                p.sendMessage("§cYou already have this star unlocked!");
                return;
            }
            if (bsgPlayer.getCoins() < star.getPrice()) {
                p.sendMessage("§cYou don't have enough coins to purchase this star!");
                return;
            }
            p.sendMessage(ChatColor.GOLD + "You purchased star " + ChatColor.GREEN + star.getName());
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

            bsgPlayer.removeCoins(star.getPrice());
            bsgPlayer.addStar(star);
            p.closeInventory();
            Bukkit.getScheduler().runTaskAsynchronously(BlitzSG.getInstance(), () -> BlitzSG.getInstance().getStatisticsManager().save(bsgPlayer));
        } else if (e.getInventory().getName() == "§8Auras") {
            e.setCancelled(true);
            if (bsgPlayer.isInGame())
                return;
            if (BlitzSG.getInstance().getCosmeticsManager().getAuraByName(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName())) == null) {
                return;
            }
            Aura aura = BlitzSG.getInstance().getCosmeticsManager().getAuraByName(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName()));

            if (bsgPlayer.getRank().getPosition() < aura.getRequiredRank().getPosition()) {
                p.sendMessage("§cYou must be " + aura.getRequiredRank().getRankFormatted() + " §cor higher to use that!");
                return;
            }
            p.sendMessage(ChatColor.GREEN + "You selected " + ChatColor.GOLD + aura.getName());
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

            bsgPlayer.setAura(aura);
            p.closeInventory();
            Bukkit.getScheduler().runTaskAsynchronously(BlitzSG.getInstance(), () -> BlitzSG.getInstance().getStatisticsManager().save(bsgPlayer));

        } else if (e.getInventory().getName() == "§8Taunts") {

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("hub");

            p.sendPluginMessage(BlitzSG.getInstance(), "BungeeCord", out.toByteArray());


            e.setCancelled(true);
            if (bsgPlayer.isInGame())
                return;
            if (BlitzSG.getInstance().getCosmeticsManager().getTauntByName(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName())) == null) {
                return;
            }
            Taunt aura = BlitzSG.getInstance().getCosmeticsManager().getTauntByName(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName()));

            if (bsgPlayer.getRank().getPosition() < aura.getRequiredRank().getPosition()) {
                p.sendMessage("§cYou must be " + aura.getRequiredRank().getRankFormatted() + " §cor higher to use that!");
                return;
            }
            if (bsgPlayer.getRank().getPosition() == 0 && aura.getRequiredRank().getPosition() == 0 && bsgPlayer.getTaunt() == null) {
                if (bsgPlayer.getCoins() < 2000) {
                    p.sendMessage("§cYou don't have enough coins to buy this!");
                    return;
                }
                bsgPlayer.removeCoins(2000);
                bsgPlayer.setTaunt(aura);
                p.sendMessage("§aYou unlocked the default taunt!");

                return;
            }
            p.sendMessage(ChatColor.GREEN + "You selected the " + ChatColor.GOLD + aura.getName() + " Taunt " + ChatColor.GREEN + "!");
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);

            bsgPlayer.setTaunt(aura);
            p.closeInventory();
            Bukkit.getScheduler().runTaskAsynchronously(BlitzSG.getInstance(), () -> BlitzSG.getInstance().getStatisticsManager().save(bsgPlayer));

        }
        if (e.getInventory().getName() == "§8Star Selector") {
            if (bsgPlayer.getGame().isDeathmatchStarting())
                if (bsgPlayer.getGame().getDeathmatchStartTime() >= 15) {
                    p.sendMessage(BlitzSG.CORE_NAME + ChatColor.RED + "The Blitz Star has been disabled!");
                    e.setCancelled(true);
                    return;
                }
            e.setCancelled(true);
            if (!bsgPlayer.isInGame())
                return;
            if (BlitzSG.getInstance().getStarManager().getStar(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName()) == null) {
                return;
            }
            Star star = BlitzSG.getInstance().getStarManager().getStar(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName()));
            if (star.getPrice() != 0 && !(bsgPlayer.getStars().contains(star))) {
                BlitzSG.send(p, "&cYou don't have this star unlocked!");
                return;
            }
            bsgPlayer.getGame().msgAll(BlitzSG.CORE_NAME + bsgPlayer.getRank(true).getChatColor() + p.getName() + " &6BLITZ! &e" + star.getName());
            p.closeInventory();
            star.run(p);
        }

    }

}
