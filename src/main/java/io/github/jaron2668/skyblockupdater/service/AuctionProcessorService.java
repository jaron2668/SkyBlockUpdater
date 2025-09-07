package io.github.jaron2668.skyblockupdater.service;

import io.github.jaron2668.skyblocksharedmodels.AuctionActive;
import io.github.jaron2668.skyblocksharedmodels.AuctionEnded;
import io.github.jaron2668.skyblockupdater.repository.AuctionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void processNewAuctions(List<AuctionActive> auctions) {
        for (AuctionActive auction : auctions) {
            kafkaPublisher.publishNewAuction(auction);
            auctionDao.saveAuction(auction);
        }
    }

    /**
     * Processes ended auctions
     *
     * @param auctions list of new ended auctions
     */
    public void processEndedAuctions(List<AuctionEnded> auctions) {
        int movedAuctions = 0;
        int newAuctions = 0;
        int deletedAuctions = 0;

        for (AuctionEnded auction : auctions) {
            UUID uuid = auction.getUuid();
            kafkaPublisher.publishEndedAuction(uuid);

            boolean exists = auctionDao.existsActiveByUuid(uuid);
            if(auction.wasBought()) {
                if (exists) {
                    auctionDao.moveAuctionToBought(uuid,auction.getTimeEnded());
                    movedAuctions++;
                } else {
                    auctionDao.saveBoughtAuction(auction);
                    newAuctions++;
                }
            } else {
                if (exists) {
                    auctionDao.deleteActiveAuctionAndItem(uuid);
                    deletedAuctions++;
                }
            }
        }

        LOG.info("Moved {} existing auctions to ended auctions.", movedAuctions);
        LOG.info("Inserted {} ended auctions that weren't tracked before.", newAuctions);
        LOG.info("Deleted {} auctions that weren't bought.", deletedAuctions);
    }
}
