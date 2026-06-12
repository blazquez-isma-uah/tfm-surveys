package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateSurveyResponseRequestDTO(
  @NotNull @JsonProperty("answer")
  YesNoMaybeAnswer answer,

  @JsonProperty("instrumentId")
  String instrumentId,

  @Size(max = 1000)
  @JsonProperty("comment")
  String comment
) {
}
