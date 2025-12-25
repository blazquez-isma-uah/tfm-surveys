package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;

import java.time.Instant;

public record SurveyResponseDTO(
  String id,
  Integer version,
  String surveyId,
  String userIamId,
  YesNoMaybeAnswer answerYesNoMaybe,
  String instrumentId,
  String comment,
  Instant answeredAt
) {}
