package model.player;
import java.io.Serializable;

import utility.CropType;
import model.exceptions.NotEnoughResourceException;
import java.util.HashMap;
import java.util.Map;

public class Inventory implements Serializable {
    private int money;
    private int medicine;
    private int water;
    private int fertilizer;
    private final Map<CropType, Integer> seeds;

    public Inventory(int initialMoney, int initialWater, int initialFertilizer) {
        this.money = initialMoney;
        this.water = initialWater;
        this.medicine = 0;
        this.fertilizer = initialFertilizer;
        this.seeds = new HashMap<>();
        for (CropType type : CropType.values()) {
            seeds.put(type, 0);
        }
    }

    public int getMoney() { return money; }

    public void earnMoney(int amount) {
        validatePositive(amount, "Money: ");
        this.money += amount;
    }

    public void spendMoney(int amount) {
        validatePositive(amount, "Money: ");
        if (this.money < amount) {
            throw new NotEnoughResourceException("Not enough money. Required: " + amount + ", available: " + money);
        }
        this.money -= amount;
    }
    public int getWater() { return water; }

    public void gainWater(int amount) {
        validatePositive(amount, "Water: ");
        this.water += amount;
    }

    public void useWater(int amount) {
        validatePositive(amount, "Water");
        if (this.water < amount) {
            throw new NotEnoughResourceException("Not enough water.");
        }
        this.water -= amount;
    }
    public int getFertilizer() { return fertilizer; }

    public void gainFertilizer(int amount) {
        validatePositive(amount, "Fertilizer: ");
        this.fertilizer += amount;
    }

    public void useFertilizer(int amount) {
        validatePositive(amount, "Fertilizer: ");
        if (this.fertilizer < amount) {
            throw new NotEnoughResourceException("Not enough fertilizer.");
        }
        this.fertilizer -= amount;
    }
    public int getSeedCount(CropType type) {
        return seeds.getOrDefault(type, 0);
    }

    public void addSeed(CropType type, int amount) {
        validatePositive(amount, "Seed amount: ");
        seeds.put(type, getSeedCount(type) + amount);
    }

    public void removeSeed(CropType type, int amount) {
        validatePositive(amount, "Seed amount: ");
        int current = getSeedCount(type);
        if (current < amount) {
            throw new NotEnoughResourceException("Not enough seeds for " + type.getCropName() + " .");
        }
        seeds.put(type, current - amount);
    }
    public int getMedicine() {
    	return medicine;
    }
    public void gainMedicine(int amount) {
    	validatePositive(amount, "Medicine: ");
    	this.medicine += amount;
    }
    public void useMedicine(int amount) {
    	validatePositive(amount, "Medicine: ");
    	if (this.medicine < amount) {
    		throw new NotEnoughResourceException("Not enough medicine!");
    	}
    	this.medicine -= amount;
    }
    private void validatePositive(int amount, String name) {
        if (amount <= 0) {
            throw new IllegalArgumentException(name + " must be positive.");
        }
    }

    public String getStatusString() {
        return String.format("[ Money: $%d | Water: %d | Fertilizer: %d |Medicine: %d]", money, water, fertilizer,medicine);
    }
}