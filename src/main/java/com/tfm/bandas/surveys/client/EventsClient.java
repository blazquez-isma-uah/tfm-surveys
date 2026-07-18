package com.tfm.bandas.surveys.client;

import com.tfm.bandas.surveys.exception.EventNotFoundException;
import com.tfm.bandas.surveys.exception.EventsServiceUnavailableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventsClient {

    private static final Logger logger = LoggerFactory.getLogger(EventsClient.class);

    private final EventsFeignClient feign;

    /**
     * Verifica que un evento exista en el servicio de eventos por su ID.
     * Si el evento no existe, lanza EventNotFoundException (404).
     * Si la comunicación con el servicio de eventos falla por cualquier otro
     * motivo (timeout, error 5xx, etc.), lanza EventsServiceUnavailableException.
     */
    public void validateEventExists(String eventId) {
        try {
            feign.getEvent(eventId);
        } catch (FeignException.NotFound e) {
            throw new EventNotFoundException("No se puede crear la encuesta: el evento indicado no existe.");
        } catch (FeignException e) {
            logger.error("Error al comunicarse con el servicio de eventos (eventId={})", eventId, e);
            throw new EventsServiceUnavailableException("El servicio de eventos no está disponible en este momento. Inténtalo de nuevo más tarde.");
        }
    }
}
