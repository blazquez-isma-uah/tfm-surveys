package com.tfm.bandas.surveys.model.entity;

import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "survey", indexes = {
  @Index(name = "idx_survey_event", columnList = "event_id"),
  @Index(name = "idx_survey_status", columnList = "status"),
  @Index(name = "idx_survey_window", columnList = "opens_at,closes_at"),
  @Index(name = "idx_survey_type", columnList = "survey_type"),
  @Index(name = "idx_survey_event_type", columnList = "event_id,survey_type")
})
public class SurveyEntity {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String id;

  @Column(name = "event_id", nullable = false)
  private String eventId;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "description", length = 4000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private SurveyStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "response_type", nullable = false, length = 30)
  private ResponseType responseType;

  @Enumerated(EnumType.STRING)
  @Column(name = "survey_type", nullable = false, length = 20)
  private SurveyType surveyType;

  @Column(name = "opens_at")
  private Instant opensAt;

  @Column(name = "closes_at")
  private Instant closesAt;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Version
  @Column(name = "version")
  private Integer version;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID().toString();
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
    if (status == null) status = SurveyStatus.DRAFT;
    if (responseType == null) responseType = ResponseType.YES_NO_MAYBE;
    if (surveyType == null) surveyType = SurveyType.OTHER;
  }

  @PreUpdate
  void preUpdate() { updatedAt = Instant.now(); }

}
