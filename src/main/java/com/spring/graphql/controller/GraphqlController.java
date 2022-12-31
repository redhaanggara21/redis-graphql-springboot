package com.spring.graphql.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import com.spring.graphql.model.GraphqlObject;
import com.spring.graphql.model.GraphqlObjectInput;
import com.spring.graphql.service.GraphqlService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GraphqlController {

    private final @NonNull GraphqlService graphqlService;

    @MutationMapping
    public GraphqlObject publishGraphqlObject(
        @Argument GraphqlObjectInput graphqlObjectInput) {

        return graphqlService.publishGraphqlObject(graphqlObjectInput);
    }

    @SubscriptionMapping
    public Flux<GraphqlObject> graphqlObjectPublished(@Argument String chanel) {

        return graphqlService.graphqlObjectPublished(chanel);
    }
}
