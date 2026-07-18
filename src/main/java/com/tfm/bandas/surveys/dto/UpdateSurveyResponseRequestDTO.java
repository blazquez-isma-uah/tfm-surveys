package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateSurveyResponseRequestDTO(
  @NotNull(message = "La respuesta es obligatoria.") @JsonProperty("answer")
  YesNoMaybeAnswer answer,

  @JsonProperty("instrumentId")
  String instrumentId,

  @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres.")
  @JsonProperty("comment")
  String comment
) {
}
