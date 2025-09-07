package io.github.jaron2668.skyblockupdater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.jaron2668.skyblocksharedmodels.AuctionActive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaPublisherService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaPublisherService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String TOPIC_NEW = "updater-newauction";
    private final String TOPIC_ENDED = "updater-endedauction";

    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishNewAuction(AuctionActive auction) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(auction);
            kafkaTemplate.send(TOPIC_NEW, auction.getUuid().toString(), json);
        } catch (Exception e) {
            LOG.error("Couldn't publish kafka event with topic {}.",TOPIC_NEW,e);
        }
    }

    public void publishEndedAuction(UUID auctionUuid) {
        try {
            kafkaTemplate.send(TOPIC_ENDED, auctionUuid.toString());
        } catch (Exception e) {
            LOG.error("Couldn't publish kafka event with topic {}.",TOPIC_ENDED,e);
        }
    }
}

