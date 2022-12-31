package com.spring.graphql.model;

import java.io.Serializable;

import lombok.Builder;

@Builder
public record GraphqlEntry(String key, String value) implements Serializable {}
