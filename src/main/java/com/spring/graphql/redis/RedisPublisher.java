package com.spring.graphql.redis;

import org.springframework.stereotype.Service;

import com.spring.graphql.exception.ExceptionHandler;
import com.spring.graphql.model.GraphqlObject;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final @NonNull RedisPubSubReactiveCommands<String, GraphqlObject>
        redisPubReactiveCommands;

    public void publish(GraphqlObject graphqlObject) {
        redisPubReactiveCommands
            .publish(graphqlObject.chanel(), graphqlObject)
            .doOnNext(
                l ->
                    log.info(
                        "Redis=> published msg: {} on Redis channel: {}",
                        graphqlObject,
                        graphqlObject.chanel()))
            .onErrorContinue(ExceptionHandler::logError)
            .subscribe();
    }
}
