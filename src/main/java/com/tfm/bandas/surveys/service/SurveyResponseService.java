package com.tfm.bandas.surveys.service;

import com.tfm.bandas.surveys.dto.RespondYesNoMaybeRequestDTO;
import com.tfm.bandas.surveys.dto.SurveyResponseDTO;
import com.tfm.bandas.surveys.dto.UpdateSurveyResponseRequestDTO;
import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface SurveyResponseService {

    SurveyResponseDTO respondYesNoMaybeToSurvey(String surveyId, String userId, RespondYesNoMaybeRequestDTO answer);
    Map<YesNoMaybeAnswer, Long> resultsYesNoMaybeOfSurvey(String surveyId);
    Page<SurveyResponseDTO> completeResultsOfSurvey(String surveyId, Pageable pageable);

    SurveyResponseDTO findMyResponse(String surveyId, String userId);

    SurveyResponseDTO updateMyResponse(String surveyId, String userId, int ifMatchVersion, UpdateSurveyResponseRequestDTO dto);
    void deleteMyResponse(String surveyId, String userId, int ifMatchVersion);
    // Admin
    SurveyResponseDTO updateUserResponse(String surveyId, String targetUserId, int ifMatchVersion, UpdateSurveyResponseRequestDTO dto);
    void deleteUserResponse(String surveyId, String targetUserId, int ifMatchVersion);
}
