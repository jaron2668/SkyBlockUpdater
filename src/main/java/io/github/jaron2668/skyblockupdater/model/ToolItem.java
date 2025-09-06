package io.github.jaron2668.skyblockupdater.model;

public class ToolItem extends Item{
    private int upgradeLevel;
    private String reforge;
    private int hotPotatoCount;
    private int rarityUpgrades;

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

    public int getHotPotatoCount() {
        return hotPotatoCount;
    }

    public void setHotPotatoCount(int hotPotatoCount) {
        this.hotPotatoCount = hotPotatoCount;
    }

    public int getRarityUpgrades() {
        return rarityUpgrades;
    }

    public void setRarityUpgrades(int rarityUpgrades) {
        this.rarityUpgrades = rarityUpgrades;
    }
}
