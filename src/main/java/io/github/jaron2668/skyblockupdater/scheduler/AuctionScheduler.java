package io.github.jaron2668.skyblockupdater.scheduler;

import io.github.jaron2668.skyblocksharedmodels.AuctionActive;
import io.github.jaron2668.skyblocksharedmodels.AuctionEnded;
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

    @Scheduled(fixedRate = 1000 * 60 * 3)
    public void pollActiveAuctions() {
        List<AuctionActive> auctions = fetcher.fetchActiveAuctions();
        processor.processNewAuctions(auctions);
    }

    @Scheduled(fixedRate = 1000 * 50)
    public void pollEndedAuctions() {
        List<AuctionEnded> auctions = fetcher.fetchEndedAuctions();
        processor.processEndedAuctions(auctions);
    }
}
