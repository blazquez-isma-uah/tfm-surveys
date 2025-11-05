package com.tfm.bandas.surveys.client;

import com.tfm.bandas.surveys.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "eventsClient",
        url  = "${events.service.uri}",
        configuration = FeignSecurityConfig.class
)
public interface EventsFeignClient {

    @GetMapping("${events.service.exists-path}")
    ResponseEntity<Void> getEvent(@PathVariable("eventId") String eventId);

}
