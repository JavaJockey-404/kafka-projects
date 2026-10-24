package com.example.helloworld.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    private final KafkaMessageProducer producer;
    private final KafkaMessageConsumer consumer;
    private final ObjectMapper objectMapper;

    public LocationController(KafkaMessageProducer producer, KafkaMessageConsumer consumer, ObjectMapper objectMapper) {
        this.producer = producer;
        this.consumer = consumer;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/messages")
    public List<String> getReceivedMessages() {
        return consumer.getReceivedMessages();
    }

    @PostMapping
    public ResponseEntity<String> publishLocation(@RequestBody LocationRequest location) {
        validate(location);

        try {
            String message = objectMapper.writeValueAsString(location);
            producer.sendMessage(location.getLatitude() + "," + location.getLongitude(), message);
            return ResponseEntity.accepted().body("Location sent to Kafka.");
        } catch (JsonProcessingException | ExecutionException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not send location to Kafka.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Sending location to Kafka was interrupted.", e);
        }
    }

    private void validate(LocationRequest location) {
        if (location == null
                || location.getLatitude() == null
                || location.getLongitude() == null
                || !Double.isFinite(location.getLatitude())
                || !Double.isFinite(location.getLongitude())
                || location.getLatitude() < -90 || location.getLatitude() > 90
                || location.getLongitude() < -180 || location.getLongitude() > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Latitude must be between -90 and 90, and longitude between -180 and 180.");
        }
    }
}
