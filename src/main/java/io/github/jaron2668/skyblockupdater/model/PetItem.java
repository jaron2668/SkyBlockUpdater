package io.github.jaron2668.skyblockupdater.model;

public class PetItem extends Item {
    private int level;
    private int candyCount;
    private String petItem;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCandyCount() {
        return candyCount;
    }

    public void setCandyCount(int candyCount) {
        this.candyCount = candyCount;
    }

    public String getPetItem() {
        return petItem;
    }

    public void setPetItem(String petItem) {
        this.petItem = petItem;
    }
}
