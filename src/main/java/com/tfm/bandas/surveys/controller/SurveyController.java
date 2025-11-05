package com.tfm.bandas.surveys.controller;

import com.tfm.bandas.surveys.dto.CreateSurveyRequestDTO;
import com.tfm.bandas.surveys.dto.RespondYesNoMaybeRequestDTO;
import com.tfm.bandas.surveys.dto.SurveyDTO;
import com.tfm.bandas.surveys.dto.SurveyResponseDTO;
import com.tfm.bandas.surveys.service.SurveyService;
import com.tfm.bandas.surveys.utils.YesNoMaybeAnswer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/surveys")
public class SurveyController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);
    private final SurveyService surveyService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public SurveyDTO createSurvey(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateSurveyRequestDTO createSurveyRequestDTO) {
        logger.info("Calling createSurvey with arguments: {}", createSurveyRequestDTO);
        String userId = jwt.getSubject();
        SurveyDTO response = surveyService.createSurvey(createSurveyRequestDTO, userId);
        logger.info("createSurvey by userId={} returning: {}", userId, response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/{surveyId}")
    public SurveyDTO getSurveyById(@PathVariable String surveyId) {
        logger.info("Calling getSurveyById with surveyId={}", surveyId);
        SurveyDTO response = surveyService.getSurveyById(surveyId);
        logger.info("getSurveyById returning: {}", response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{surveyId}")
    public void deleteSurvey(@PathVariable String surveyId) {
        logger.info("Calling deleteSurvey with surveyId={}", surveyId);
        surveyService.deleteSurvey(surveyId);
        logger.info("deleteSurvey Completed successfully for surveyId={}", surveyId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{surveyId}/open")
    public SurveyDTO openSurvey(@PathVariable String surveyId) {
        logger.info("Calling openSurvey with surveyId={}", surveyId);
        SurveyDTO response = surveyService.openSurvey(surveyId);
        logger.info("openSurvey Completed successfully for survey={}", response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{surveyId}/close")
    public SurveyDTO closeSurvey(@PathVariable String surveyId) {
        logger.info("Calling closeSurvey with surveyId={}", surveyId);
        SurveyDTO response = surveyService.closeSurvey(surveyId);
        logger.info("closeSurvey Completed successfully for survey={}", response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{surveyId}/cancel")
    public SurveyDTO cancelSurvey(@PathVariable String surveyId) {
        logger.info("Calling cancelSurvey with surveyId={}", surveyId);
        SurveyDTO response = surveyService.cancelSurvey(surveyId);
        logger.info("cancelSurvey Completed successfully for survey={}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/listOpen/{eventId}")
    public List<SurveyDTO> listOpenSurveyByEventId(@PathVariable String eventId) {
        logger.info("Calling listOpenSurveyByEventId with eventId={}", eventId);
        List<SurveyDTO> response = surveyService.listOpenSurveysForEvent(eventId);
        logger.info("listOpenSurveyByEventId returning {} surveys for eventId={}", response.size(), eventId);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @GetMapping("/listAll/{eventId}")
    public List<SurveyDTO> listAllSurveyByEventId(@PathVariable String eventId) {
        logger.info("Calling listAllSurveyByEventId with eventId={}", eventId);
        List<SurveyDTO> response = surveyService.listAllSurveysForEvent(eventId);
        logger.info("listAllSurveyByEventId returning {} surveys for eventId={}", response.size(), eventId);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
    @PostMapping("/{surveyId}/responses")
    public SurveyResponseDTO respondToSurvey(@AuthenticationPrincipal Jwt jwt, @PathVariable String surveyId,
                                             @Valid @RequestBody RespondYesNoMaybeRequestDTO respondYesNoMaybeRequestDTO) {
        logger.info("Calling respondToSurvey with surveyId={}, respondYesNoMaybeRequestDTO={}", surveyId, respondYesNoMaybeRequestDTO);
        String userId = jwt.getSubject();
        SurveyResponseDTO response = surveyService.respondYesNoMaybeToSurvey(surveyId, userId, respondYesNoMaybeRequestDTO);
        logger.info("respondToSurvey by userId={} returning: {}", userId, response);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("yesNoMaybeResults/{surveyId}")
    public Map<YesNoMaybeAnswer, Long> resultsYesNoMaybeOfSurvey(@PathVariable String surveyId) {
        logger.info("Calling getSurveyResults with surveyId={}", surveyId);
        Map<YesNoMaybeAnswer, Long> response = surveyService.resultsYesNoMaybeOfSurvey(surveyId);
        logger.info("getSurveyResults returning: {}", response);
        return response;
    }

    // completeResultsOfSurvey
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("completeResults/{surveyId}")
    public List<SurveyResponseDTO> completeResultsOfSurvey(@PathVariable String surveyId) {
        logger.info("Calling completeResultsOfSurvey with surveyId={}", surveyId);
        List<SurveyResponseDTO> response = surveyService.completeResultsOfSurvey(surveyId);
        logger.info("completeResultsOfSurvey returning {} responses for surveyId={}", response.size(), surveyId);
        return response;
    }
}

