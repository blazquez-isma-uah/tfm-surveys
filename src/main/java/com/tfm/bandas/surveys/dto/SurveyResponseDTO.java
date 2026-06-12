package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record SurveyResponseDTO(
  @JsonProperty("id") String id,
  @JsonProperty("version") Integer version,
  @JsonProperty("surveyId") String surveyId,
  @JsonProperty("userIamId") String userIamId,
  @JsonProperty("answerYesNoMaybe") YesNoMaybeAnswer answerYesNoMaybe,
  @JsonProperty("instrumentId") String instrumentId,
  @JsonProperty("comment") String comment,
  @JsonProperty("answeredAt") Instant answeredAt
) {}
