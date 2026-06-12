package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RespondYesNoMaybeRequestDTO(
  @NotNull @JsonProperty("answer") YesNoMaybeAnswer answer,
  @JsonProperty("instrumentId") String instrumentId, // Obligatorio solo si la encuesta es YES_NO_MAYBE_WITH_INSTRUMENT y answer es YES o MAYBE
  @Size(max=1000) @JsonProperty("comment") String comment
) {}
