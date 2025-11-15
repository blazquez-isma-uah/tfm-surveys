package com.tfm.bandas.surveys.model.repository;

import com.tfm.bandas.surveys.model.entity.SurveyEntity;
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
}
