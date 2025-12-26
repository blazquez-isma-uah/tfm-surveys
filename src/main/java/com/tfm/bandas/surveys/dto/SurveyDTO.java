package com.tfm.bandas.surveys.dto;

import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;

import java.time.Instant;

public record SurveyDTO(
  String id,
  Integer version,
  String eventId,
  String title,
  String description,
  SurveyStatus status,
  ResponseType responseType,
  SurveyType surveyType,
  Instant opensAt,
  Instant closesAt,
  String createdBy,
  Instant createdAt,
  Instant updatedAt
) {}
