/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.idol.savedsearches.query;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.find.core.beanconfiguration.BiConfiguration;
import com.hp.autonomy.frontend.find.core.savedsearches.EmbeddableIndex;
import com.hp.autonomy.frontend.find.core.savedsearches.FieldTextParser;
import com.hp.autonomy.frontend.find.core.savedsearches.SavedSearchService;
import com.hp.autonomy.frontend.find.core.savedsearches.SavedSearchType;
import com.hp.autonomy.frontend.find.core.savedsearches.query.SavedQuery;
import com.hp.autonomy.frontend.find.core.savedsearches.query.SavedQuery.Builder;
import com.hp.autonomy.frontend.find.core.savedsearches.query.SavedQueryController;
import com.hp.autonomy.frontend.find.idol.dashboards.IdolDashboardConfig;
import com.hp.autonomy.frontend.find.idol.dashboards.widgets.datasources.SavedSearchDatasource;
import com.hp.autonomy.searchcomponents.core.search.QueryRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolDocumentsService;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@ConditionalOnProperty(BiConfiguration.BI_PROPERTY)
class IdolSavedQueryController extends SavedQueryController<IdolQueryRequest, String, IdolQueryRestrictions, IdolSearchResult, AciErrorException> {
    private final ConfigService<IdolDashboardConfig> dashboardConfigService;

    @SuppressWarnings({"TypeMayBeWeakened", "ConstructorWithTooManyParameters"})
    @Autowired
    IdolSavedQueryController(final SavedSearchService<SavedQuery, Builder> service,
                             final IdolDocumentsService documentsService,
                             final FieldTextParser fieldTextParser,
                             final ObjectFactory<IdolQueryRestrictionsBuilder> queryRestrictionsBuilderFactory,
                             final ObjectFactory<IdolQueryRequestBuilder> queryRequestBuilderFactory,
                             final ConfigService<IdolDashboardConfig> dashboardConfigService) {
        super(service, documentsService, fieldTextParser, queryRestrictionsBuilderFactory, queryRequestBuilderFactory);
        this.dashboardConfigService = dashboardConfigService;
    }

    @Override
    protected String convertEmbeddableIndex(final EmbeddableIndex embeddableIndex) {
        return embeddableIndex.getName();
    }

    @Override
    protected void addParams(final QueryRequestBuilder<IdolQueryRequest, IdolQueryRestrictions, ?> queryRequestBuilder) {
        queryRequestBuilder.print(PrintParam.NoResults.name());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public SavedQuery get(@PathVariable("id") final long id) {
        if(getValidIds().contains(id)) {
            return service.getDashboardSearch(id);
        } else {
            throw new IllegalArgumentException("Saved Search Id is not in the dashboards configuration file");
        }
    }

    private Collection<Long> getValidIds() {
        return dashboardConfigService.getConfig().getDashboards().stream()
                .flatMap(dashboard -> dashboard.getWidgets().stream()
                        .filter(widget -> widget.getDatasource() instanceof SavedSearchDatasource)
                        .map(widget -> (SavedSearchDatasource)widget.getDatasource())
                        .filter(datasource -> datasource.getConfig().getType() == SavedSearchType.QUERY)
                        .map(datasource -> datasource.getConfig().getId()))
                .collect(Collectors.toSet());
    }
}
