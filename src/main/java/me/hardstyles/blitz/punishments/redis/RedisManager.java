package me.hardstyles.blitz.punishments.redis;

import com.google.gson.Gson;
import lombok.Getter;
import me.hardstyles.blitz.Core;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

@Getter
public class RedisManager {
    private final Core plugin = Core.i();
    private final Gson GSON = new Gson();
    Jedis subJedis, pubJedis;

    public RedisManager() {
        String host = Core.i().getConfig().getString("redis.host");
        int port = Core.i().getConfig().getInt("redis.port");
        String password = Core.i().getConfig().getString("redis.password");

        subJedis = new Jedis(host, port);
        subJedis.auth(password);
        pubJedis = new Jedis(host, port);
        pubJedis.auth(password);
        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), ()-> subJedis.subscribe(new RedisListener(this), "PUNISHMENT"));
    }

    public void shutdown() {
        subJedis.shutdown();
    }
}

