package io.github.jaron2668.skyblockupdater.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.service.AuctionFetcherService;
import io.github.jaron2668.skyblockupdater.service.AuctionProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AuctionScheduler {

    @Autowired
    private AuctionFetcherService fetcher;

    @Autowired
    private AuctionProcessorService processor;

    @Scheduled(fixedRate = 5000)
    public void pollActiveAuctions() {
        List<Auction> auctions = fetcher.fetchActiveAuctions();
        processor.processNewAuctions(auctions);
    }

    @Scheduled(fixedRate = 15000)
    public void pollEndedAuctions() {
        List<JsonNode> auctions = fetcher.fetchEndedAuctions();
        processor.processEndedAuctions(auctions);
    }
}
