package com.tfm.bandas.surveys.dto.mapper;

import com.tfm.bandas.surveys.dto.SurveyResponseDTO;
import com.tfm.bandas.surveys.model.entity.SurveyResponseEntity;

public final class SurveyResponseMapper {
  private SurveyResponseMapper() {}

  public static SurveyResponseDTO toDto(SurveyResponseEntity entity) {
    if (entity == null) return null;
    return new SurveyResponseDTO(
      entity.getId(),
      entity.getSurveyId(),
      entity.getUserIamId(),
      entity.getAnswerYesNoMaybe(),
      entity.getComment(),
      entity.getAnsweredAt()
    );
  }

  public static SurveyResponseEntity toEntity(SurveyResponseDTO dto) {
    if (dto == null) return null;
    return SurveyResponseEntity.builder()
      .id(dto.id())
      .surveyId(dto.surveyId())
      .userIamId(dto.userIamId())
      .answerYesNoMaybe(dto.answerYesNoMaybe())
      .comment(dto.comment())
      .answeredAt(dto.answeredAt())
      .build();
  }
}

