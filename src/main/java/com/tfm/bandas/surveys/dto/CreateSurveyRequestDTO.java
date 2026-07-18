package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CreateSurveyRequestDTO(
  @NotNull(message = "El ID del evento es obligatorio.") @JsonProperty("eventId") String eventId,
  @NotBlank(message = "El título es obligatorio.")
  @Size(max = 200, message = "El título no puede superar los 200 caracteres.")
  @JsonProperty("title") String title,
  @Size(max = 4000, message = "La descripción no puede superar los 4000 caracteres.")
  @JsonProperty("description") String description,
  @JsonProperty("responseType") ResponseType responseType, // Opcional, por defecto YES_NO_MAYBE
  @JsonProperty("surveyType") SurveyType surveyType, // Opcional, por defecto OTHER
  @JsonProperty("opensAt") Instant opensAt,
  @JsonProperty("closesAt") Instant closesAt
) {}
