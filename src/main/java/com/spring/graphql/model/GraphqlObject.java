package com.spring.graphql.model;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;

@Builder
public record GraphqlObject(String chanel, List<GraphqlEntry> entries) implements Serializable {}
