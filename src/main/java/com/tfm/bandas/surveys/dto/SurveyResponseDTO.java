package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;

import java.time.Instant;

public record SurveyResponseDTO(
  String id,
  String surveyId,
  String userIamId,
  YesNoMaybeAnswer answerYesNoMaybe,
  String comment,
  Instant answeredAt
) {}

