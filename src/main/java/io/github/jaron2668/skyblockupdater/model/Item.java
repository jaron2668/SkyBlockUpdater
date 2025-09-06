package io.github.jaron2668.skyblockupdater.model;

import java.util.List;
import java.util.UUID;

public class Item {
    private UUID uuid;
    private String itemId;
    private String itemBytes;
    private String rarity;
    private List<Enchantment> enchantments;
    private List<GemstoneSlot> gemstones;
    private String remainingTagDump;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public List<Enchantment> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(List<Enchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public List<GemstoneSlot> getGemstones() {
        return gemstones;
    }

    public void setGemstones(List<GemstoneSlot> gemstones) {
        this.gemstones = gemstones;
    }

    public String getRemainingTagDump() {
        return remainingTagDump;
    }

    public void setRemainingTagDump(String remainingTagDump) {
        this.remainingTagDump = remainingTagDump;
    }
}
