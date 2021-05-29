package me.hardstyles.blitz.punishments;

import me.hardstyles.blitz.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class BannedPlayer {
    public String reason;
    public boolean isBanned;

    public BannedPlayer(Core core, UUID uuid) {
        try {
            Connection conn = core.getData().getConnection();
            String sql = "select * from bans;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            HashMap<String, String> ban = getBan(rs, uuid);
            if (ban != null && Boolean.parseBoolean(ban.get("banned"))) {
                reason = ChatColor.RED + "You're currently banned for " + ChatColor.WHITE + ban.get("reason") + ChatColor.RED + ".\n" + ChatColor.RED + "Expires in " + ChatColor.WHITE + formatDate(Long.parseLong(ban.get("expires")));
                isBanned = true;
            } else {
                isBanned = false;
            }

            rs.close();
            ps.close();
            conn.close();
            return;
        } catch (SQLException e) {
            reason = "Database Failure";
            isBanned = true;

        }

    }

    private HashMap<String, String> getBan(ResultSet rs, UUID uuid) {
        HashMap<String, String> data = new HashMap<>();
        try {
            while (rs.next()) {
                if (rs.getString("uuid").equalsIgnoreCase(uuid.toString())) {
                    if (rs.getDouble("expires") != -1) {
                        double expireDate = rs.getDouble("expires");
                        if (System.currentTimeMillis() > expireDate) {
                            //todo unban
                            data.put("banned", "false");
                            return data;
                        }
                        data.put("banned", "true");
                        data.put("reason", rs.getString("reason"));
                        data.put("executor", rs.getString("executor"));
                        data.put("expires", rs.getString("expires"));
                        return data;
                    }
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String formatDate(double milis) {
        Date start = new Date(System.currentTimeMillis()); // JANUARY_1_2007
        Date end = new Date((long) milis); // APRIL_1_2007
        long diffInSeconds = (end.getTime() - start.getTime()) / 1000;
        long diff[] = new long[]{0, 0, 0, 0};
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 24));
        return (String.format("%sd, %sh, %sm, %ss", diff[0], diff[1], diff[2], diff[3]));
    }

}
