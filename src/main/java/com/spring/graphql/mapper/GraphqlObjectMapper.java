package com.spring.graphql.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.spring.graphql.model.GraphqlEntry;
import com.spring.graphql.model.GraphqlEntryInput;
import com.spring.graphql.model.GraphqlObject;
import com.spring.graphql.model.GraphqlObjectInput;

@Mapper
public interface GraphqlObjectMapper {

    GraphqlObjectMapper INSTANCE = Mappers.getMapper(GraphqlObjectMapper.class);

    GraphqlObjectInput objectToInput(GraphqlObject graphqlObject);

    GraphqlObject inputToObject(GraphqlObjectInput graphqlObjectInput);

    GraphqlEntryInput entryToInput(GraphqlEntry graphqlEntry);

    GraphqlEntry inputToEntry(GraphqlEntryInput graphqlEntryInput);
}
