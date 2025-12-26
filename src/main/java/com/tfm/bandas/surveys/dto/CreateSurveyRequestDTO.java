package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateSurveyRequestDTO(
  @NotNull String eventId,
  @NotBlank @Size(max = 200) String title,
  @Size(max = 4000) String description,
  ResponseType responseType, // Opcional, por defecto YES_NO_MAYBE
  SurveyType surveyType, // Opcional, por defecto OTHER
  Instant opensAt,
  Instant closesAt
) {}
