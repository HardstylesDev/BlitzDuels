package me.hardstyles.blitz.punishments.redis;

import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.CompressionCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import me.hardstyles.blitz.Core;

@Getter
public class RedisManager {
    private final Core plugin;
    private final Gson GSON;
    RedisClient redisClient;
    StatefulRedisPubSubConnection<String, String> pubSubSender;
    StatefulRedisPubSubConnection<String, String> pubSubSubscriber;

    public RedisManager() {
        plugin = Core.i();
        GSON = new Gson();
        String host = plugin.getConfig().getString("redis.host");
        int port = plugin.getConfig().getInt("redis.port");
        String password = plugin.getConfig().getString("redis.password");

        RedisURI uri = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withPassword(password.toCharArray())
                .build();

        redisClient = RedisClient.create(uri);

        pubSubSender = redisClient.connectPubSub(CompressionCodec.valueCompressor(StringCodec.UTF8, CompressionCodec.CompressionType.DEFLATE));
        pubSubSubscriber = redisClient.connectPubSub(CompressionCodec.valueCompressor(StringCodec.UTF8, CompressionCodec.CompressionType.DEFLATE));

        pubSubSubscriber.sync().subscribe("PUNISHMENT");
    }

    public void shutdown() {
        redisClient.shutdown();
    }
}

