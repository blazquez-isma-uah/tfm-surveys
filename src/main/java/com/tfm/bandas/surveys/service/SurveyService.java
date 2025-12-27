package com.tfm.bandas.surveys.service;

import com.tfm.bandas.surveys.dto.*;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface SurveyService {
  SurveyDTO createSurvey(CreateSurveyRequestDTO survey, String userCreatorId);
  SurveyDTO getSurveyById(String suveyId);
  void deleteSurvey(String suveyId, int ifMatchVersion);
  SurveyDTO updateSurvey(String suveyId, int ifMatchVersion, UpdateSurveyRequestDTO survey);
  SurveyDTO openSurvey(String suveyId, int ifMatchVersion);
  SurveyDTO closeSurvey(String suveyId, int ifMatchVersion);
  SurveyDTO cancelSurvey(String suveyId, int ifMatchVersion);
  Page<SurveyDTO> listOpenSurveysForEvent(String eventId, Pageable pageable);
  Page<SurveyDTO> listAllSurveysForEvent(String eventId, Pageable pageable);
  Page<SurveyDTO> searchSurveys(
          String qText,
          String title,
          String description,
          String eventId,
          SurveyStatus status,
          SurveyType surveyType,
          java.time.Instant opensFrom,
          java.time.Instant opensTo,
          java.time.Instant closesFrom,
          java.time.Instant closesTo,
          Pageable pageable);
  Page<SurveyDTO> listSurveysAnsweredByUser(
          String userId,
          SurveyStatus status,
          SurveyType surveyType,
          java.time.Instant opensFrom,
          java.time.Instant opensTo,
          java.time.Instant closesFrom,
          java.time.Instant closesTo,
          Pageable pageable);
  Page<SurveyDTO> listSurveysNotAnsweredByUser(
          String userId,
          SurveyStatus status,
          SurveyType surveyType,
          java.time.Instant opensFrom,
          java.time.Instant opensTo,
          java.time.Instant closesFrom,
          java.time.Instant closesTo,
          Pageable pageable);
}
