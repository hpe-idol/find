/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.idol.comparison;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.frontend.find.core.search.DocumentsController;
import com.hp.autonomy.frontend.find.core.search.QueryRestrictionsBuilderFactory;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ComparisonServiceImpl implements ComparisonService<IdolSearchResult, AciErrorException> {
    private final DocumentsService<String, IdolSearchResult, AciErrorException> documentsService;
    private final QueryRestrictionsBuilderFactory<IdolQueryRestrictions, String> queryRestrictionsBuilderFactory;

    @Autowired
    public ComparisonServiceImpl(
            final DocumentsService<String, IdolSearchResult, AciErrorException> documentsService,
            final QueryRestrictionsBuilderFactory<IdolQueryRestrictions, String> queryRestrictionsBuilderFactory
    ) {
        this.documentsService = documentsService;
        this.queryRestrictionsBuilderFactory = queryRestrictionsBuilderFactory;
    }

    private Documents<IdolSearchResult> getEmptyResults() {
        return new Documents<>(Collections.emptyList(), 0, "", null, null, null);
    }

    private String generateDifferenceStateToken(final String firstQueryStateToken, final String secondQueryStateToken) throws AciErrorException {
        final QueryRestrictions<String> queryRestrictions = queryRestrictionsBuilderFactory.createBuilder()
                .queryText("*")
                .fieldText("")
                .minScore(0)
                .stateMatchId(firstQueryStateToken)
                .stateDontMatchId(secondQueryStateToken)
                .build();

        return documentsService.getStateToken(queryRestrictions, ComparisonController.STATE_TOKEN_MAX_RESULTS, false);
    }

    @Override
    public Documents<IdolSearchResult> getResults(
            final List<String> stateMatchIds,
            final List<String> stateDontMatchIds,
            final String text,
            final String fieldText,
            final int resultsStart,
            final int maxResults,
            final String summary,
            final String sort,
            final boolean highlight
    ) throws AciErrorException {
        if (stateMatchIds.isEmpty()) {
            return getEmptyResults();
        }

        final QueryRestrictions<String> queryRestrictions = queryRestrictionsBuilderFactory.createBuilder()
                .queryText(text)
                .fieldText(fieldText)
                .minScore(0)
                .stateMatchIds(stateMatchIds)
                .stateDontMatchIds(stateDontMatchIds)
                .build();

        final QueryRequest<String> queryRequest = QueryRequest.<String>builder()
                .queryRestrictions(queryRestrictions)
                .start(resultsStart)
                .maxResults(maxResults)
                .summary(summary)
                .summaryCharacters(DocumentsController.MAX_SUMMARY_CHARACTERS)
                .sort(sort)
                .highlight(highlight)
                .autoCorrect(false)
                .queryType(QueryRequest.QueryType.RAW)
                .build();

        return documentsService.queryTextIndex(queryRequest);
    }

    @Override
    public ComparisonStateTokens getCompareStateTokens(final String firstStateToken, final String secondStateToken) throws AciErrorException {
        // First generate the relative complement state tokens, i.e. A \ B and B \ A
        // - where A is the set of documents in our "firstStateToken" and B is the set of
        // documents in our "secondStateToken".
        final String docsInFirstStateToken = generateDifferenceStateToken(firstStateToken, secondStateToken);
        final String docsInSecondStateToken = generateDifferenceStateToken(secondStateToken, firstStateToken);

        final ComparisonStateTokens stateTokens = new ComparisonStateTokens();
        stateTokens.setFirstQueryStateToken(firstStateToken);
        stateTokens.setSecondQueryStateToken(secondStateToken);
        stateTokens.setDocumentsOnlyInFirstStateToken(docsInFirstStateToken);
        stateTokens.setDocumentsOnlyInSecondStateToken(docsInSecondStateToken);

        return stateTokens;
    }
}
