package com.tfm.bandas.surveys.service.impl;

import com.tfm.bandas.surveys.client.EventsClient;
import com.tfm.bandas.surveys.dto.CreateSurveyRequestDTO;
import com.tfm.bandas.surveys.dto.RespondYesNoMaybeRequestDTO;
import com.tfm.bandas.surveys.dto.SurveyDTO;
import com.tfm.bandas.surveys.dto.SurveyResponseDTO;
import com.tfm.bandas.surveys.dto.mapper.SurveyMapper;
import com.tfm.bandas.surveys.dto.mapper.SurveyResponseMapper;
import com.tfm.bandas.surveys.model.entity.SurveyEntity;
import com.tfm.bandas.surveys.model.entity.SurveyResponseEntity;
import com.tfm.bandas.surveys.model.repository.SurveyRepository;
import com.tfm.bandas.surveys.model.repository.SurveyResponseRepository;
import com.tfm.bandas.surveys.service.SurveyService;
import com.tfm.bandas.surveys.utils.ResponseType;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final EventsClient eventsClient;

    @Override
    @Transactional
    public SurveyDTO createSurvey(CreateSurveyRequestDTO survey, String userCreatorId) {
        // Validar fechas
        if (survey.opensAt() != null && survey.closesAt() != null && survey.opensAt().isAfter(survey.closesAt())) {
            throw new IllegalArgumentException("opensAt must be <= closesAt");
        }

        // Validar que el eventId existe llamando al cliente de eventos
        if (!eventsClient.existsEventById(survey.eventId())) {
            throw new IllegalArgumentException("eventId not found: " + survey.eventId());
        }

        SurveyEntity entity = SurveyMapper.toEntity(survey);
        entity.setCreatedBy(userCreatorId);
        return SurveyMapper.toDto(surveyRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyDTO getSurveyById(String suveyId) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        return SurveyMapper.toDto(survey);
    }

    @Override
    @Transactional
    public void deleteSurvey(String suveyId) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        if (survey.getStatus() == SurveyStatus.OPEN)
            throw new IllegalStateException("Cannot delete an OPEN survey");
        surveyRepository.delete(survey);
    }

    @Override
    @Transactional
    public SurveyDTO openSurvey(String suveyId) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        // Si ya está abierto, no hacer nada, para otros estados, abrirlo
        if (survey.getStatus() == SurveyStatus.OPEN) return SurveyMapper.toDto(survey);
        survey.setStatus(SurveyStatus.OPEN);
        return SurveyMapper.toDto(surveyRepository.save(survey));
    }

    @Override
    @Transactional
    public SurveyDTO closeSurvey(String suveyId) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        if (survey.getStatus() == SurveyStatus.CLOSED || survey.getStatus() == SurveyStatus.CANCELLED) return SurveyMapper.toDto(survey);
        survey.setStatus(SurveyStatus.CLOSED);
        return SurveyMapper.toDto(surveyRepository.save(survey));
    }

    @Override
    @Transactional
    public SurveyDTO cancelSurvey(String suveyId) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        if (survey.getStatus() == SurveyStatus.CANCELLED) return SurveyMapper.toDto(survey);
        survey.setStatus(SurveyStatus.CANCELLED);
        return SurveyMapper.toDto(surveyRepository.save(survey));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurveyDTO> listAllSurveysForEvent(String eventId) {
        return surveyRepository.findByEventId(eventId).stream()
                .map(SurveyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurveyDTO> listOpenSurveysForEvent(String eventId) {
        return surveyRepository.findOpenSurveysOfEvent(eventId, Instant.now())
                .stream().map(SurveyMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SurveyResponseDTO respondYesNoMaybeToSurvey(String surveyId, String userId, RespondYesNoMaybeRequestDTO answer) {
        SurveyEntity survey = surveyRepository.findById(surveyId).orElseThrow();
        Instant now = Instant.now();
        if (survey.getStatus() != SurveyStatus.OPEN ||
            (survey.getOpensAt() != null && survey.getOpensAt().isAfter(now)) ||
            (survey.getClosesAt() != null && survey.getClosesAt().isBefore(now))) {
            throw new IllegalStateException("Survey is not open for responses");
        }
        SurveyResponseEntity response = surveyResponseRepository.findBySurveyIdAndUserIamId(surveyId, userId)
            .orElseGet(SurveyResponseEntity::new);
        response.setSurveyId(surveyId);
        response.setUserIamId(userId);
        response.setAnswerYesNoMaybe(answer.answer());
        response.setComment(answer.comment());
        return SurveyResponseMapper.toDto(surveyResponseRepository.save(response)); // upsert - si no existe lo crea y si existe lo actualiza
    }

    @Override
    @Transactional(readOnly = true)
    public Map<YesNoMaybeAnswer, Long> resultsYesNoMaybeOfSurvey(String surveyId) {
        SurveyEntity survey = surveyRepository.findById(surveyId).orElseThrow();
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
    public List<SurveyResponseDTO> completeResultsOfSurvey(String surveyId) {
        List<SurveyResponseEntity> responses = surveyResponseRepository.findBySurveyId(surveyId);
        return responses.stream()
                .map(SurveyResponseMapper::toDto)
                .collect(Collectors.toList());
    }
}
