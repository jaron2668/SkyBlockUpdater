package io.github.jaron2668.skyblockupdater.model;

public class Enchantment {
    public enum EnchantmentType {
        SHARPNESS;
    }

    private final EnchantmentType type;
    private final int level;

    public Enchantment(EnchantmentType type, int level) {
        this.type = type;
        this.level = level;
    }

    public EnchantmentType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }
}
