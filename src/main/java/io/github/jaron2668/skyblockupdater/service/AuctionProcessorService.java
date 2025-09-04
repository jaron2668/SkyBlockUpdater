package io.github.jaron2668.skyblockupdater.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.repository.AuctionDao;
import io.github.jaron2668.skyblockupdater.util.AttributeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuctionProcessorService {

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
        for (JsonNode json : auctionsJsons) {
            boolean bought = json.has("buyer");
            UUID uuid = AttributeParser.parseHypixelUuid(json.get("auction_id").asText());
            Instant timeEnded = Instant.ofEpochMilli(json.get("timestamp").asLong());

            if(bought) {
                if(auctionDao.existsActiveById(uuid)) {
                    auctionDao.moveAuctionToEnded(uuid, timeEnded);
                } else {
                    Auction auction = new Auction();
                    auction.setId(uuid);
                    auction.setItemBytes(json.get("item_bytes").asText());
                    auction.setPrice(json.get("price").asLong());

                    AttributeParser.parseAttributes(auction);

                    auctionDao.saveEndedAuction(auction, timeEnded);
                }
            } else {
                auctionDao.deleteActiveAuction(uuid);
            }
        }
    }
}
