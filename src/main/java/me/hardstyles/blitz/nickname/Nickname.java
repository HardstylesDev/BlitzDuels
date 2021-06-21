package me.hardstyles.blitz.nickname;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.player.IPlayer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.UUID;

public class Nickname {
    public void setNick(Player p, String s) {
        setNick(p, s, false);
    }
    public void setNick(Player p, String s, boolean onJoin) {
        if (!onJoin) {
            String skin[] = prepareSkinTextures(p, s);
            System.out.println(skin.length + " >>> " + skin[0]);

            GameProfile gameProfile = ((CraftPlayer) p).getProfile();
            CraftPlayer craftOfflinePlayer = (CraftPlayer) Bukkit.getOfflinePlayer(p.getUniqueId());

            MinecraftServer server = MinecraftServer.getServer();
            WorldServer world = server.getWorldServer(0);
            PlayerInteractManager manager = new PlayerInteractManager(world);
            EntityPlayer player = new EntityPlayer(server, world, gameProfile, manager);

            EntityPlayer offlineplayer = new EntityPlayer(server, world, craftOfflinePlayer.getProfile(), manager);
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, player);
            p.kickPlayer(ChatColor.GREEN + "Nickname changed, please rejoin!");

            IPlayer bsgPlayer = Core.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
            if(bsgPlayer.getNick() == null){
                bsgPlayer.setNick(new Nick(s, null, null, true));
            }
            bsgPlayer.getNick().setNickName(s);
            bsgPlayer.getNick().setNicked(true);
            bsgPlayer.getNick().setSkinValue(skin[0]);
            bsgPlayer.getNick().setSkinSignature(skin[1]);
            Core.getInstance().getStatisticsManager().save(bsgPlayer);
            return;
        }
        IPlayer bsgPlayer = Core.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

        if (bsgPlayer.getNick().getSkinSignature() == null) return;
        setSkinForSelf(p);
        refresh(p);
        setPlayerNameTag(p, s);
        setPlayerSkin(p, s);
        //removeOfflinePlayer(p.getDisplayName());


        //bsgPlayer.setNickName(s);
        p.setPlayerListName(p.getName());

    }
    public void removeOfflinePlayer(String realIGN) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(realIGN);
        GameProfile gameProfile = ((CraftPlayer) offlinePlayer).getProfile();
        CraftPlayer craftOfflinePlayer = (CraftPlayer) Bukkit.getOfflinePlayer(offlinePlayer.getUniqueId());

        MinecraftServer server = MinecraftServer.getServer();
        WorldServer world = server.getWorldServer(0);
        PlayerInteractManager manager = new PlayerInteractManager(world);
        EntityPlayer player = new EntityPlayer(server, world, gameProfile, manager);

        EntityPlayer offlineplayer = new EntityPlayer(server, world, craftOfflinePlayer.getProfile(), manager);

        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (p2 == offlinePlayer)
                continue;
            System.out.println("attempting to remove " + gameProfile.getName() + " for " + p2.getName());
            ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player.getBukkitEntity()).getHandle()));
            ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) offlineplayer.getBukkitEntity()).getHandle()));
            ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getBukkitEntity().getEntityId()));
            ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(offlineplayer.getBukkitEntity().getEntityId()));
        }
    }

    public void unnick(Player p) {
        IPlayer bsgPlayer = Core.getInstance().getPlayerManager().getPlayer(p.getUniqueId());


        bsgPlayer.setNick(null);
        p.kickPlayer(ChatColor.GREEN + "Please rejoin");
        Core.getInstance().getStatisticsManager().save(bsgPlayer);

    }

    public void setPlayerNameTag(Player player, String name) {
        setPlayerNameTag(player, name, false);
    }

    public void setPlayerNameTag(Player player, String name, Boolean unnicking) {
        Nick nick = Core.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getNick();

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
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
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
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String[] prepareSkinTextures(Player p, String arg) {
        OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(arg);
        if (op.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
            IPlayer bsgPlayer = Core.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
            String skin[] = new String[]{bsgPlayer.getNick().getSkinValue(), bsgPlayer.getNick().getSkinSignature()};
            return skin;
        }
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.mineskin.org/generate/user/" + op.getUniqueId()).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                if (reply.contains("\"error\""))
                    return null;
                String value = reply.split("\"value\":\"")[1].split("\"")[0];
                String signature = reply.split("\"signature\":\"")[1].split("\"")[0];


                String skin[] = new String[]{value, signature};
                return skin;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public boolean setPlayerSkin(Player p, String arg) {
        Nick nick = Core.getInstance().getPlayerManager().getPlayer(p.getUniqueId()).getNick();
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
                    if (!player.equals(p))
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle()));
                    if (player.equals(p)) ;

                });
        return true;

    }

    public boolean setSkinForSelf(Player p) {
        Nick nick = Core.getInstance().getPlayerManager().getPlayer(p.getUniqueId()).getNick();

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

    public String fetchRealName(Player p) {
        try {
            System.out.println("the link: " + "https://api.minetools.eu/uuid/" + ("" + p.getUniqueId()).replaceAll("-", ""));

            URL url = new URL("https://api.minetools.eu/uuid/" + ("" + p.getUniqueId()).replaceAll("-", ""));
            URLConnection request = url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            String zipcode = rootobj.get("zip_code").getAsString(); //just grab the zipcode
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        return null;
    }
}


