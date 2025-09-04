package io.github.jaron2668.skyblockupdater.repository;

import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.model.Enchantment;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
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
            CREATE TABLE IF NOT EXISTS auctions (
                id UUID PRIMARY KEY,
                item_id TEXT NOT NULL,
                item_name TEXT NOT NULL,
                start_time TIMESTAMPTZ NOT NULL,
                end_time TIMESTAMPTZ NOT NULL,
                price BIGINT NOT NULL,
                dungeon_stars INTEGER NOT NULL,
                reforge TEXT NOT NULL,
                rarity TEXT NOT NULL,
                hpb_count INTEGER NOT NULL,
                fpb_count INTEGER NOT NULL,
                has_art_of_war BOOLEAN NOT NULL,
                has_art_of_peace BOOLEAN NOT NULL,
                is_recombobulated BOOLEAN NOT NULL
            );
            CREATE TABLE IF NOT EXISTS enchantments (
                id UUID PRIMARY KEY,
                enchantment TEXT NOT NULL,
                level INTEGER NOT NULL,
                FOREIGN KEY (id) REFERENCES auctions(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        jdbc.execute(sql);
    }

    public void save(Auction auction) {
        String sql = """
            INSERT INTO auctions (
                id,
                item_id,
                item_name,
                start_time,
                end_time,
                price,
                dungeon_stars,
                reforge,
                rarity,
                hpb_count,
                fpb_count,
                has_art_of_war,
                has_art_of_peace,
                is_recombobulated
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
        """;

        jdbc.update(sql,
                auction.getId(),
                auction.getItemId(),
                auction.getItemName(),
                Timestamp.from(auction.getStartTime()),
                Timestamp.from(auction.getEndTime()),
                auction.getPrice(),
                auction.getDungeonStars(),
                auction.getReforge(),
                auction.getRarity(),
                auction.getHpbCount(),
                auction.getFpbCount(),
                auction.hasArtOfWar(),
                auction.hasArtOfPeace(),
                auction.isRecombobulated()
        );

        List<Enchantment> enchantments = auction.getImportantEnchantments();
        if(enchantments.isEmpty())
            return;

        sql = """
            INSERT INTO TABLE enchantments(id, enchantment, level)
            VALUES (?, ?, ?)
            ON CONFLICT DO NOTHING;
        """;
        UUID auctionId = auction.getId();
        for (Enchantment enchantment : enchantments) {
            jdbc.update(sql,
                    auctionId,
                    enchantment.getType().toString(),
                    enchantment.getLevel()
            );
        }
    }


    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM auctions WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

}
