package com.tfm.bandas.surveys.service;

import com.tfm.bandas.surveys.dto.*;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface SurveyService {
  SurveyDTO createSurvey(CreateSurveyRequestDTO survey, String userCreatorId);
  SurveyDTO getSurveyById(String surveyId);
  void deleteSurvey(String surveyId, int ifMatchVersion);
  SurveyDTO updateSurvey(String surveyId, int ifMatchVersion, UpdateSurveyRequestDTO survey);
  SurveyDTO openSurvey(String surveyId, int ifMatchVersion);
  SurveyDTO closeSurvey(String surveyId, int ifMatchVersion);
  SurveyDTO cancelSurvey(String surveyId, int ifMatchVersion);
  SurveyDTO resetSurvey(String surveyId, int ifMatchVersion);
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
  void deleteSurveysByEventId(String eventId);
}
