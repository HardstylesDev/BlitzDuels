package me.hardstyles.blitz.nickname;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.nickname.skin.Skin;
import me.hardstyles.blitz.rank.ranks.Default;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;

public class NicknameCommand implements CommandExecutor {
    Skin skin = new Skin("ewogICJ0aW1lc3RhbXAiIDogMTYyMDUwODI3NTgwMCwKICAicHJvZmlsZUlkIiA6ICIzODJjNjk3YjUwYTg0NTNlOGNhYWM0YjllNWY5NWM1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJjb21wbGlzaGVyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzEyMTNlNjFkMmUwMjZmZWM5ZmNiOGNjMzE1ZGZhY2ExNTk1NjFjNWUxZGNiZjJkN2M2ZDcwMDQxMWRmOGY5NTMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "foYvf3QEdTe3OZqK57Oz9KGST1Z+dD1JbCY6ZbGWejcbJvNp5X4DgXMPKPUYqhWNg2AiNFeUYJYRp5CqRjbL+GZzdLm/B3fYiwe+/junPeCb/NzOz7EK5nHRM0WzB+7HNNNSI18zq3RxpCWieXCg1JmYq506+hPPgFYevNAZhX8+oKRffmQK4qvvAow7jaEblLKT6w4yPRQNFEM8lC4un34q11Cer5d0/eKzZb6bVl0fQiS4PCI3hk0NaUpwA+eJdr7+M0E26EBB6GuAe23IDEkUpNvjI3WWaxgMLfLWa9LrJedn1Rr0v9X7vKRbdAIL1z0fVv7Ub8W3NkXAbOPwglpIQC4x5nsKKhjAUXZLzKfe1e8nme9xdQgMt8mDf/cFgAGEaohhzHWHIFAhc8V0OEYXyquh5/WhN1/2tZkvjUFdceO82NFI8CpKtw6oAYngPs9DZUGFdlPTsTj1Jb9gyyKcGjX3vSJdiQGLthD5oCWEXwwz3J6bSe2LNXStYzUheIKNWfpstyucsGpSf7ugXtOotikCEEb5o5jBBASKy8gRW75zXojezXqc4u+SzURyFisXc8/fpuIPRjFb/TkJuPA5Qyj+GX9GzU6+Qh1CnkcuUCLlcrJ4vd4eZfGvxk+Op0Gu/hVpuwAVUreMxjrwpQYtSlI4CIlDy1rLRLTk6LU=");

    final private Core core;

    public NicknameCommand(Core core) {
        this.core = core;
    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 0){
            sender.sendMessage(ChatColor.RED + "/nick <name>");
        }
        if (args[0].equalsIgnoreCase("test")) {


           setNameTag((Player) sender, args[1]);
            return true;
        }
        if (args[0].equalsIgnoreCase("spoof")) {


            sendPlayerToServer((Player) sender, "mc.hypixel.net");

            return true;
        }
        if ((Core.getInstance().getRankManager().getRank((Player) sender) instanceof Default)) {
            sender.sendMessage(Core.CORE_NAME + "missing permission.");

            return true;
        }
        if (alias.equalsIgnoreCase("unnick")) {
            Core.send((Player) sender, "&eYou are no longer nicked!");
            new Nickname().unnick((Player) sender);
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("unnick") || args[0].equalsIgnoreCase("reset")) {
                Core.send(p, "&eYou are no longer nicked");
                core.getServer().getScheduler().runTaskAsynchronously(core, ()-> new Nickname().unnick(p));

                return true;
            }
            Core.send(p, "&eYour nickname has been set to &e" + args[0]);


            core.getServer().getScheduler().runTaskAsynchronously(core, ()->  new Nickname().setNick(p, args[0]));


            return true;
        }
        p.sendMessage(Core.CORE_NAME + "&e");
        return true;
    }

    private void setSkin(Player p, Skin skin) {
        int r = (int) (Math.random() * 5);
        String name = new String[]{"Horse", "Sheep", "Wolf", "Cow", "Chicken"}[r];
        skin = getSkinFromName(name);
        CraftPlayer cp = (CraftPlayer) p;
        GameProfile gameProfile = cp.getHandle().getProfile();

        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", skin.getSkinValue(), skin.getSkinSignature()));
        Packet remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle());

        Packet add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle());

        Packet destroy = new PacketPlayOutEntityDestroy(p.getEntityId());

        Packet spawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle());

        Packet respawn = respawnPacket(p);


        sendPacketExcluding(p, remove);

        sendPacket(add);
        setNameTag(p, name);
        sendPacket(destroy);
//
        sendPacketExcluding(p, spawn);
//
        sendPacket(p, respawn);



    }

    private void sendPacketExcluding(Player exclude, Packet p) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == exclude) continue;
            ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(p);
        }
    }

    private void sendPacket(Player player, Packet p) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(p);
    }

    private void sendPacket(Packet p) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(p);
        }
    }

    private Skin getSkinFromName(String arg) {
        OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(arg);
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.mineskin.org/generate/user/" + op.getUniqueId()).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                if (reply.contains("\"error\""))
                    return null;
                String value = reply.split("\"value\":\"")[1].split("\"")[0];
                String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
                return new Skin(value, signature);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    private Packet respawnPacket(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        World world = ep.world;
        EnumDifficulty difficulty = ep.world.getDifficulty();
        WorldData worlddata = ep.world.worldData;
        net.minecraft.server.v1_8_R3.WorldType worldtype = worlddata.getType();
        int dimension = world.worldProvider.getDimension();
        PlayerInteractManager playerIntManager = ep.playerInteractManager;
        WorldSettings.EnumGamemode enumGamemode = playerIntManager.getGameMode();


        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(dimension, difficulty, worldtype, enumGamemode);

        return respawn;
    }

    private void setNameTag(Player player, String name) {
        try {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hidePlayer(player);
            }
            GameProfile profile = ((CraftPlayer) player).getProfile();

            Field ff = profile.getClass().getDeclaredField("name");
            ff.setAccessible(true);
            ff.set(profile, name);

            Field ffu = profile.getClass().getDeclaredField("id");
            ffu.setAccessible(true);
            ffu.set(profile, UUID.randomUUID());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Celmic Network&8] &fThere was an problem connecting to " + server + "!"));
            return;
        }

        player.sendPluginMessage(Core.getInstance(), "BungeeCord", b.toByteArray());
    }


}

