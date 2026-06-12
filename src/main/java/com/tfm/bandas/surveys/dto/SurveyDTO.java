package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record SurveyDTO(
  @JsonProperty("id") String id,
  @JsonProperty("version") Integer version,
  @JsonProperty("eventId") String eventId,
  @JsonProperty("title") String title,
  @JsonProperty("description") String description,
  @JsonProperty("status") SurveyStatus status,
  @JsonProperty("responseType") ResponseType responseType,
  @JsonProperty("surveyType") SurveyType surveyType,
  @JsonProperty("opensAt") Instant opensAt,
  @JsonProperty("closesAt") Instant closesAt,
  @JsonProperty("createdBy") String createdBy,
  @JsonProperty("createdAt") Instant createdAt,
  @JsonProperty("updatedAt") Instant updatedAt
) {}
