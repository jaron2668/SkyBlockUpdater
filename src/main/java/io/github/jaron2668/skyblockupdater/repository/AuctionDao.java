package io.github.jaron2668.skyblockupdater.repository;

import io.github.jaron2668.skyblockupdater.model.*;
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
    public void createTableIfNotExists() {
        String deleteItems = """
            WITH to_delete AS (
                SELECT item_uuid FROM AuctionsActive
            )
            DELETE FROM Items
            WHERE uuid IN (SELECT item_uuid FROM to_delete);
        """;

        String dropTable = """
            DROP TABLE IF EXISTS AuctionsActive;
        """;

        String createItems = """
            CREATE TABLE IF NOT EXISTS Items (
                uuid UUID PRIMARY KEY,
                item_id TEXT NOT NULL,
                item_bytes TEXT NOT NULL,
                rarity TEXT
            );
        """;

        String createToolItems = """
            CREATE TABLE IF NOT EXISTS ToolItems (
                uuid UUID PRIMARY KEY,
                upgrade_level INTEGER NOT NULL,
                reforge TEXT NOT NULL,
                hot_potato_count INTEGER NOT NULL,
                rarity_upgrades INTEGER NOT NULL,
                FOREIGN KEY (uuid) REFERENCES Items(uuid)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createWeaponItems = """
            CREATE TABLE IF NOT EXISTS WeaponItems (
                uuid UUID PRIMARY KEY,
                art_of_war_count INTEGER NOT NULL,
                FOREIGN KEY (uuid) REFERENCES ToolItems(uuid)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createArmorItems = """
            CREATE TABLE IF NOT EXISTS ArmorItems (
                uuid UUID PRIMARY KEY,
                art_of_peace_count INTEGER NOT NULL,
                FOREIGN KEY (uuid) REFERENCES ToolItems(uuid)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createEnchantments = """
            CREATE TABLE IF NOT EXISTS Enchantments (
                uuid UUID NOT NULL,
                type TEXT NOT NULL
                level INTEGER NOT NULL,
                PRIMARY KEY (uuid,type),
                FOREIGN KEY (uuid) REFERENCES Items(uuid)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createGemstones = """
            CREATE TABLE IF NOT EXISTS Gemstones (
                uuid UUID NOT NULL,
                slot_id TEXT NOT NULL,
                gem_purity TEXT NOT NULL,
                gem_type TEXT NOT NULL,
                PRIMARY KEY (uuid,slot_id),
                FOREIGN KEY (uuid) REFERENCES Items(uuid)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createPetItems = """
            CREATE TABLE IF NOT EXISTS PetItems (
                uuid UUID PRIMARY KEY,
                level INTEGER NOT NULL,
                candy_count INTEGER NOT NULL,
                pet_item TEXT NOT NULL,
                FOREIGN KEY (uuid) REFERENCES Items(uuid)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            );
        """;

        String createAuctionsActive = """
            CREATE TABLE auctions_active (
                uuid UUID PRIMARY KEY,
                item_uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                start_time TIMESTAMPTZ NOT NULL,
                end_time TIMESTAMPTZ NOT NULL,
                price BIGINT NOT NULL,
                FOREIGN KEY (item_uuid,item_id) REFERENCES Items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            );
        """;

        String createAuctionsBought = """
            CREATE TABLE IF NOT EXISTS AuctionsBought (
                uuid UUID PRIMARY KEY,
                item_uuid UUID NOT NULL,
                item_id TEXT NOT NULL,
                time_bought TIMESTAMPTZ NOT NULL,
                price BIGINT NOT NULL,
                was_bin BOOLEAN NOT NULL,
                FOREIGN KEY (item_uuid,item_id) REFERENCES Items(uuid,item_id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            );
        """;
        LOG.debug("Creating tables if not exist and dropping prior active auctions.");
        jdbc.execute(deleteItems);
        jdbc.execute(dropTable);
        jdbc.execute(createItems);
        jdbc.execute(createToolItems);
        jdbc.execute(createWeaponItems);
        jdbc.execute(createArmorItems);
        jdbc.execute(createEnchantments);
        jdbc.execute(createGemstones);
        jdbc.execute(createPetItems);
        jdbc.execute(createAuctionsActive);
        jdbc.execute(createAuctionsBought);
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
            INSERT INTO AuctionActive (
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
            INSERT INTO AuctionBought (
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
            INSERT INTO Items (
                uuid,
                item_id,
                item_bytes,
                rarity
            ) VALUES (?,?,?,?)
            ON CONFLICT (uuid) DO NOTHING;
        """;

        jdbc.update(insertItem,
                item.getUuid(),
                item.getItemId(),
                item.getItemBytes(),
                item.getRarity()
        );

        if (item instanceof ToolItem toolItem) {
            String insertToolItem = """
                INSERT INTO ToolItems (
                    uuid,
                    upgrade_level,
                    reforge,
                    hot_potato_count,
                    rarity_upgrades
                ) VALUES (?,?,?,?,?)
                ON CONFLICT (uuid) DO NOTHING;
            """;
            jdbc.update(insertToolItem,
                    toolItem.getUuid(),
                    toolItem.getUpgradeLevel(),
                    toolItem.getReforge(),
                    toolItem.getHotPotatoCount(),
                    toolItem.getRarityUpgrades()
            );

            if (toolItem instanceof WeaponItem weaponItem) {
                String insertWeaponItem = """
                    INSERT INTO WeaponItems (
                        uuid,
                        art_of_war_count
                    ) VALUES (?,?)
                    ON CONFLICT (uuid) DO NOTHING;
                """;
                jdbc.update(insertWeaponItem,
                        weaponItem.getUuid()
                        ,weaponItem.getArtOfWarCount()
                );

            } else if (toolItem instanceof ArmorItem armorItem) {
                String insertArmorItem = """
                    INSERT INTO ArmorItems (
                        uuid,
                        art_of_peace_count
                    ) VALUES (?,?)
                    ON CONFLICT (uuid) DO NOTHING;
                """;
                jdbc.update(insertArmorItem,
                        armorItem.getUuid(),
                        armorItem.getArtOfPeaceCount()
                );
            }

        } else if (item instanceof PetItem petItem) {
            String insertPetItem = """
                INSERT INTO PetItems (
                    uuid,
                    level,
                    candy_count,
                    pet_item
                ) VALUES (?,?,?,?)
                ON CONFLICT (uuid) DO NOTHING;
            """;
            jdbc.update(insertPetItem,
                    petItem.getUuid(),
                    petItem.getLevel(),
                    petItem.getCandyCount(),
                    petItem.getPetItem()
            );
        }

        List<Enchantment> enchantments = item.getEnchantments();
        if (!enchantments.isEmpty()) {
            String insertEnchantment = """
                INSERT INTO Enchantments (
                    uuid,
                    type,
                    level
                ) VALUES (?,?,?)
                ON CONFLICT (uuid,type) DO NOTHING;
            """;
            for (Enchantment enchantment : enchantments) {
                jdbc.update(insertEnchantment,
                        item.getUuid(),
                        enchantment.type(),
                        enchantment.level()
                );
            }
        }

        List<GemstoneSlot> gemstones = item.getGemstones();
        if (!gemstones.isEmpty()) {
            String insertGemstone = """
                INSERT INTO Gemstones (
                    uuid,
                    slot_id,
                    gem_purity,
                    gem_type
                ) VALUES (?,?,?)
                ON CONFLICT (uuid,slot_id) DO NOTHING;
            """;
            for (GemstoneSlot gemstone : gemstones) {
                jdbc.update(insertGemstone,
                        item.getUuid(),
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
     * @param auctionUUID UUID of auction which has ended
     * @param timeBought {@link Instant} where auction was bought
     */
    @Transactional
    public void moveAuctionToBought(UUID auctionUUID, Instant timeBought) {
        String copyAuction = """
            INSERT INTO AuctionsBought (
                uuid,
                item_uuid,
                item_id,
                time_bought,
                price,
                was_bin
            )
            SELECT
                uuid,item_uuid,item_id,?,price,?
            FROM AuctionsActive
            WHERE id = ?
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
        String deleteAuction = "DELETE FROM AuctionsActive WHERE uuid = ?";
        jdbc.update(deleteAuction, auctionUUID);
    }

    /**
     * Deletes an active auction (AND the corresponding item)
     * @param auctionUuid UUID of the auction which should be deleted
     */
    @Transactional
    public void deleteActiveAuctionAndItem(UUID auctionUuid) {
        String sql = "SELECT item_uuid FROM AuctionsActive WHERE uuid = ?;";
        UUID itemUuid;
        try {
            itemUuid = jdbc.queryForObject(sql, UUID.class, auctionUuid);
        } catch (DataAccessException e) {
            LOG.warn("Couldn't access item_uuid from auction with uuid {} for removal. Skipping deletion.",auctionUuid);
            return;
        }
        String deleteAuction = "DELETE FROM AuctionsActive WHERE uuid = ?;";
        jdbc.update(deleteAuction, auctionUuid);

        String deleteItem = """
            DELETE FROM Items
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
        String sql = "SELECT COUNT(*) FROM AuctionsActive WHERE uuid = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, uuid);
        return count != null && count > 0;
    }

}
