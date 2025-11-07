package com.tfm.bandas.surveys.model.repository;

import com.tfm.bandas.surveys.model.entity.SurveyResponseEntity;
import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponseEntity, String> {
  Optional<SurveyResponseEntity> findBySurveyIdAndUserIamId(String surveyId, String userIamId);

  @Query("select r from SurveyResponseEntity r where r.surveyId = :surveyId")
  Page<SurveyResponseEntity> findBySurveyId(@Param("surveyId") String surveyId, Pageable pageable);

  @Query("select count(r) from SurveyResponseEntity r where r.surveyId = :surveyId")
  long countBySurveyId(String surveyId);

  long countBySurveyIdAndAnswerYesNoMaybe(String surveyId, YesNoMaybeAnswer answer);

  void deleteBySurveyIdAndUserIamId(String surveyId, String userIamId);

}
