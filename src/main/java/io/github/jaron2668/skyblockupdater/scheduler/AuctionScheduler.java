package io.github.jaron2668.skyblockupdater.scheduler;

import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.service.AuctionFetcherService;
import io.github.jaron2668.skyblockupdater.service.AuctionProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuctionScheduler {

    @Autowired
    private AuctionFetcherService fetcher;

    @Autowired
    private AuctionProcessorService processor;

    @Scheduled(fixedRate = 5000)
    public void pollHypixelApi() {
        List<Auction> auctions = fetcher.fetchActiveAuctions();
        processor.processAuctions(auctions);
    }
}
