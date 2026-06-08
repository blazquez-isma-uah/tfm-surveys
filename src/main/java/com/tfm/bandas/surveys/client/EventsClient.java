package com.tfm.bandas.surveys.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventsClient {

    private final EventsFeignClient feign;

    /**
     * Verifica si un evento existe en el servicio de eventos por su ID.
     * Si Feign no lanza excepción, el evento existe (2xx).
     * Si lanza FeignException. NotFound (404), el evento no existe.
     * Otros errores de Feign se propagan hacia arriba.
     */
    public boolean existsEventById(String eventId) {
        try {
            feign.getEvent(eventId);
            return true;
        } catch (FeignException.NotFound e) {
            return false;
        }
        // Otros errores se propagan — el servicio de surveys los manejará
    }
}
