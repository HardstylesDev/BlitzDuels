package me.hardstyles.blitz.punishments.redis;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.punishments.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

public class RedisListener extends JedisPubSub {
    private final RedisManager manager;
    private final PunishmentManager punishmentManager;

    public RedisListener(RedisManager manager) {
        this.manager = manager;
        punishmentManager = Core.i().getPunishmentManager();
    }

    @Override
    public void onMessage(String channel, String json) {
        if (channel.equals("PUNISHMENT")) {
            handlePunishment(manager.getGSON().fromJson(json, PunishmentInfo.class));
        }
    }

    private void handlePunishment(PunishmentInfo punishment) {
        switch (punishment.getType()) {
            case KICK: {
                if (Bukkit.getPlayerExact(punishment.getPunished()) != null) {
                    Bukkit.getPlayerExact(punishment.getPunished()).kickPlayer(
                            "§cYou have been Kicked.\n \n§bReason: §7" + punishment.getReason());
                }

                for (IPlayer iPlayer : Core.i().getPlayerManager().getPlayers().values()) {
                    if (iPlayer.getRank().getPosition() >= 6) {
                        Player p = Bukkit.getPlayer(iPlayer.getUuid());
                        if (p == null) {
                            continue;
                        }
                        p.sendMessage("§7§m------------------------------------------");
                        p.sendMessage("§8[§3§lStaff Alert§8] " + punishment.getExecutorDisplay() + " §ekicked " +
                                punishment.getPunishedDisplay());
                        p.sendMessage("");
                        p.sendMessage("§8» §bReason: §7" + punishment.getReason());
                        p.sendMessage("§8» §bServer: §7" + punishment.getServer());
                        p.sendMessage("§7§m------------------------------------------");
                    }
                }
                break;
            }
            case BAN: {
                if (punishment.isRemoval()) {
                    for (IPlayer iPlayer : Core.i().getPlayerManager().getPlayers().values()) {
                        if (iPlayer.getRank().getPosition() >= 6) {
                            Player p = Bukkit.getPlayer(iPlayer.getUuid());
                            if (p == null) {
                                continue;
                            }
                            p.sendMessage("§7§m------------------------------------------");
                            p.sendMessage("§8[§3§lStaff Alert§8] " + punishment.getExecutorDisplay() + " §eunbanned " +
                                    punishment.getPunishedDisplay());
                            p.sendMessage("");
                            p.sendMessage("§8» §bReason: §7" + punishment.getReason());
                            p.sendMessage("§8» §bServer: §7" + punishment.getServer());
                            p.sendMessage("§7§m------------------------------------------");
                        }
                    }
                } else {
                    boolean perm = punishment.getLength() == -1;
                    if (Bukkit.getPlayerExact(punishment.getPunished()) != null) {
                        Bukkit.getPlayerExact(punishment.getPunished()).kickPlayer(
                                "§cYou are currently Banned.\n \n§bReason: §7" + punishment.getReason() + "\n§bDuration: §7" + (perm ? "Permanent" : punishmentManager.formatMillis(punishment.getLength())));
                    }

                    for (IPlayer iPlayer : Core.i().getPlayerManager().getPlayers().values()) {
                        if (iPlayer.getRank().getPosition() >= 6) {
                            Player p = Bukkit.getPlayer(iPlayer.getUuid());
                            if (p == null) {
                                continue;
                            }
                            p.sendMessage("§7§m------------------------------------------");
                            p.sendMessage("§8[§3§lStaff Alert§8] " + punishment.getExecutorDisplay() + (perm ? " §ebanned " : " §etemporarily banned ") +
                                    punishment.getPunishedDisplay());
                            p.sendMessage("");
                            p.sendMessage("§8» §bDuration: §7" + (perm ? "Permanent" : punishmentManager.formatMillis(punishment.getLength())));
                            p.sendMessage("§8» §bReason: §7" + punishment.getReason());
                            p.sendMessage("§8» §bServer: §7" + punishment.getServer());
                            p.sendMessage("§7§m------------------------------------------");
                        }
                    }
                }
                break;
            }
            case IPBAN: {
                if (punishment.isRemoval()) {
                    for (IPlayer iPlayer : Core.i().getPlayerManager().getPlayers().values()) {
                        if (iPlayer.getRank().getPosition() >= 6) {
                            Player p = Bukkit.getPlayer(iPlayer.getUuid());
                            p.sendMessage("§7§m------------------------------------------");
                            p.sendMessage("§8[§3§lStaff Alert§8] " + punishment.getExecutorDisplay() + " §eipunbanned " + punishment.getPunishedDisplay());
                            p.sendMessage("");
                            p.sendMessage("§8» §bReason: §7" + punishment.getReason());
                            p.sendMessage("§8» §bServer: §7" + punishment.getServer());
                            p.sendMessage("§7§m------------------------------------------");
                        }
                    }
                } else {
                    String punished = punishment.getPunished();
                    String uuid = punishmentManager.getUUID(punished);
                    String ip = punishmentManager.getIP(uuid);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (punishmentManager.getIP(p.getUniqueId().toString()).equals(ip)) {
                            p.kickPlayer(
                                    "§cYou are currently IP-Banned.\n \n§bReason: §7" + punishment.getReason());
                        }
                    }

                    for (IPlayer iPlayer : Core.i().getPlayerManager().getPlayers().values()) {
                        if (iPlayer.getRank().getPosition() >= 6) {
                            Player p = Bukkit.getPlayer(iPlayer.getUuid());
                            if (p == null) {
                                continue;
                            }
                            p.sendMessage("§7§m------------------------------------------");
                            p.sendMessage("§8[§3§lStaff Alert§8] " + punishment.getExecutorDisplay() +  " §eipbanned " +
                                    punishment.getPunishedDisplay());
                            p.sendMessage("");
                            p.sendMessage("§8» §bDuration: §7Permanent");
                            p.sendMessage("§8» §bReason: §7" + punishment.getReason());
                            p.sendMessage("§8» §bServer: §7" + punishment.getServer());
                            p.sendMessage("§7§m------------------------------------------");
                        }
                    }
                }
                break;
            }
            case MUTE: {
                Player target = Bukkit.getPlayerExact(punishment.getPunished());
                if (punishment.isRemoval()) {
                    if (target != null) {
                        target.sendMessage("§aYou have been unmuted.");
                    }

                    for (IPlayer iPlayer : Core.i().getPlayerManager().getPlayers().values()) {
                        if (iPlayer.getRank().getPosition() >= 6) {
                            Player p = Bukkit.getPlayer(iPlayer.getUuid());
                            if (p == null) {
                                continue;
                            }
                            p.sendMessage("§7§m------------------------------------------");
                            p.sendMessage("§8[§3§lStaff Alert§8] " + punishment.getExecutorDisplay() + " §eunmuted " +
                                    punishment.getPunishedDisplay());
                            p.sendMessage("");
                            p.sendMessage("§8» §bReason: §7" + punishment.getReason());
                            p.sendMessage("§8» §bServer: §7" + punishment.getServer());
                            p.sendMessage("§7§m------------------------------------------");
                        }
                    }
                } else {
                    boolean perm = punishment.getLength() == -1;
                    if (target != null) {
                        target.sendMessage(" ");
                        target.sendMessage("§cYou have been muted! §8(§7Reason: §f" + punishment.getReason() + " §8| §7Duration: §f" + (perm ? "Permanent" : punishmentManager.formatMillis(punishment.getLength())) + "§8)");
                        target.sendMessage(" ");
                    }

                    for (IPlayer iPlayer : Core.i().getPlayerManager().getPlayers().values()) {
                        if (iPlayer.getRank().getPosition() >= 6) {
                            Player p = Bukkit.getPlayer(iPlayer.getUuid());
                            if (p == null) {
                                continue;
                            }
                            p.sendMessage("§7§m------------------------------------------");
                            p.sendMessage("§8[§3§lStaff Alert§8] " + punishment.getExecutorDisplay() +
                                    (perm ? " §emuted " : " §etemporarily muted ") + punishment.getPunishedDisplay());
                            p.sendMessage("");
                            p.sendMessage("§8» §bDuration: §7" + (perm ? "Permanent" : punishmentManager.formatMillis(punishment.getLength())));
                            p.sendMessage("§8» §bReason: §7" + punishment.getReason());
                            p.sendMessage("§8» §bServer: §7" + punishment.getServer());
                            p.sendMessage("§7§m------------------------------------------");
                        }
                    }
                }
                break;
            }
        }
    }
}
