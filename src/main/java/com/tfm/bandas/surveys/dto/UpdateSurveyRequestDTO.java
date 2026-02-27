// com.tfm.bandas.surveys.dto.UpdateSurveyRequestDTO.java
package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.SurveyType;
import com.tfm.bandas.surveys.utils.ResponseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record UpdateSurveyRequestDTO(
  @NotBlank
  @Size(max = 200)
  String title,

  @Size(max = 4000)
  String description,

  @NotNull
  ResponseType responseType,

  @NotNull
  SurveyType surveyType,

  @NotNull
  Instant opensAt,

  @NotNull
  Instant closesAt
) {
  public void validateWindow() {
    if (opensAt.isAfter(closesAt)) {
      throw new IllegalArgumentException("opensAt must be <= closesAt");
    }
  }
}
