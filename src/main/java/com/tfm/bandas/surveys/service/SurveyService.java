package com.tfm.bandas.surveys.service;

import com.tfm.bandas.surveys.dto.CreateSurveyRequestDTO;
import com.tfm.bandas.surveys.dto.RespondYesNoMaybeRequestDTO;
import com.tfm.bandas.surveys.dto.SurveyDTO;
import com.tfm.bandas.surveys.dto.SurveyResponseDTO;
import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;

import java.util.List;
import java.util.Map;

public interface SurveyService {
  SurveyDTO createSurvey(CreateSurveyRequestDTO survey, String userCreatorId);
  SurveyDTO getSurveyById(String id);
  void deleteSurvey(String id);
  SurveyDTO openSurvey(String id);
  SurveyDTO closeSurvey(String id);   // cierra
  List<SurveyDTO> listOpenSurveysForEvent(String eventId);
  List<SurveyDTO> listAllSurveysForEvent(String eventId);
  SurveyResponseDTO respondYesNoMaybeToSurvey(String surveyId, String userId, RespondYesNoMaybeRequestDTO answer);
  Map<YesNoMaybeAnswer, Long> resultsYesNoMaybeOfSurvey(String surveyId);
  List<SurveyResponseDTO> completeResultsOfSurvey(String surveyId);
}
