package me.hardstyles.blitz.punishments;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PunishmentHandler implements Listener {
    private final ItemStack filler = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(3).name(" ").make();
    private final PunishmentManager punishmentManager;

    public PunishmentHandler(Core core) {
        punishmentManager = core.getPunishmentManager();
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent e) {
        String display = punishmentManager.getDisplay(e.getName());
        punishmentManager.updateData(e.getName(), e.getAddress().getHostAddress(), e.getUniqueId(), display);

        Punishment punishment = punishmentManager.getActiveBan(e.getName());

        if (punishment == null) return;

        if (punishmentManager.isIPBanned(e.getAddress().getHostAddress())) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            e.setKickMessage(
                    "\n " +
                    "\n§cYou are currently IP-Banned." +
                    "\n " +
                    "\n§bReason: §7" + punishment.getReason()
            );
        } else if (punishmentManager.isPunished(e.getUniqueId(), PType.BAN)) {
            boolean perm = punishment.getLength() == -1;
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            e.setKickMessage(
                    "\n " +
                    "\n§cYou are currently Banned." +
                    "\n " +
                    "\n§bReason: §7" + punishment.getReason() +
                    "\n§bDuration: §7" + (perm ? "Permanent" : punishmentManager.formatMillis(punishment.getTime() + punishment.getLength() - System.currentTimeMillis()))
            );
        }
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;

        Player p = (Player) e.getWhoClicked();

        if (e.getView().getTitle().startsWith("§eHistory: §6")) {
            String target = e.getView().getTitle().substring(13);
            e.setCancelled(true);
            String type = "";
            if (e.getRawSlot() == 12) {
                type = "Mutes";
            } else if (e.getRawSlot() == 13) {
                type = "Bans";
            } else if (e.getRawSlot() == 14) {
                type = "IP Bans";
            } else if (e.getRawSlot() == 31) {
                e.getView().close();
            }
            if (!type.isEmpty()) {
                p.openInventory(punishmentManager.getHistoryGui(target, type));
            }
        } else if (e.getView().getTitle().startsWith("§eMutes: §6") || e.getView().getTitle().startsWith("§eBans: §6")
                || e.getView().getTitle().startsWith("§eIP Bans: §6")) {
            e.setCancelled(true);
            String target = e.getView().getTitle().split(ChatColor.GOLD.toString())[1];
            if (e.getRawSlot() == 49) {
                Inventory inv = Bukkit.createInventory(null, 36, "§eHistory: §6" + target);
                for (int i = 0; i < 36; i++) {
                    inv.setItem(i, filler);
                }
                inv.setItem(12, new ItemBuilder(Material.BOOK).name("§6§lMutes").make());
                inv.setItem(13, new ItemBuilder(Material.BOOK).name("§6§lBans").make());
                inv.setItem(14, new ItemBuilder(Material.BOOK).name("§6§lIP Bans").make());
                inv.setItem(31, new ItemBuilder(Material.NETHER_STAR).name("§7» §a§lExit §7«").make());
                p.openInventory(inv);
            } else if (e.getCurrentItem().getType() == Material.EMPTY_MAP) {
                if (p.hasPermission("core.history.remove")) {
                    int id = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName().substring(17));
                    punishmentManager.deletePunishment(id);
                    p.sendMessage(String.format("§7Successfully removed punishment §c#%s§7!", id));
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1f, 2f);
                    String type = e.getView().getTitle().substring(2).split(":")[0];
                    p.openInventory(punishmentManager.getHistoryGui(target, type));
                } else {
                    p.sendMessage("§cYou don't have permission to delete history.");
                }
            }
        }
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent e) {
        if (punishmentManager.isPunished(e.getPlayer().getUniqueId(), PType.MUTE)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cYou are currently muted.");
        }
    }
}
