package com.tfm.bandas.surveys.service.impl;

import com.tfm.bandas.surveys.client.EventsClient;
import com.tfm.bandas.surveys.dto.*;
import com.tfm.bandas.surveys.dto.mapper.SurveyMapper;
import com.tfm.bandas.surveys.model.entity.SurveyEntity;
import com.tfm.bandas.surveys.model.repository.SurveyRepository;
import com.tfm.bandas.surveys.model.specification.SurveySpecifications;
import com.tfm.bandas.surveys.service.SurveyService;
import com.tfm.bandas.surveys.utils.SurveyStatus;
import com.tfm.bandas.surveys.utils.SurveyType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.tfm.bandas.surveys.utils.EtagUtils.compareVersion;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
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

        // Validar unicidad: solo puede haber una encuesta de tipo ATTENDANCE por evento
        SurveyType surveyType = entity.getSurveyType();
        if (surveyType == SurveyType.ATTENDANCE) {
            if (surveyRepository.existsByEventIdAndSurveyType(survey.eventId(), SurveyType.ATTENDANCE)) {
                throw new IllegalArgumentException("An ATTENDANCE survey already exists for event: " + survey.eventId());
            }
        }

        entity.setCreatedBy(userCreatorId);
        return SurveyMapper.toDto(surveyRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyDTO getSurveyById(String suveyId) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        return SurveyMapper.toDto(survey);
    }

    @Override
    @Transactional
    public void deleteSurvey(String suveyId, int ifMatchVersion) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        compareVersion(ifMatchVersion, survey.getVersion());
        if (survey.getStatus() == SurveyStatus.OPEN)
            throw new IllegalStateException("Cannot delete an OPEN survey");
        surveyRepository.delete(survey);
    }

    @Override
    public SurveyDTO updateSurvey(String surveyId, int ifMatchVersion, UpdateSurveyRequestDTO survey) {
        survey.validateWindow();

        SurveyEntity surveyEntity = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Survey not found: " + surveyId));

        compareVersion(ifMatchVersion, surveyEntity.getVersion());

        // Validar unicidad si se intenta cambiar a ATTENDANCE
        // Solo validar si el tipo está cambiando Y el nuevo tipo es ATTENDANCE
        SurveyType currentType = surveyEntity.getSurveyType();
        SurveyType newType = survey.surveyType();

        if (newType == SurveyType.ATTENDANCE && currentType != SurveyType.ATTENDANCE) {
            // Se está intentando cambiar a ATTENDANCE, verificar que no exista otra
            if (surveyRepository.existsByEventIdAndSurveyType(surveyEntity.getEventId(), SurveyType.ATTENDANCE)) {
                throw new IllegalArgumentException("An ATTENDANCE survey already exists for event: " + surveyEntity.getEventId());
            }
        }

        // Reglas por estado
        switch (surveyEntity.getStatus()) {
            case DRAFT -> applyPutDraft(surveyEntity, survey);
            case OPEN  -> applyPutOpen(surveyEntity, survey);
            default    -> throw new IllegalStateException("Survey in " + surveyEntity.getStatus() + " cannot be modified");
        }

        // Validación final de ventana
        Instant o = surveyEntity.getOpensAt(), c = surveyEntity.getClosesAt();
        if (o != null && c != null && o.isAfter(c)) {
            throw new IllegalArgumentException("opensAt must be <= closesAt");
        }

        SurveyEntity saved = surveyRepository.saveAndFlush(surveyEntity);
        return SurveyMapper.toDto(saved);
    }

    @Override
    @Transactional
    public SurveyDTO openSurvey(String suveyId, int ifMatchVersion) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        compareVersion(ifMatchVersion, survey.getVersion());
        // Si ya está abierto, no hacer nada, para otros estados, abrirlo
        if (survey.getStatus() == SurveyStatus.OPEN) return SurveyMapper.toDto(survey);
        survey.setStatus(SurveyStatus.OPEN);
        return SurveyMapper.toDto(surveyRepository.saveAndFlush(survey));
    }

    @Override
    @Transactional
    public SurveyDTO closeSurvey(String suveyId, int ifMatchVersion) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();
        compareVersion(ifMatchVersion, survey.getVersion());

        if (survey.getStatus() == SurveyStatus.CLOSED || survey.getStatus() == SurveyStatus.CANCELLED) return SurveyMapper.toDto(survey);
        survey.setStatus(SurveyStatus.CLOSED);
        return SurveyMapper.toDto(surveyRepository.saveAndFlush(survey));
    }

    @Override
    @Transactional
    public SurveyDTO cancelSurvey(String suveyId, int ifMatchVersion) {
        SurveyEntity survey = surveyRepository.findById(suveyId).orElseThrow();

        compareVersion(ifMatchVersion, survey.getVersion());
        if (survey.getStatus() == SurveyStatus.CANCELLED) return SurveyMapper.toDto(survey);
        survey.setStatus(SurveyStatus.CANCELLED);
        return SurveyMapper.toDto(surveyRepository.saveAndFlush(survey));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SurveyDTO> listAllSurveysForEvent(String eventId, Pageable pageable) {
        return surveyRepository.findByEventId(eventId, pageable)
                .map(SurveyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SurveyDTO> listOpenSurveysForEvent(String eventId, Pageable pageable) {
        return surveyRepository.findOpenSurveysOfEvent(eventId, Instant.now(), pageable)
                .map(SurveyMapper::toDto);
    }

    @Override
    public Page<SurveyDTO> searchSurveys(
            String qText, String title, String description, String eventId, SurveyStatus status,
            Instant opensFrom, Instant opensTo, Instant closesFrom, Instant closesTo,
            Pageable pageable) {

        Specification<SurveyEntity> spec = Specification.allOf(
                SurveySpecifications.all(),
                SurveySpecifications.text(qText),
                SurveySpecifications.titleContains(title),
                SurveySpecifications.descriptionContains(description),
                SurveySpecifications.eventIdEquals(eventId),
                SurveySpecifications.statusEquals(status),
                SurveySpecifications.opensAtFrom(opensFrom),
                SurveySpecifications.opensAtTo(opensTo),
                SurveySpecifications.closesAtFrom(closesFrom),
                SurveySpecifications.closesAtTo(closesTo));

        return surveyRepository.findAll(spec, pageable).map(SurveyMapper::toDto); // usa tu mapper
    }


    private void applyPutDraft(SurveyEntity e, UpdateSurveyRequestDTO dto) {
        e.setTitle(dto.title());
        e.setDescription(dto.description());
        e.setSurveyType(dto.surveyType());
        e.setOpensAt(dto.opensAt());
        e.setClosesAt(dto.closesAt());
    }

    private void applyPutOpen(SurveyEntity e, UpdateSurveyRequestDTO dto) {
        e.setTitle(dto.title());
        e.setDescription(dto.description());
        e.setSurveyType(dto.surveyType());

        // opensAt no se puede modificar en OPEN
        if (!dto.opensAt().equals(e.getOpensAt())) {
            throw new IllegalArgumentException("Cannot modify opensAt while survey is OPEN");
        }

        // closesAt obligatorio y solo se puede mantener o AMPLIAR
        if (dto.closesAt().isBefore(e.getClosesAt())) {
            throw new IllegalArgumentException("Cannot shorten closesAt while survey is OPEN");
        }
        e.setClosesAt(dto.closesAt());
    }

}
