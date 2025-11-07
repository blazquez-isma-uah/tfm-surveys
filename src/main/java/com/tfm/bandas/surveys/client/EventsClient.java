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
     * Si el cliente Feign devuelve 2xx, el evento existe.
     * Si devuelve excepción NotFound (404), el evento no existe.
     * Otros errores de Feign se propagan hacia arriba.
     * @param eventId
     * @return
     */
    public boolean existsEventById(String eventId) {
       try {
           ResponseEntity<Void> response = feign.getEvent(eventId);
           // Si la respuesta es 2xx, el evento existe
           return response.getStatusCode().is2xxSuccessful();
       } catch (FeignException.NotFound e) {
           return false; // Event no encontrado, luego no existe
       }
       // Otros errores de Feign se propagan hacia arriba
    }
}
