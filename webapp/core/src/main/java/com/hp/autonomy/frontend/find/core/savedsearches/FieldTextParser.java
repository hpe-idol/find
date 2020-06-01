/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.savedsearches;

@FunctionalInterface
public interface FieldTextParser {
    /**
     * @param savedSearch
     * @param applyDocumentSelection Whether to include the effect of document selection
     * @return Field text for performing the saved search
     */
    String toFieldText(SavedSearch<?, ?> savedSearch, Boolean applyDocumentSelection);
}
