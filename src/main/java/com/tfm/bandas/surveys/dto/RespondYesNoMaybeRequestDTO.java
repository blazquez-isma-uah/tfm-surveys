package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RespondYesNoMaybeRequestDTO(
  @NotNull YesNoMaybeAnswer answer,
  @Size(max=1000) String comment
) {}
