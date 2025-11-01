package com.tfm.bandas.surveys.model.repository;

import com.tfm.bandas.surveys.model.entity.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SurveyRepository extends JpaRepository<SurveyEntity, String> {
  @Query("""
    select s from SurveyEntity s
    where s.status = 'OPEN'
      and (:eventId is null or s.eventId = :eventId)
      and (s.opensAt is null or s.opensAt <= :now)
      and (s.closesAt is null or s.closesAt >= :now)
    """)
  List<SurveyEntity> findOpenSurveys(@Param("eventId") String eventId, @Param("now") Instant now);

  List<SurveyEntity> findByEventId(String eventId);
}
