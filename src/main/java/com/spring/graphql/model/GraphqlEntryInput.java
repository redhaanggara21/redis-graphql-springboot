package com.spring.graphql.model;

import java.io.Serializable;

import lombok.Builder;

@Builder
public record GraphqlEntryInput(String key, String value) implements Serializable {}
