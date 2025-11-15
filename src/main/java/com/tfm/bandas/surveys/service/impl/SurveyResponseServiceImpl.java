package com.tfm.bandas.surveys.service.impl;

import com.tfm.bandas.surveys.dto.RespondYesNoMaybeRequestDTO;
import com.tfm.bandas.surveys.dto.SurveyResponseDTO;
import com.tfm.bandas.surveys.dto.UpdateSurveyResponseRequestDTO;
import com.tfm.bandas.surveys.dto.mapper.SurveyResponseMapper;
import com.tfm.bandas.surveys.exception.PreconditionFailedException;
import com.tfm.bandas.surveys.model.entity.SurveyEntity;
import com.tfm.bandas.surveys.model.entity.SurveyResponseEntity;
import com.tfm.bandas.surveys.model.repository.SurveyRepository;
import com.tfm.bandas.surveys.model.repository.SurveyResponseRepository;
import com.tfm.bandas.surveys.service.SurveyResponseService;
import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.tfm.bandas.surveys.utils.EtagUtils.compareVersion;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyResponseServiceImpl implements SurveyResponseService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyRepository surveyRepository;

    @Override
    @Transactional
    public SurveyResponseDTO respondYesNoMaybeToSurvey(String surveyId, String userId, RespondYesNoMaybeRequestDTO answer) {
        SurveyEntity survey = getSurvey(surveyId);
        ensureRespondable(survey);

        SurveyResponseEntity response = surveyResponseRepository.findBySurveyIdAndUserIamId(surveyId, userId)
                .orElseGet(SurveyResponseEntity::new);
        response.setSurveyId(surveyId);
        response.setUserIamId(userId);
        response.setAnswerYesNoMaybe(answer.answer());
        response.setComment(answer.comment());
        return SurveyResponseMapper.toDto(surveyResponseRepository.saveAndFlush(response)); // upsert - si no existe lo crea y si existe lo actualiza
    }

    @Override
    @Transactional(readOnly = true)
    public Map<YesNoMaybeAnswer, Long> resultsYesNoMaybeOfSurvey(String surveyId) {
        SurveyEntity survey = getSurvey(surveyId);
        if (survey.getResponseType() != ResponseType.YES_NO_MAYBE) {
            throw new IllegalStateException("Survey response type is not YES_NO_MAYBE");
        }
        Map<YesNoMaybeAnswer, Long> answers = new EnumMap<>(YesNoMaybeAnswer.class);
        for (YesNoMaybeAnswer y : YesNoMaybeAnswer.values()) {
            Long count = surveyResponseRepository.countBySurveyIdAndAnswerYesNoMaybe(surveyId, y);
            answers.put(y, count);
        }
        return answers;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SurveyResponseDTO> completeResultsOfSurvey(String surveyId, Pageable pageable) {
        return surveyResponseRepository.findBySurveyId(surveyId, pageable)
                .map(SurveyResponseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyResponseDTO findMyResponse(String surveyId, String userId) {
        getSurvey(surveyId);
        return surveyResponseRepository.findBySurveyIdAndUserIamId(surveyId, userId)
                .map(SurveyResponseMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Response not found for user"));
    }

    @Override
    public SurveyResponseDTO updateMyResponse(String surveyId, String userId, int ifMatchVersion, UpdateSurveyResponseRequestDTO dto) {
        SurveyEntity survey = getSurvey(surveyId);
        ensureRespondable(survey);

        SurveyResponseEntity response = surveyResponseRepository.findBySurveyIdAndUserIamId(surveyId, userId)
                .orElseThrow(() -> new NoSuchElementException("Response not found for user"));

        compareVersion(ifMatchVersion, response.getVersion());
        response.setAnswerYesNoMaybe(dto.answer());
        response.setComment(dto.comment());
        response.setAnsweredAt(Instant.now());

        return SurveyResponseMapper.toDto(surveyResponseRepository.saveAndFlush(response));
    }

    @Override
    public void deleteMyResponse(String surveyId, String userId, int ifMatchVersion) {
        SurveyEntity survey = getSurvey(surveyId);
        ensureRespondable(survey);

        SurveyResponseEntity response = surveyResponseRepository.findBySurveyIdAndUserIamId(surveyId, userId)
                .orElseThrow(() -> new NoSuchElementException("Response not found for user"));

        compareVersion(ifMatchVersion, response.getVersion());
        surveyResponseRepository.delete(response);
    }

    // ---- Admin variants ----
    @Override
    public SurveyResponseDTO updateUserResponse(String surveyId, String targetUserId, int ifMatchVersion, UpdateSurveyResponseRequestDTO dto) {
        return updateMyResponse(surveyId, targetUserId, ifMatchVersion, dto);
    }

    @Override
    public void deleteUserResponse(String surveyId, String targetUserId, int ifMatchVersion) {
        deleteMyResponse(surveyId, targetUserId, ifMatchVersion);
    }

    private SurveyEntity getSurvey(String surveyId) {
        return surveyRepository.findById(surveyId).orElseThrow(() -> new NoSuchElementException("Survey not found: " + surveyId));
    }

    // ---- helpers ----
    private void ensureRespondable(SurveyEntity survey) {
        Instant now = Instant.now();
        if (survey.getStatus() != SurveyStatus.OPEN) throw new IllegalStateException("Survey not open");
        if (survey.getOpensAt() != null && now.isBefore(survey.getOpensAt())) throw new IllegalStateException("Before opensAt");
        if (survey.getClosesAt() != null && now.isAfter(survey.getClosesAt())) throw new IllegalStateException("After closesAt");
    }

}
