package io.github.jaron2668.skyblockupdater.repository;

import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.model.Enchantment;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class AuctionDao {

    private final JdbcTemplate jdbc;

    public AuctionDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    @PostConstruct
    public void createTableIfNotExists() {
        String sql = """
            DROP TABLE IF EXISTS auctions_active;
            CREATE TABLE auctions_active (
                id UUID PRIMARY KEY,
                item_id TEXT NOT NULL,
                item_name TEXT NOT NULL,
                item_bytes TEXT NOT NULL,
                start_time TIMESTAMPTZ NOT NULL,
                end_time TIMESTAMPTZ NOT NULL,
                price BIGINT NOT NULL,
                upgrade_level INTEGER NOT NULL,
                reforge TEXT NOT NULL,
                rarity TEXT NOT NULL,
                hot_potato_count INTEGER NOT NULL,
                art_of_war_count INTEGER NOT NULL,
                art_of_peace_count INTEGER NOT NULL,
                rarity_upgrades INTEGER NOT NULL
            );
            CREATE TABLE IF NOT EXISTS auctions_ended (
                id UUID PRIMARY KEY,
                item_id TEXT NOT NULL,
                item_name TEXT NOT NULL,
                item_bytes TEXT NOT NULL,
                start_time TIMESTAMPTZ NOT NULL,
                end_time TIMESTAMPTZ NOT NULL,
                price BIGINT NOT NULL,
                upgrade_level INTEGER NOT NULL,
                reforge TEXT NOT NULL,
                rarity TEXT NOT NULL,
                hot_potato_count INTEGER NOT NULL,
                art_of_war_count INTEGER NOT NULL,
                art_of_peace_count INTEGER NOT NULL,
                rarity_upgrades INTEGER NOT NULL
            );
            CREATE TABLE IF NOT EXISTS enchantments_active (
                id UUID PRIMARY KEY,
                enchantment TEXT NOT NULL,
                level INTEGER NOT NULL,
                FOREIGN KEY (id) REFERENCES active_auctions(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
            CREATE TABLE IF NOT EXISTS enchantments_ended (
                id UUID PRIMARY KEY,
                enchantment TEXT NOT NULL,
                level INTEGER NOT NULL,
                FOREIGN KEY (id) REFERENCES ended_auctions(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        jdbc.execute(sql);
    }

    /**
     * Saves a new active auction
     * @param auction active auction to save
     */
    @Transactional
    public void saveActiveAuction(Auction auction) {
        String insertAuction = """
            INSERT INTO auctions_active (
                id,
                item_id,
                item_name,
                item_bytes,
                start_time,
                end_time,
                price,
                upgrade_level,
                reforge,
                rarity,
                hot_potato_count,
                art_of_war_count,
                art_of_peace_count,
                rarity_upgrades
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
        """;

        jdbc.update(insertAuction,
                auction.getId(),
                auction.getItemId(),
                auction.getItemName(),
                auction.getItemBytes(),
                Timestamp.from(auction.getStartTime()),
                Timestamp.from(auction.getEndTime()),
                auction.getPrice(),
                auction.getUpgradeLevel(),
                auction.getReforge(),
                auction.getRarity(),
                auction.getHotPotatoCount(),
                auction.getArtOfWarCount(),
                auction.getArtOfPeaceCount(),
                auction.getRarityUpgrades()
        );

        List<Enchantment> enchantments = auction.getImportantEnchantments();
        if(enchantments.isEmpty())
            return;

        String insertEnchantments = """
            INSERT INTO TABLE enchantments_active(id, enchantment, level)
            VALUES (?, ?, ?)
            ON CONFLICT DO NOTHING;
        """;
        UUID auctionId = auction.getId();
        for (Enchantment enchantment : enchantments) {
            jdbc.update(insertEnchantments,
                    auctionId,
                    enchantment.type().toString(),
                    enchantment.level()
            );
        }
    }

    /**
     * End an active auction
     * Removes the auction from auctions_active and inserts it into auctions_ended
     * @param auctionUUID UUID of auction which has ended
     */
    @Transactional
    public void endAuction(UUID auctionUUID) {
        // Copy auction to auction_ended
        String moveAuction = """
            INSERT INTO auctions_ended (
                id, item_id, item_name, item_bytes, start_time, end_time,
                price, upgrade_level, reforge, rarity,
                hot_potato_count, art_of_war_count, art_of_peace_count, rarity_upgrades
            )
            SELECT
                id, item_id, item_name, item_bytes, start_time, end_time,
                price, upgrade_level, reforge, rarity,
                hot_potato_count, art_of_war_count, art_of_peace_count, rarity_upgrades
            FROM auctions_active
            WHERE id = ?
        """;

        // Copy enchantments to enchantments_ended
        String moveEnchantments = """
            INSERT INTO enchantments_ended (id, enchantment, level)
            SELECT id, enchantment, level
            FROM enchantments_active
            WHERE id = ?
        """;

        // Delete from enchantments_active
        String deleteEnchantments = "DELETE FROM enchantments_active WHERE id = ?";

        // Delete from auction_active
        String deleteAuction = "DELETE FROM auctions_active WHERE id = ?";

        jdbc.update(moveAuction, auctionUUID);
        jdbc.update(moveEnchantments, auctionUUID);
        jdbc.update(deleteEnchantments, auctionUUID);
        jdbc.update(deleteAuction, auctionUUID);
    }


    /**
     * Checks if an active auction with passed uuid exists
     * @param id uuid to check
     * @return true if auction exists
     */
    public boolean existsActiveById(UUID id) {
        String sql = "SELECT COUNT(*) FROM active_auctions WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

}
