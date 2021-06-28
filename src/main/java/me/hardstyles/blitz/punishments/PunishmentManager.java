package me.hardstyles.blitz.punishments;

import com.google.common.collect.Lists;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import me.hardstyles.blitz.punishments.redis.PunishmentInfo;
import me.hardstyles.blitz.punishments.redis.RedisManager;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class PunishmentManager {
    private final ItemStack filler = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(3).name(" ").make();
    private final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm aa");
    private final Connection connection;
    private final RedisManager redisManager;

    public PunishmentManager(Core core) {
        this.connection = core.getData().getConnection();
        this.redisManager = core.getRedisManager();
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS sc_punishments(`id` INT(16) PRIMARY KEY AUTO_INCREMENT, `UUID` VARCHAR(36), `IP` VARCHAR(20), " +
                    "`type` VARCHAR(10), `removed` VARCHAR(16) DEFAULT NULL, `time` BIGINT(16), `length` BIGINT(16), `reason` VARCHAR(255), `executor` VARCHAR(16), `server` VARCHAR(32))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS sc_userdata(`UUID` VARCHAR(36) PRIMARY KEY, `IP` VARCHAR(20), `name` VARCHAR(16), `display` VARCHAR(32))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isIPBanned(String ip) {
        if (ip.equals("Unknown")) {
            return false;
        }

        try (PreparedStatement statement = connection.prepareStatement("SELECT UUID FROM sc_punishments WHERE type = \"IPBAN\" AND IP = ? AND removed IS NULL")) {
            statement.setString(1, ip);
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPunished(String name, PType type) {
        if (hasData(name)) {
            return isPunished(UUID.fromString(getUUID(name)), type);
        }
        return false;
    }

    public boolean isPunished(UUID uuid, PType type) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT time, length FROM sc_punishments WHERE UUID = ? AND type = ? AND removed IS NULL")) {
            statement.setString(1, uuid.toString());
            statement.setString(2, type.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (result.getLong("length") == -1) {
                    return true;
                } else if (result.getLong("time") + result.getLong("length") > System.currentTimeMillis()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void punish(PunishmentInfo info) {
        redisManager.getJedis().publish("PUNISHMENT", redisManager.getGSON().toJson(info));

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO sc_punishments(`UUID`, `IP`, `type`, `time`, " +
                "`length`, `reason`, `executor`, `server`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, getUUID(info.getPunished()));
            statement.setString(2, getIP(getUUID(info.getPunished())));
            statement.setString(3, info.getType().toString());
            statement.setString(4, String.valueOf(System.currentTimeMillis()));
            statement.setString(5, String.valueOf(info.getLength()));
            statement.setString(6, info.getReason());
            statement.setString(7, info.getExecutor());
            statement.setString(8, info.getServer());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(String uuid, PunishmentInfo info) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE sc_punishments SET `removed` = ? WHERE `UUID` = ? AND `type` = ? AND removed IS NULL")) {
            statement.setString(1, info.getExecutor());
            statement.setString(2, uuid);
            statement.setString(3, info.getType().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        redisManager.getJedis().publish("PUNISHMENT", redisManager.getGSON().toJson(info));
    }

    public String getIP(String uuid) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT IP FROM sc_userdata WHERE UUID = ?")) {
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("IP");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public String getUUID(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT UUID FROM sc_userdata WHERE LOWER(name) = ?")) {
            statement.setString(1, name.toLowerCase());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("UUID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public String getDisplay(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT display FROM sc_userdata WHERE UUID = ?")) {
            statement.setString(1, getUUID(name));
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("display");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    public void updateData(IPlayer p) {
        updateData(p.getName(),
                p.getIp(),
                p.getUuid(),
                p.getRank().getChatColor() + p.getName()
        );
    }

    public void updateData(String name, String ip, UUID uuid, String display) {
        if (hasData(uuid)) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE sc_userdata SET `name` = ?, `IP` = ?, `display` = ? WHERE UUID = ?")) {
                statement.setString(1, name);
                statement.setString(2, ip);
                statement.setString(3, display);
                statement.setString(4, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO sc_userdata(`UUID`, `IP`, `name`, `display`) VALUES(?, ?, ?, ?)")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, ip);
                statement.setString(3, name);
                statement.setString(4, display);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasData(String name) {
        return !getUUID(name).equals("Unknown");
    }

    public boolean hasData(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT UUID FROM sc_userdata WHERE UUID = ? LIMIT 1")) {
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Punishment> getPunishments(String uuid, PType type) {
        List<Punishment> punishments = Lists.newArrayList();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM sc_punishments WHERE UUID = ? AND type = ?")) {
            statement.setString(1, uuid);
            statement.setString(2, type.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Punishment punishment = new Punishment(
                        result.getInt("id"),
                        result.getString("uuid"), type,
                        result.getString("removed"),
                        result.getLong("time"),
                        result.getLong("length"),
                        result.getString("reason"),
                        result.getString("executor"),
                        result.getString("server")
                );
                punishments.add(punishment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }

    public Punishment getActiveBan(String name) {
        if (isIPBanned(getIP(getUUID(name)))) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM sc_punishments WHERE IP = ? AND removed IS NULL")) {
                statement.setString(1, getIP(getUUID(name)));
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    if (result.getString("type").contains("BAN")) {
                        return new Punishment(
                                result.getInt("id"),
                                result.getString("uuid"),
                                PType.IPBAN,
                                result.getString("removed"),
                                result.getLong("time"),
                                result.getLong("length"),
                                result.getString("reason"),
                                result.getString("executor"),
                                result.getString("server")
                        );
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (isPunished(name, PType.BAN)) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM sc_punishments WHERE UUID = ? AND removed IS NULL")) {
                statement.setString(1, getUUID(name));
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    if (result.getString("type").endsWith("BAN")) {
                        Punishment punishment = new Punishment(
                                result.getInt("id"),
                                result.getString("uuid"),
                                PType.BAN,
                                result.getString("removed"),
                                result.getLong("time"),
                                result.getLong("length"),
                                result.getString("reason"),
                                result.getString("executor"),
                                result.getString("server")
                        );
                        if (punishment.getLength() == -1) {
                            return punishment;
                        } else if (punishment.getTime() + punishment.getLength() > System.currentTimeMillis()) {
                            return punishment;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<String> getAccounts(String ip) {
        List<String> alts = Lists.newArrayList();
        try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM sc_userdata WHERE IP = ?")) {
            statement.setString(1, ip);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                alts.add(result.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alts;
    }

    public String getName(String uuid) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM sc_userdata WHERE UUID = ?")) {
            statement.setString(1, uuid);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deletePunishment(int id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM sc_punishments WHERE id = ?")) {
            statement.setString(1, String.valueOf(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String formatMillis(long millis) {
        long seconds = millis / 1000;
        long days = 0, hours = 0, minutes = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        while (hours >= 24) {
            hours -= 24;
            days++;
        }

        String format = pluralize(days, "Day", ", ")
                + pluralize(hours, "Hour", ", ")
                + pluralize(minutes, "Minute", ", ")
                + pluralize(seconds, "Second", ", ");
        return format.isEmpty() ? "" : format.substring(0, format.length() - 2);
    }

    private String pluralize(long amount, String name, String... extra) {
        StringBuilder format = new StringBuilder();
        if (amount == 1) {
            format.append(amount).append(" ").append(name);
        } else {
            format.append(amount > 0 ? amount + " " + name + "s" : "");
        }
        if (!format.toString().isEmpty()) {
            for (String s : extra) {
                format.append(s);
            }
        }
        return format.toString();
    }

    public Inventory getHistoryGui(String target, String type) {
        Inventory inv = Bukkit.createInventory(null, 54, "&e" + type + ": &6" + target);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, filler);
            inv.setItem(i + 45, filler);
        }
        List<Punishment> punishments = getPunishments(getUUID(target), type.equals("Mutes") ? PType.MUTE : (type.equals("Bans") ? PType.BAN : PType.IPBAN));
        for (int i = 0; i < Math.min(punishments.size(), 36); i++) {
            Punishment punishment = punishments.get(i);
            boolean inactive = (punishment.getLength() != -1 && System.currentTimeMillis() > punishment.getTime() + punishment.getLength()) || punishment.getRemoved() != null;
            ItemStack item = new ItemBuilder(Material.EMPTY_MAP).name("§6Punishment: §c#" + punishment.getId())
                    .lore("§7&m---------------------------------")
                    .lore("§8» §bStatus: " + (inactive ? "§cInactive" : "§aActive"))
                    .lore("§8» §bPunished By: §7" + punishment.getExecutor())
                    .lore("§8» §bRemoved By: " + (punishment.getRemoved() == null ? "§cN/A" : "§7" + punishment.getRemoved()))
                    .lore("§8» §bDate Of: §7" + dateFormat.format(new Date(punishment.getTime())) + " (EST)")
                    .lore("§8» §bLength: §7" + (punishment.getLength() == -1 ? "Permanent" : formatMillis(punishment.getLength())))
                    .lore("§8» §bReason: §7" + punishment.getReason())
                    .lore("§8» §bServer: §7" + punishment.getServer())
                    .lore(" ")
                    .lore("§a§lClick to remove this history")
                    .lore("§7§m---------------------------------").make();
            inv.setItem(i + 9, item);
        }
        inv.setItem(49, new ItemBuilder(Material.NETHER_STAR).name("&7» &a&lExit &7«").make());
        return inv;
    }

    public long parseDate(String input) throws NumberFormatException {
        long amount = Integer.parseInt(input.substring(0, input.length() - 1));
        switch (input.toCharArray()[input.length() - 1]) {
            case 'y': {
                return amount * 31536000000L;
            }
            case 'M': {
                return amount * 2592000000L;
            }
            case 'w': {
                return amount * 604800000L;
            }
            case 'd': {
                return amount * 86400000L;
            }
            case 'h': {
                return amount * 3600000L;
            }
            case 'm': {
                return amount * 60000L;
            }
            case 's': {
                return amount * 1000L;
            }
            default: {
                throw new NumberFormatException("Did not follow format.");
            }
        }
    }
}
