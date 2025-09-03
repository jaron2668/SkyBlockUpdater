package io.github.jaron2668.skyblockupdater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaron2668.skyblockupdater.model.Auction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String TOPIC = "updater-newauction";

    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Auction auction) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(auction);
            kafkaTemplate.send(TOPIC, auction.getId().toString(), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

