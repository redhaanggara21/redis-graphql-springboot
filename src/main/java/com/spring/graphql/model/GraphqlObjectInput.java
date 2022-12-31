package com.spring.graphql.model;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;

@Builder
public record GraphqlObjectInput(String chanel, List<GraphqlEntry> entries) implements Serializable {}
