package com.spring.graphql.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.spring.graphql.mapper.GraphqlObjectMapper;
import com.spring.graphql.model.GraphqlObjectInput;
import com.spring.graphql.model.GraphqlObject;
import com.spring.graphql.redis.RedisPublisher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphqlService {

    private final @NonNull ConcurrentHashMap<String, Sinks.Many<GraphqlObject>> sinks;
    private final @NonNull RedisPublisher redisPublisher;

    public GraphqlObject publishGraphqlObject(GraphqlObjectInput graphqlObjectInput) {

        log.debug(
            "Graphql=> received msg from service: {} on chanel: {}",
            graphqlObjectInput,
            graphqlObjectInput.chanel());
        GraphqlObject gqlObject = GraphqlObjectMapper.INSTANCE.inputToObject(graphqlObjectInput);
        redisPublisher.publish(gqlObject);
        return gqlObject;
    }

    public Flux<GraphqlObject> graphqlObjectPublished(String topic) {

        return sinks
            .computeIfAbsent(topic, key -> Sinks.many().multicast().onBackpressureBuffer())
            .asFlux()
            .doOnNext(po -> log.debug("Flux=> msg {} flow from sink: {}", po, topic))
            .doOnSubscribe(s -> log.info("Graphql=> subscription opened for chanel: {}", topic))
            .doOnCancel(() -> log.info("Graphql=> subscription closed for chanel: {}", topic));
    }
}
