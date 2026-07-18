// com.tfm.bandas.surveys.dto.UpdateSurveyRequestDTO.java
package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.SurveyType;
import com.tfm.bandas.surveys.utils.ResponseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record UpdateSurveyRequestDTO(
  @NotBlank(message = "El título es obligatorio.")
  @Size(max = 200, message = "El título no puede superar los 200 caracteres.")
  @JsonProperty("title")
  String title,

  @Size(max = 4000, message = "La descripción no puede superar los 4000 caracteres.")
  @JsonProperty("description")
  String description,

  @NotNull(message = "El tipo de respuesta es obligatorio.")
  @JsonProperty("responseType")
  ResponseType responseType,

  @NotNull(message = "El tipo de encuesta es obligatorio.")
  @JsonProperty("surveyType")
  SurveyType surveyType,

  @NotNull(message = "La fecha de apertura es obligatoria.")
  @JsonProperty("opensAt")
  Instant opensAt,

  @NotNull(message = "La fecha de cierre es obligatoria.")
  @JsonProperty("closesAt")
  Instant closesAt
) {
  public void validateWindow() {
    if (opensAt.isAfter(closesAt)) {
      throw new IllegalArgumentException("La fecha de apertura debe ser anterior o igual a la fecha de cierre.");
    }
  }
}
