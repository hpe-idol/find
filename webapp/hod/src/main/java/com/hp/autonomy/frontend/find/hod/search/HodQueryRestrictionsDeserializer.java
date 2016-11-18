/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.hod.search;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.find.core.search.QueryRestrictionsDeserializer;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.function.Function;

@JsonComponent
public class HodQueryRestrictionsDeserializer extends QueryRestrictionsDeserializer<ResourceIdentifier> {
    @Override
    public QueryRestrictions<ResourceIdentifier> deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final ObjectMapper objectMapper = createObjectMapper();

        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        return HodQueryRestrictions.builder()
                .queryText(parseAsText(objectMapper, node, "queryText"))
                .fieldText(parseAsText(objectMapper, node, "fieldText"))
                .databases(parseDatabaseArray(node, "databases"))
                .minDate(parseDate(objectMapper, node, "minDate"))
                .maxDate(parseDate(objectMapper, node, "maxDate"))
                .languageType(parseAsText(objectMapper, node, "languageType"))
                .build();
    }

    @Override
    protected Function<JsonNode, ResourceIdentifier> constructDatabaseNodeParser() {
        return databaseNode -> new ResourceIdentifier(databaseNode.get("domain").asText(), databaseNode.get("name").asText());
    }
}
