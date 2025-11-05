package com.tfm.bandas.surveys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateSurveyRequestDTO(
  @NotNull String eventId,
  @NotBlank @Size(max = 200) String title,
  @Size(max = 4000) String description,
  Instant opensAt,
  Instant closesAt
) {}
