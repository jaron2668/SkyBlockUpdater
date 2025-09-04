package io.github.jaron2668.skyblockupdater.model;

import java.time.Instant;
import java.util.UUID;

import java.util.List;


public class Auction {
    private UUID id;
    private String itemId;
    private String itemBytes; // raw B64 NBT - other attributes are extracted from this - mostly saved for testing purposes
    private Instant startTime;
    private Instant endTime;
    private long price;
    private int upgradeLevel; // correspond to stars I guess?
    private String reforge;
    private String rarity;
    private int hotPotatoCount;
    private int artOfWarCount;
    private int artOfPeaceCount;
    private int rarityUpgrades;
    private List<Enchantment> importantEnchantments;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemBytes() {
        return itemBytes;
    }

    public void setItemBytes(String itemBytes) {
        this.itemBytes = itemBytes;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public List<Enchantment> getImportantEnchantments() {
        return importantEnchantments;
    }

    public void setImportantEnchantments(List<Enchantment> importantEnchantments) {
        this.importantEnchantments = importantEnchantments;
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public String getReforge() {
        return reforge;
    }

    public void setReforge(String reforge) {
        this.reforge = reforge;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public boolean hasEnchant(Enchantment.EnchantmentType type) {
        return importantEnchantments != null && importantEnchantments.stream().anyMatch(e -> e.type() == type);
    }

    public boolean isDungeonItem() {
        return upgradeLevel > 0;
    }

    public int getHotPotatoCount() {
        return hotPotatoCount;
    }

    public void setHotPotatoCount(int hotPotatoCount) {
        this.hotPotatoCount = hotPotatoCount;
    }

    public int getArtOfWarCount() {
        return artOfWarCount;
    }

    public void setArtOfWarCount(int artOfWarCount) {
        this.artOfWarCount = artOfWarCount;
    }

    public int getArtOfPeaceCount() {
        return artOfPeaceCount;
    }

    public void setArtOfPeaceCount(int artOfPeaceCount) {
        this.artOfPeaceCount = artOfPeaceCount;
    }

    public int getRarityUpgrades() {
        return rarityUpgrades;
    }

    public void setRarityUpgrades(int rarityUpgrades) {
        this.rarityUpgrades = rarityUpgrades;
    }
}
