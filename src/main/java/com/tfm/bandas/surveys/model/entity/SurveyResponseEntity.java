package com.tfm.bandas.surveys.model.entity;

import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
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
@Table(name = "survey_response",
  uniqueConstraints = @UniqueConstraint(name = "uk_response_survey_user", columnNames = {"survey_id","user_iam_id"}),
  indexes = {
    @Index(name = "idx_response_survey", columnList = "survey_id"),
    @Index(name = "idx_response_user", columnList = "user_iam_id")
})
public class SurveyResponseEntity {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String id;

  @Column(name = "survey_id", nullable = false)
  private String surveyId;

  @Column(name = "user_iam_id", nullable = false)
  private String userIamId;

  // MVP: solo para YES_NO_MAYBE
  @Enumerated(EnumType.STRING)
  @Column(name = "answer_yes_no_maybe", length = 10, nullable = false)
  private YesNoMaybeAnswer answerYesNoMaybe;

  @Column(name = "comment", length = 1000)
  private String comment;

  @Column(name = "answered_at", nullable = false)
  private Instant answeredAt;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID().toString();
    if (answeredAt == null) answeredAt = Instant.now();
  }

}
