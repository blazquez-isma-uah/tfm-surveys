package com.tfm.bandas.surveys.utils;

public class Constants {
    public static final String[] PATTERNS_PERMITED = {"/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"};
    public static final String[] PATTERNS_SURVEYS = {"/api/surveys/**"};
    public static final String[] PATTERNS_RESPONSES = {"/api/surveys/responses/**"};
}
