package com.tfm.bandas.surveys.model.repository;

import com.tfm.bandas.surveys.model.entity.SurveyResponseEntity;
import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponseEntity, String> {
  Optional<SurveyResponseEntity> findBySurveyIdAndUserIamId(String surveyId, String userIamId);
  long countBySurveyIdAndAnswerYesNoMaybe(String surveyId, YesNoMaybeAnswer answer);

  @Query("select r from SurveyResponseEntity r where r.surveyId = :surveyId")
  List<SurveyResponseEntity> findBySurveyId(@Param("surveyId") String surveyId);

  @Query("select r from SurveyResponseEntity r where r.surveyId = :surveyId and r.userIamId = :userId")
  Optional<SurveyResponseEntity> findMyResponse(@Param("surveyId") String surveyId, @Param("userId") String userId);

}
