package com.tfm.bandas.surveys.model.repository;

import com.tfm.bandas.surveys.model.entity.SurveyEntity;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface SurveyRepository extends JpaRepository<SurveyEntity, String>, JpaSpecificationExecutor<SurveyEntity> {
  @Query("""
    select s from SurveyEntity s
    where s.status = com.tfm.bandas.surveys.utils.SurveyStatus.OPEN
      and s.eventId = :eventId
      and (s.opensAt is null or s.opensAt <= :now)
      and (s.closesAt is null or s.closesAt >= :now)
    """)
  Page<SurveyEntity> findOpenSurveysOfEvent(@Param("eventId") String eventId, @Param("now") Instant now, Pageable pageable);

  Page<SurveyEntity> findByEventId(String eventId, Pageable pageable);

  boolean existsByEventIdAndSurveyType(String eventId, SurveyType surveyType);

  @Query("""
    select s from SurveyEntity s
    where exists (
      select 1 from SurveyResponseEntity r
      where r.surveyId = s.id and r.userIamId = :userId
    )
    and (:status is null or s.status = :status)
    and (:surveyType is null or s.surveyType = :surveyType)
    and (:opensFrom is null or s.opensAt >= :opensFrom)
    and (:opensTo is null or s.opensAt <= :opensTo)
    and (:closesFrom is null or s.closesAt >= :closesFrom)
    and (:closesTo is null or s.closesAt <= :closesTo)
    """)
  Page<SurveyEntity> findSurveysAnsweredByUser(
      @Param("userId") String userId,
      @Param("status") SurveyStatus status,
      @Param("surveyType") SurveyType surveyType,
      @Param("opensFrom") Instant opensFrom,
      @Param("opensTo") Instant opensTo,
      @Param("closesFrom") Instant closesFrom,
      @Param("closesTo") Instant closesTo,
      Pageable pageable);

  @Query("""
    select s from SurveyEntity s
    where not exists (
      select 1 from SurveyResponseEntity r
      where r.surveyId = s.id and r.userIamId = :userId
    )
    and (:status is null or s.status = :status)
    and (:surveyType is null or s.surveyType = :surveyType)
    and (:opensFrom is null or s.opensAt >= :opensFrom)
    and (:opensTo is null or s.opensAt <= :opensTo)
    and (:closesFrom is null or s.closesAt >= :closesFrom)
    and (:closesTo is null or s.closesAt <= :closesTo)
    """)
  Page<SurveyEntity> findSurveysNotAnsweredByUser(
      @Param("userId") String userId,
      @Param("status") SurveyStatus status,
      @Param("surveyType") SurveyType surveyType,
      @Param("opensFrom") Instant opensFrom,
      @Param("opensTo") Instant opensTo,
      @Param("closesFrom") Instant closesFrom,
      @Param("closesTo") Instant closesTo,
      Pageable pageable);
}
