package io.github.jaron2668.skyblockupdater.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.repository.AuctionDao;
import io.github.jaron2668.skyblockupdater.util.AttributeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuctionProcessorService {

    private static final Logger LOG = LoggerFactory.getLogger(AuctionProcessorService.class);

    @Autowired
    private AuctionDao auctionDao;

    @Autowired
    private KafkaPublisherService kafkaPublisher;

    /**
     * Processes new auctions
     *
     * @param auctions new auctions to process
     */
    public void processNewAuctions(List<Auction> auctions) {
        for (Auction auction : auctions) {
            if (!auctionDao.existsActiveById(auction.getId())) {
                kafkaPublisher.publishNewAuction(auction);
                auctionDao.saveActiveAuction(auction);
            }
        }
    }

    /**
     * Processes ended auctions
     *
     * @param auctionsJsons list of fetch action jsons
     */
    public void processEndedAuctions(List<JsonNode> auctionsJsons) {
        int movedAuctions = 0;
        int newAuctions = 0;
        int deletedAuctions = 0;

        for (JsonNode json : auctionsJsons) {
            boolean bought = json.has("buyer") && !json.get("buyer").asText().isBlank(); // TODO: not really sure if this works because the documentation isn't very specific
            UUID uuid = AttributeParser.parseHypixelUuid(json.get("auction_id").asText());
            Instant timeEnded = Instant.ofEpochMilli(json.get("timestamp").asLong());

            kafkaPublisher.publishEndedAuction(uuid);

            if(bought) {
                if(auctionDao.existsActiveById(uuid)) {
                    auctionDao.moveAuctionToEnded(uuid, timeEnded);
                    movedAuctions++;
                } else {
                    Auction auction = new Auction();
                    auction.setId(uuid);
                    auction.setItemBytes(json.get("item_bytes").asText());
                    auction.setPrice(json.get("price").asLong());

                    AttributeParser.parseAttributes(auction);

                    auctionDao.saveEndedAuction(auction, timeEnded);
                    newAuctions++;
                }
            } else {
                auctionDao.deleteActiveAuction(uuid);
                deletedAuctions++;
            }
        }

        LOG.info("Moved {} existing auctions to ended auctions.", movedAuctions);
        LOG.info("Inserted {} ended auctions that weren't tracked before.", newAuctions);
        LOG.info("Deleted {} auctions that weren't bought.", deletedAuctions);
    }
}
