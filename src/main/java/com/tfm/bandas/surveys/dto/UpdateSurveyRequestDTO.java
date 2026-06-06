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
  @NotBlank
  @Size(max = 200)
  @JsonProperty("title")
  String title,

  @Size(max = 4000)
  @JsonProperty("description")
  String description,

  @NotNull
  @JsonProperty("responseType")
  ResponseType responseType,

  @NotNull
  @JsonProperty("surveyType")
  SurveyType surveyType,

  @NotNull
  @JsonProperty("opensAt")
  Instant opensAt,

  @NotNull
  @JsonProperty("closesAt")
  Instant closesAt
) {
  public void validateWindow() {
    if (opensAt.isAfter(closesAt)) {
      throw new IllegalArgumentException("opensAt must be <= closesAt");
    }
  }
}
