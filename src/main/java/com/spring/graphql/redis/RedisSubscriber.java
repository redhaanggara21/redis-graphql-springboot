package com.spring.graphql.redis;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.spring.graphql.exception.ExceptionHandler;
import com.spring.graphql.model.GraphqlObject;
import io.lettuce.core.pubsub.api.reactive.PatternMessage;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements ApplicationListener<ApplicationReadyEvent> {

    public static final String CHANNEL_PATTERN = "*";
    public final @NonNull RedisPubSubReactiveCommands<String, GraphqlObject>
      redisSubReactiveCommands;
    private final @NonNull ConcurrentHashMap<String, Sinks.Many<GraphqlObject>> sinks;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {

    redisSubReactiveCommands.psubscribe(CHANNEL_PATTERN).subscribe();
    redisSubReactiveCommands
        .observePatterns()
        .doOnNext(this::handleMsg)
        .onErrorContinue(ExceptionHandler::logError)
        .subscribe();
    }

    private void handleMsg(PatternMessage<String, GraphqlObject> patternMessage) {

    log.info(
        "Redis=> received msg: {} from Redis channel: {}",
        patternMessage.getMessage(),
        patternMessage.getChannel());
    Optional.ofNullable(sinks.get(patternMessage.getChannel()))
        .ifPresentOrElse(
            sink -> {
                log.debug(
                    "Flux=> pour msg: {} on sink: {}",
                    patternMessage.getMessage(),
                    patternMessage.getChannel());
                sink.tryEmitNext(patternMessage.getMessage());
            },
            () -> log.warn("Flux=> Could not find chanel:{}", patternMessage.getChannel()));
    }
}
