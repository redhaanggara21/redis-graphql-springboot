package com.spring.graphql.config;

import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.graphql.model.GraphqlObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Sinks;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Config {

    @NonNull private final RedisConfig redisConfig;

    @Bean
    ConcurrentHashMap<String, Sinks.Many<GraphqlObject>> sinks() {
        return new ConcurrentHashMap<>();
    }

    @Bean("redisSubReactiveCommands")
    RedisPubSubReactiveCommands<String, GraphqlObject> redisSubReactiveCommands() {

        return RedisClient.create(redisUri(redisConfig))
            .connectPubSub(new GraphqlRedisCodec())
            .reactive();
    }

    @Bean("redisPubReactiveCommands")
    RedisPubSubReactiveCommands<String, GraphqlObject> redisPubReactiveCommands() {

        return RedisClient.create(redisUri(redisConfig))
            .connectPubSub(new GraphqlRedisCodec())
            .reactive();
    }

    private RedisURI redisUri(final RedisConfig config) {

        log.info(
            "redisURI: server = {}, port = {}, password length = {}",
            config.getServer(),
            config.getPort(),
            isNull(config.getPassword()) ? null : config.getPassword().length());

        return RedisURI.Builder.redis(config.getServer())
            .withPassword(config.getPassword().toCharArray())
            .withPort(config.getPort())
            .withSsl(false)
            .build();
    }

    private static class GraphqlRedisCodec implements RedisCodec<String, GraphqlObject> {

        private final ByteArrayCodec codec = new ByteArrayCodec();

        @Override
        public String decodeKey(ByteBuffer bytes) {
            return StandardCharsets.US_ASCII.decode(bytes).toString();
        }

        @Override
        public GraphqlObject decodeValue(ByteBuffer bytes) {
            try (var is = new ObjectInputStream(new ByteArrayInputStream(codec.decodeValue(bytes)))) {
                return (GraphqlObject) is.readObject();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public ByteBuffer encodeKey(String key) {
            return Charset.defaultCharset().encode(key);
        }

        @Override
        public ByteBuffer encodeValue(GraphqlObject value) {
            try (var bos = new ByteArrayOutputStream();
                 var os = new ObjectOutputStream(bos)) {
                os.writeObject(value);
                return codec.encodeValue(bos.toByteArray());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
