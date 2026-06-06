package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CreateSurveyRequestDTO(
  @NotNull @JsonProperty("eventId") String eventId,
  @NotBlank @Size(max = 200) @JsonProperty("title") String title,
  @Size(max = 4000) @JsonProperty("description") String description,
  @JsonProperty("responseType") ResponseType responseType, // Opcional, por defecto YES_NO_MAYBE
  @JsonProperty("surveyType") SurveyType surveyType, // Opcional, por defecto OTHER
  @JsonProperty("opensAt") Instant opensAt,
  @JsonProperty("closesAt") Instant closesAt
) {}
