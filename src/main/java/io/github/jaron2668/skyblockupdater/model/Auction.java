package io.github.jaron2668.skyblockupdater.model;

import java.time.Instant;
import java.util.UUID;

import java.util.List;


public class Auction {
    private UUID id;
    private String itemId;
    private String itemName;
    private Instant startTime;
    private Instant endTime;
    private long price;
    private int dungeonStars;
    private String reforge;
    private String rarity;
    private int hpbCount;
    private int fpbCount;
    private boolean hasArtOfWar;
    private boolean hasArtOfPeace;
    private boolean isRecombobulated;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public int getDungeonStars() {
        return dungeonStars;
    }

    public void setDungeonStars(int dungeonStars) {
        this.dungeonStars = dungeonStars;
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
        return importantEnchantments != null && importantEnchantments.stream().anyMatch(e -> e.getType() == type);
    }

    public boolean isDungeonItem() {
        return dungeonStars > 0;
    }

    public int getHpbCount() {
        return hpbCount;
    }

    public void setHpbCount(int hpbCount) {
        this.hpbCount = hpbCount;
    }

    public int getFpbCount() {
        return fpbCount;
    }

    public void setFpbCount(int fpbCount) {
        this.fpbCount = fpbCount;
    }

    public boolean hasArtOfWar() {
        return hasArtOfWar;
    }

    public void setHasArtOfWar(boolean hasArtOfWar) {
        this.hasArtOfWar = hasArtOfWar;
    }

    public boolean hasArtOfPeace() {
        return hasArtOfPeace;
    }

    public void setHasArtOfPeace(boolean hasArtOfPeace) {
        this.hasArtOfPeace = hasArtOfPeace;
    }

    public boolean isRecombobulated() {
        return isRecombobulated;
    }

    public void setRecombobulated(boolean recombobulated) {
        isRecombobulated = recombobulated;
    }
}
