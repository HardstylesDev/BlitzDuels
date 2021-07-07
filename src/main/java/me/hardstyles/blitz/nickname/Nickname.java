package me.hardstyles.blitz.nickname;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

public class Nickname {
    public void setNick(Player p, String s) {
        setNick(p, s, false);
    }

    public void setNick(Player p, String s, boolean onJoin) {
        if (!onJoin) {
            String[] skin = prepareSkinTextures(p, s);
            p.kickPlayer(ChatColor.GREEN + "Nick > " + ChatColor.YELLOW+ "Setting nick, please relog!");

            IPlayer bsgPlayer = Core.i().getPlayerManager().getPlayer(p.getUniqueId());
            if (bsgPlayer.getNick() == null) {
                bsgPlayer.setNick(new Nick(s, null, null, true));
            }
            bsgPlayer.getNick().setNickName(s);
            bsgPlayer.getNick().setNicked(true);
            bsgPlayer.getNick().setSkinValue(skin[0]);
            bsgPlayer.getNick().setSkinSignature(skin[1]);
            Core.i().getStatisticsManager().save(bsgPlayer);
            return;
        }
        IPlayer bsgPlayer = Core.i().getPlayerManager().getPlayer(p.getUniqueId());

        if (bsgPlayer.getNick().getSkinSignature() == null) return;
        setSkinForSelf(p);
        refresh(p);
        setPlayerNameTag(p, s);
        setPlayerSkin(p, s);

        p.setPlayerListName(p.getName());

    }

    public void unnick(Player p) {
        IPlayer bsgPlayer = Core.i().getPlayerManager().getPlayer(p.getUniqueId());


        bsgPlayer.setNick(null);
        p.kickPlayer(ChatColor.GREEN + "Please rejoin");
        Core.i().getStatisticsManager().save(bsgPlayer);

    }

    public void setPlayerNameTag(Player player, String name) {
        setPlayerNameTag(player, name, false);
    }

    public void setPlayerNameTag(Player player, String name, Boolean unnicking) {
        Nick nick = Core.i().getPlayerManager().getPlayer(player.getUniqueId()).getNick();

        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            boolean gameProfileExists = false;
            try {
                Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {

            }
            try {
                Class.forName("com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {

            }
            for (Player p : Bukkit.getOnlinePlayers())
                if (!p.equals(player))
                    p.hidePlayer(player);
            if (!gameProfileExists) {
                Field nameField = entityPlayer.getClass().getSuperclass().getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(entityPlayer, name);

                Field uuidField = entityPlayer.getClass().getSuperclass().getDeclaredField("id");
                uuidField.setAccessible(true);
                uuidField.set(entityPlayer, UUID.randomUUID());


            } else {
                Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
                Field ff = profile.getClass().getDeclaredField("name");
                ff.setAccessible(true);
                ff.set(profile, name);
                Field ffu = profile.getClass().getDeclaredField("id");
                ffu.setAccessible(true);
                ffu.set(profile, UUID.randomUUID());
            }
            if (Bukkit.class.getMethod("getOnlinePlayers").getReturnType() == Collection.class) {
                Collection<? extends Player> players = (Collection<? extends Player>) Bukkit.class.getMethod("getOnlinePlayers").invoke(null);
                for (Player p : players) {
                    p.hidePlayer(player);
                    p.showPlayer(player);

                    //if (!p.equals(player))
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
                    CraftPlayer cp = (CraftPlayer) player;
                    GameProfile gameProfile = cp.getHandle().getProfile();

                    gameProfile.getProperties().removeAll("textures");
                    gameProfile.getProperties().put("textures", new Property("textures", nick.getSkinValue(), nick.getSkinSignature()));

                    if (!p.equals(player))
                        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                    if (!p.equals(player))
                        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle()));

                }
            } else {
                Player[] players = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers").invoke(null));
                for (Player p : players) {
                    p.hidePlayer(player);
                    p.showPlayer(player);


                    if (!p.equals(player))
                        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
                    CraftPlayer cp = (CraftPlayer) player;
                    GameProfile gameProfile = cp.getHandle().getProfile();

                    gameProfile.getProperties().removeAll("textures");
                    gameProfile.getProperties().put("textures", new Property("textures", nick.getSkinValue(), nick.getSkinSignature()));


                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                    if (!p.equals(player))
                        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle()));


                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String[] prepareSkinTextures(Player p, String arg) {
        OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(arg);
        if (op.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
            IPlayer bsgPlayer = Core.i().getPlayerManager().getPlayer(p.getUniqueId());
            return new String[]{bsgPlayer.getNick().getSkinValue(), bsgPlayer.getNick().getSkinSignature()};
        }
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.mineskin.org/generate/user/" + op.getUniqueId()).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                if (reply.contains("\"error\""))
                    return null;
                String value = reply.split("\"value\":\"")[1].split("\"")[0];
                String signature = reply.split("\"signature\":\"")[1].split("\"")[0];


                return new String[]{value, signature};
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public boolean setPlayerSkin(Player p, String arg) {
        Nick nick = Core.i().getPlayerManager().getPlayer(p.getUniqueId()).getNick();
        OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(arg);
        if (!op.getName().equalsIgnoreCase(arg)) {
            return false;
        } else if (nick.getSkinSignature() == null) {
            Bukkit.broadcastMessage("skinSig = null");
            return false;
        }

        p.sendMessage("Setting your nick to " + arg);


        Bukkit.getOnlinePlayers().

                forEach(player ->

                {
                    if (!player.equals(p))
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle()));
                    CraftPlayer cp = (CraftPlayer) p;
                    GameProfile gameProfile = cp.getHandle().getProfile();

                    gameProfile.getProperties().removeAll("textures");
                    gameProfile.getProperties().put("textures", new Property("textures", nick.getSkinValue(), nick.getSkinSignature()));


                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle()));
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(p.getEntityId()));
                    if (!player.equals(p)) {
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle()));
                    }
                });
        return true;

    }

    public boolean setSkinForSelf(Player p) {
        Nick nick = Core.i().getPlayerManager().getPlayer(p.getUniqueId()).getNick();

        CraftPlayer cp = (CraftPlayer) p;
        GameProfile gameProfile = cp.getHandle().getProfile();

        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", nick.getSkinValue(), nick.getSkinSignature()));

        return true;


    }


    public void refresh(Player p) {

        final EntityPlayer ep = ((CraftPlayer) p).getHandle();
        final PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        final PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        // final Location loc = BlitzSG.getInstance().getGameManager().getAvailableGame().getArena().getLobby().clone();
        ep.playerConnection.sendPacket(removeInfo);
        ep.playerConnection.sendPacket(addInfo);

    }
}


