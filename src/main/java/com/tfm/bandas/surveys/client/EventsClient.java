package com.tfm.bandas.surveys.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventsClient {

    private final EventsFeignClient feign;

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
