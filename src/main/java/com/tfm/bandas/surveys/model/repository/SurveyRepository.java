package com.tfm.bandas.surveys.model.repository;

import com.tfm.bandas.surveys.model.entity.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SurveyRepository extends JpaRepository<SurveyEntity, String> {
  @Query("""
    select s from SurveyEntity s
    where s.status = com.tfm.bandas.surveys.utils.SurveyStatus.OPEN
      and s.eventId = :eventId
      and (s.opensAt is null or s.opensAt <= :now)
      and (s.closesAt is null or s.closesAt >= :now)
    """)
  List<SurveyEntity> findOpenSurveysOfEvent(@Param("eventId") String eventId, @Param("now") Instant now);
  List<SurveyEntity> findByEventId(String eventId);
}
