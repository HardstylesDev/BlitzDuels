package me.hardstyles.blitz.punishments.redis;

import com.google.gson.Gson;
import lombok.Getter;
import me.hardstyles.blitz.Core;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Getter
public class RedisManager {
    private final Core plugin = Core.i();
    private final Gson GSON = new Gson();
    Jedis jedis = new Jedis("127.0.0.1");
    JedisPubSub pubSub;

    public RedisManager() {
        pubSub = new RedisListener(this);
        jedis.subscribe(pubSub, "PUNISHMENT");
    }

    public void shutdown() {
        jedis.shutdown();
    }
}

