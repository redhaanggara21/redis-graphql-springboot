package com.spring.graphql.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.graphql.model.GraphqlEntry;
import com.spring.graphql.model.GraphqlObject;
import com.spring.graphql.model.GraphqlObjectInput;
import com.spring.graphql.service.GraphqlService;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@GraphQlTest(GraphqlController.class)
class GraphqlControllerTest {

  public static final String MUTATION = "mutation";
  public static final String SUBSCRIPTION = "subscription";

  public static final String MUTATION_NAME = "publishGraphqlObject";
  public static final String SUBSCRIPTION_NAME = "graphqlObjectPublished";
  public static final String MUTATION_INPUT_PARAM_NAME = "graphqlObject";
  public static final String SUBSCRIPTION_INPUT_PARAM_NAME = "chanel";
  ObjectMapper mapper = new ObjectMapper();
  @Autowired GraphQlTester tester;
  @MockBean
  GraphqlService graphqlService;

  @Test
  void shouldReturnGraphqlObjectOnMutationInvocation() {

    // given
    GraphqlObject graphqlObject =
        GraphqlObject.builder()
            .chanel("Chanel")
            .entries(List.of(GraphqlEntry.builder().key("key1").value("val1").build()))
            .entries(List.of(GraphqlEntry.builder().key("key2").value("val2").build()))
            .build();

    // when
    when(graphqlService.publishGraphqlObject(any(GraphqlObjectInput.class)))
        .thenReturn(graphqlObject);

    // then
    tester
        .documentName(MUTATION)
        .variable(MUTATION_INPUT_PARAM_NAME, mapper.convertValue(graphqlObject, Map.class))
        .execute()
        .errors()
        .verify()
        .path(MUTATION_NAME)
        .entity(GraphqlObject.class)
        .isEqualTo(graphqlObject);
  }

  @Test
  void shouldReturnFluxOnSubscriptionInvocation() {

    // given
    GraphqlObject graphqlObject =
        GraphqlObject.builder()
            .chanel("Chanel")
            .entries(List.of(GraphqlEntry.builder().key("key1").value("val1").build()))
            .entries(List.of(GraphqlEntry.builder().key("key2").value("val2").build()))
            .build();

    // when
    when(graphqlService.graphqlObjectPublished(anyString()))
        .thenReturn(Flux.fromStream(Stream.of(graphqlObject)));

    // then
    Flux<GraphqlObject> processingObjectFlux =
        tester
            .documentName(SUBSCRIPTION)
            .variable(SUBSCRIPTION_INPUT_PARAM_NAME, "Chanel")
            .executeSubscription()
            .toFlux(SUBSCRIPTION_NAME, GraphqlObject.class);

    StepVerifier.create(processingObjectFlux).expectNext(graphqlObject).verifyComplete();
  }
}
