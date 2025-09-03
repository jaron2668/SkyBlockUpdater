package io.github.jaron2668.skyblockupdater.repository;

import io.github.jaron2668.skyblockupdater.model.Auction;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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
                price BIGINT NOT NULL,
                end_time TIMESTAMP NOT NULL
            )
            """;

        jdbc.execute(sql);
    }

    public void save(Auction auction) {
        String sql = "INSERT INTO auctions (id, item_id, item_name, price, end_time) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO NOTHING";

        jdbc.update(sql,
                auction.getId(),
                auction.getItemId(),
                auction.getItemName(),
                auction.getPrice(),
                Timestamp.from(auction.getEndTime())
        );
    }

    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM auctions WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

}
