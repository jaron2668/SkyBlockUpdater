package io.github.jaron2668.skyblockupdater.service;

import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.repository.AuctionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuctionProcessorService {

    @Autowired
    private AuctionDao auctionDao;

    @Autowired
    private KafkaPublisherService kafkaPublisher;

    public void processAuctions(List<Auction> auctions) {
        for (Auction auction : auctions) {
            if (!auctionDao.existsActiveById(auction.getId())) {
                kafkaPublisher.publish(auction);
                auctionDao.saveActiveAuction(auction);
            }
        }
    }
}
