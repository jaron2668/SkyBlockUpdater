package io.github.jaron2668.skyblockupdater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaron2668.skyblockupdater.model.Auction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaPublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String TOPIC_NEW = "updater-newauction";
    private final String TOPIC_ENDED = "updater-endedauction";

    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishNewAuction(Auction auction) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(auction);
            kafkaTemplate.send(TOPIC_NEW, auction.getId().toString(), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishEndedAuction(UUID auctionUuid) {
        try {
            kafkaTemplate.send(TOPIC_NEW, auctionUuid.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

