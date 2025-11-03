package com.tfm.bandas.surveys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateSurveyRequestDTO(
  @NotNull String eventId,
  @NotBlank String title,
  String description,
  Instant opensAt,
  Instant closesAt
) {}
