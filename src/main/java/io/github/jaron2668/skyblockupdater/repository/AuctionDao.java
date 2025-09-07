package io.github.jaron2668.skyblockupdater.repository;

import io.github.jaron2668.skyblocksharedmodels.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Transactional
@Repository
public class AuctionDao {

    private final JdbcTemplate jdbc;

    private static final Logger LOG = LoggerFactory.getLogger(AuctionDao.class);

    public AuctionDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    @PostConstruct
    public void setupTables() {
        String createItems = """
            CREATE TABLE IF NOT EXISTS items (
                uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                item_bytes TEXT NOT NULL,
                rarity TEXT,
                remaining_tags_dump TEXT NOT NULL,
                PRIMARY KEY (uuid,item_id)
            );
        """;

        String createToolItems = """
            CREATE TABLE IF NOT EXISTS tool_items (
                uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                upgrade_level INTEGER NOT NULL,
                reforge TEXT NOT NULL,
                hot_potato_count INTEGER NOT NULL,
                rarity_upgrades INTEGER NOT NULL,
                PRIMARY KEY (uuid,item_id),
                FOREIGN KEY (uuid,item_id) REFERENCES items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createWeaponItems = """
            CREATE TABLE IF NOT EXISTS weapon_items (
                uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                art_of_war_count INTEGER NOT NULL,
                PRIMARY KEY (uuid,item_id),
                FOREIGN KEY (uuid,item_id) REFERENCES tool_items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createArmorItems = """
            CREATE TABLE IF NOT EXISTS armor_items (
                uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                art_of_peace_count INTEGER NOT NULL,
                PRIMARY KEY (uuid,item_id),
                FOREIGN KEY (uuid,item_id) REFERENCES tool_items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createPetItems = """
            CREATE TABLE IF NOT EXISTS pet_items (
                uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                level INTEGER NOT NULL,
                candy_count INTEGER NOT NULL,
                pet_item TEXT NOT NULL,
                PRIMARY KEY (uuid,item_id),
                FOREIGN KEY (uuid,item_id) REFERENCES items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createEnchantments = """
            CREATE TABLE IF NOT EXISTS enchantments (
                uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                type TEXT NOT NULL,
                level INTEGER NOT NULL,
                PRIMARY KEY (uuid,item_id,type),
                FOREIGN KEY (uuid,item_id) REFERENCES items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createGemstones = """
            CREATE TABLE IF NOT EXISTS gemstones (
                uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                slot_id TEXT NOT NULL,
                gem_purity TEXT NOT NULL,
                gem_type TEXT NOT NULL,
                PRIMARY KEY (uuid,item_id,slot_id),
                FOREIGN KEY (uuid,item_id) REFERENCES items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createAuctionsActive = """
            CREATE TABLE IF NOT EXISTS auctions_active (
                uuid UUID PRIMARY KEY,
                item_uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                start_time TIMESTAMPTZ NOT NULL,
                end_time TIMESTAMPTZ NOT NULL,
                price BIGINT NOT NULL,
                FOREIGN KEY (item_uuid,item_id) REFERENCES items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            );
        """;

        String createAuctionsBought = """
            CREATE TABLE IF NOT EXISTS auctions_bought (
                uuid UUID PRIMARY KEY,
                item_uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                time_bought TIMESTAMPTZ NOT NULL,
                price BIGINT NOT NULL,
                was_bin BOOLEAN NOT NULL,
                FOREIGN KEY (item_uuid,item_id) REFERENCES items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            );
        """;

        String truncatePriorActiveAuctions = """
            TRUNCATE TABLE auctions_active;
        """;

        String deleteUnusedItems = """
            DELETE FROM items i
                    WHERE NOT EXISTS (
                        SELECT 1
                        FROM auctions_bought ab
                        WHERE ab.item_uuid = i.uuid AND ab.item_id = i.item_id
                    );
        """;

        LOG.debug("Creating tables if not exist and dropping prior active auctions.");
        jdbc.execute(createItems);
        jdbc.execute(createToolItems);
        jdbc.execute(createWeaponItems);
        jdbc.execute(createArmorItems);
        jdbc.execute(createPetItems);
        jdbc.execute(createEnchantments);
        jdbc.execute(createGemstones);
        jdbc.execute(createAuctionsActive);
        jdbc.execute(createAuctionsBought);
        jdbc.execute(truncatePriorActiveAuctions);
        jdbc.execute(deleteUnusedItems);
        LOG.debug("Finished creating tables and dropping auction.");
    }

    /**
     * Saves a new active auction
     * @param auction active auction to save
     */
    @Transactional
    public void saveAuction(AuctionActive auction) {
        Item item = auction.getItem();

        insertItem(item);

        String insertAuctionActive = """
            INSERT INTO auctions_active (
                uuid,
                item_uuid,
                item_id,
                start_time,
                end_time,
                price
            ) VALUES (?,?,?,?,?,?)
            ON CONFLICT (uuid) DO NOTHING;
        """;
        jdbc.update(insertAuctionActive,
                auction.getUuid(),
                item.getUuid(),
                item.getItemId(),
                Timestamp.from(auction.getStartTime()),
                Timestamp.from(auction.getEndTime()),
                auction.getPrice()
        );

        LOG.debug("Inserted new auction: {}", auction.getUuid().toString());
    }



    /**
     * Saves a bought auction that was not tracked before
     * @param auction auction to save
     */
    @Transactional
    public void saveBoughtAuction(AuctionEnded auction) {
        Item item = auction.getItem();

        insertItem(item);

        String insertAuctionBought = """
            INSERT INTO auctions_bought (
                uuid,
                item_uuid,
                item_id,
                time_bought,
                price,
                was_bin
            ) VALUES (?,?,?,?,?,?)
            ON CONFLICT (uuid) DO NOTHING;
        """;
        jdbc.update(insertAuctionBought,
                auction.getUuid(),
                item.getUuid(),
                item.getItemId(),
                Timestamp.from(auction.getTimeEnded()),
                auction.getPrice(),
                auction.wasBin()
        );

        LOG.debug("Inserted ended auction: {}", auction.getUuid().toString());
    }

    private void insertItem(Item item) {
        String insertItem = """
            INSERT INTO items (
                uuid,
                item_id,
                item_bytes,
                rarity,
                remaining_tags_dump
            ) VALUES (?,?,?,?,?)
            ON CONFLICT (uuid,item_id) DO NOTHING;
        """;

        jdbc.update(insertItem,
                item.getUuid(),
                item.getItemId(),
                item.getItemBytes(),
                item.getRarity(),
                item.getRemainingTagDump()
        );

        if (item instanceof ToolItem toolItem) {
            String insertToolItem = """
                INSERT INTO tool_items (
                    uuid,
                    item_id,
                    upgrade_level,
                    reforge,
                    hot_potato_count,
                    rarity_upgrades
                ) VALUES (?,?,?,?,?,?)
                ON CONFLICT (uuid,item_id) DO NOTHING;
            """;
            jdbc.update(insertToolItem,
                    toolItem.getUuid(),
                    toolItem.getItemId(),
                    toolItem.getUpgradeLevel(),
                    toolItem.getReforge(),
                    toolItem.getHotPotatoCount(),
                    toolItem.getRarityUpgrades()
            );

            if (toolItem instanceof WeaponItem weaponItem) {
                String insertWeaponItem = """
                    INSERT INTO weapon_items (
                        uuid,
                        item_id,
                        art_of_war_count
                    ) VALUES (?,?,?)
                    ON CONFLICT (uuid,item_id) DO NOTHING;
                """;
                jdbc.update(insertWeaponItem,
                        weaponItem.getUuid(),
                        weaponItem.getItemId(),
                        weaponItem.getArtOfWarCount()
                );

            } else if (toolItem instanceof ArmorItem armorItem) {
                String insertArmorItem = """
                    INSERT INTO armor_items (
                        uuid,
                        item_id,
                        art_of_peace_count
                    ) VALUES (?,?,?)
                    ON CONFLICT (uuid,item_id) DO NOTHING;
                """;
                jdbc.update(insertArmorItem,
                        armorItem.getUuid(),
                        armorItem.getItemId(),
                        armorItem.getArtOfPeaceCount()
                );
            }

        } else if (item instanceof PetItem petItem) {
            String insertPetItem = """
                INSERT INTO pet_items (
                    uuid,
                    item_id,
                    level,
                    candy_count,
                    pet_item
                ) VALUES (?,?,?,?,?)
                ON CONFLICT (uuid,item_id) DO NOTHING;
            """;
            jdbc.update(insertPetItem,
                    petItem.getUuid(),
                    petItem.getItemId(),
                    petItem.getLevel(),
                    petItem.getCandyCount(),
                    petItem.getPetItem()
            );
        }

        List<Enchantment> enchantments = item.getEnchantments();
        if (!enchantments.isEmpty()) {
            String insertEnchantment = """
                INSERT INTO enchantments (
                    uuid,
                    item_id,
                    type,
                    level
                ) VALUES (?,?,?,?)
                ON CONFLICT (uuid,item_id,type) DO NOTHING;
            """;
            for (Enchantment enchantment : enchantments) {
                jdbc.update(insertEnchantment,
                        item.getUuid(),
                        item.getItemId(),
                        enchantment.type().toString(),
                        enchantment.level()
                );
            }
        }

        List<GemstoneSlot> gemstones = item.getGemstones();
        if (!gemstones.isEmpty()) {
            String insertGemstone = """
                INSERT INTO gemstones (
                    uuid,
                    item_id,
                    slot_id,
                    gem_purity,
                    gem_type
                ) VALUES (?,?,?,?,?)
                ON CONFLICT (uuid,item_id,slot_id) DO NOTHING;
            """;
            for (GemstoneSlot gemstone : gemstones) {
                jdbc.update(insertGemstone,
                        item.getUuid(),
                        item.getItemId(),
                        gemstone.slotName(),
                        gemstone.gemPurity(),
                        gemstone.gemType()
                );
            }
        }
    }


    /**
     * End an active auction when it was bought
     * Removes the auction from AuctionActive and inserts it into AuctionsBought for later use
     * @param auctionUUID UUID of auction which was bought
     * @param timeBought {@link Instant} where auction was bought
     */
    @Transactional
    public void moveAuctionToBought(UUID auctionUUID, Instant timeBought) {
        String copyAuction = """
            INSERT INTO auctions_bought (
                uuid,
                item_uuid,
                item_id,
                time_bought,
                price,
                was_bin
            )
            SELECT
                uuid,item_uuid,item_id,?,price,?
            FROM auctions_active
            WHERE uuid = ?
        """;

        jdbc.update(copyAuction,
                Timestamp.from(timeBought),
                true,
                auctionUUID
        );

        deleteActiveAuction(auctionUUID);

        LOG.debug("Moved auction to ended: {}", auctionUUID.toString());
    }


    /**
     * Deletes an active auction (but NOT the corresponding item)
     * @param auctionUUID UUID of the auction which should be deleted
     */
    @Transactional
    public void deleteActiveAuction(UUID auctionUUID) {
        String deleteAuction = "DELETE FROM auctions_active WHERE uuid = ?";
        jdbc.update(deleteAuction, auctionUUID);
    }

    /**
     * Deletes an active auction (AND the corresponding item)
     * @param auctionUuid UUID of the auction which should be deleted
     */
    @Transactional
    public void deleteActiveAuctionAndItem(UUID auctionUuid) {
        String sql = "SELECT item_uuid FROM auctions_active WHERE uuid = ?;";
        UUID itemUuid;
        try {
            itemUuid = jdbc.queryForObject(sql, UUID.class, auctionUuid);
        } catch (DataAccessException e) {
            LOG.warn("Couldn't access item_uuid from auction with uuid {} for removal. Skipping deletion.",auctionUuid);
            return;
        }
        String deleteAuction = "DELETE FROM auctions_active WHERE uuid = ?;";
        jdbc.update(deleteAuction, auctionUuid);

        String deleteItem = """
            DELETE FROM items
            WHERE uuid = ?;
        """;

        jdbc.update(deleteItem, itemUuid);
    }


    /**
     * Checks if an active auction with passed uuid exists
     * @param uuid uuid to check
     * @return true if auction exists
     */
    public boolean existsActiveByUuid(UUID uuid) {
        String sql = "SELECT COUNT(*) FROM auctions_active WHERE uuid = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, uuid);
        return count != null && count > 0;
    }

}
