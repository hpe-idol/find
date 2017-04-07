/*
 * Copyright 2016 Hewlett-Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.autonomy.abc.selenium.find.application;

import java.util.Arrays;
import java.util.Optional;

public enum UserRole {
    BIFHI("bifhi"), FIND("find");

    /**
     * The id in the config file of the user with this role.
     */
    private final String configId;

    UserRole(final String configId) {
        this.configId = configId;
    }

    public String getConfigId() {
        return configId;
    }

    public static Optional<UserRole> fromString(final String value) {
        final String lowerCaseValue = value.toLowerCase();

        return Arrays.stream(values())
                .filter(role -> role.name().toLowerCase().equals(lowerCaseValue))
                .findFirst();
    }

    public static UserRole activeRole() {
        final String property = System.getProperty("userRole");

        return fromString(property)
                .orElseThrow(() -> new IllegalStateException("Unrecognised role \"" + property + "\" read from userRole system property"));
    }
}
