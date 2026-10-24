package com.example.helloworld.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class KafkaMessageConsumer {
    private final ConcurrentLinkedQueue<String> receivedMessages = new ConcurrentLinkedQueue<>();

    @KafkaListener(topics = "test-topic")
    public void receiveMessage(String message) {
        receivedMessages.add(message);
        System.out.println("Received location: " + message);
    }

    public List<String> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }
}
