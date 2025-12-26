package com.tfm.bandas.surveys.dto.mapper;

import com.tfm.bandas.surveys.dto.CreateSurveyRequestDTO;
import com.tfm.bandas.surveys.dto.SurveyDTO;
import com.tfm.bandas.surveys.model.entity.SurveyEntity;
import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;

import java.time.Instant;
import java.util.UUID;

public class SurveyMapper {

  private SurveyMapper() {}

  public static SurveyDTO toDto(SurveyEntity entity) {
    if (entity == null) return null;
    return new SurveyDTO(
      entity.getId(),
      entity.getVersion(),
      entity.getEventId(),
      entity.getTitle(),
      entity.getDescription(),
      entity.getStatus(),
      entity.getResponseType(),
      entity.getSurveyType(),
      entity.getOpensAt(),
      entity.getClosesAt(),
      entity.getCreatedBy(),
      entity.getCreatedAt(),
      entity.getUpdatedAt()
    );
  }

  public static SurveyEntity toEntity(SurveyDTO dto) {
    if (dto == null) return null;
    SurveyEntity.SurveyEntityBuilder entityBuilder = SurveyEntity.builder();
    entityBuilder.id(dto.id());
    entityBuilder.eventId(dto.eventId());
    entityBuilder.title(dto.title());
    entityBuilder.description(dto.description());
    entityBuilder.status(dto.status());
    entityBuilder.responseType(dto.responseType());
    entityBuilder.surveyType(dto.surveyType());
    entityBuilder.opensAt(dto.opensAt());
    entityBuilder.closesAt(dto.closesAt());
    entityBuilder.createdBy(dto.createdBy());
    entityBuilder.createdAt(dto.createdAt());
    entityBuilder.updatedAt(dto.updatedAt());
    return entityBuilder.build();
  }

  public static SurveyEntity toEntity(CreateSurveyRequestDTO createRequest) {
    if (createRequest == null) return null;
    SurveyEntity.SurveyEntityBuilder entityBuilder = SurveyEntity.builder();
    entityBuilder.id(UUID.randomUUID().toString());
    entityBuilder.eventId(createRequest.eventId());
    entityBuilder.title(createRequest.title());
    entityBuilder.description(createRequest.description());
    entityBuilder.opensAt(createRequest.opensAt());
    entityBuilder.closesAt(createRequest.closesAt());
    // Valores por defecto
    entityBuilder.status(SurveyStatus.DRAFT);
    entityBuilder.responseType(createRequest.responseType() != null ? createRequest.responseType() : ResponseType.YES_NO_MAYBE);
    entityBuilder.surveyType(createRequest.surveyType() != null ? createRequest.surveyType() : SurveyType.OTHER);

    entityBuilder.createdAt(Instant.now());
    entityBuilder.updatedAt(Instant.now());
    return entityBuilder.build();
  }

}
