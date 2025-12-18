package player;

import java.util.HashMap;
import java.util.Map;
import utility.CropType;
import exceptions.*;
public class Player {
    private int money;
    private int waterSupply;
    private int fertilizerSupply;
    private final Map<CropType, Integer> seedInventory;
    public Player(int initialMoney, int initialWater, int initialFertilizer) {
        this.money = initialMoney;
        this.waterSupply = initialWater;
        this.fertilizerSupply = initialFertilizer;
        this.seedInventory = new HashMap<>();
        for (CropType type : CropType.values()) {
            seedInventory.put(type, 0);
        }
    }

    public Player() {
        this(500, 100, 50);   
    }
    public int getMoney() {
        return money;
    }

    public int getWaterSupply() {
        return waterSupply;
    }

    public int getFertilizerSupply() {
        return fertilizerSupply;
    }

    public int getSeedCount(CropType cropType) {
        return seedInventory.getOrDefault(cropType, 0);
    }
//seeds
    public void addSeed(CropType cropType, int amount) {
    	validatePositive(amount, "Seed amount");
        seedInventory.put(cropType, getSeedCount(cropType) + amount);
    }

    public void removeSeed(CropType type, int amount) {
    	validatePositive(amount, "Seed amount");
        int current = getSeedCount(type);
        if (current < amount) {
            throw new NotEnoughResourceException(
                "Not enough seeds for " + type.getCropName()
            );
        }
        seedInventory.put(type, current - amount);
    }
//money

    public void spendMoney(int amount) {
    	validatePositive(amount, "Money");
        if (money < amount) {
            throw new NotEnoughResourceException(
                "Not enough money. Required: " + amount + ", available: " + money
            );
        }

        money -= amount;
    }


    public void earnMoney(int amount) {
        this.money += amount;
    }
 //water
    public void useWater(int amount) {
        validatePositive(amount, "Water");
        if (waterSupply < amount) {
            throw new NotEnoughResourceException("Not enough water");
        }
        waterSupply -= amount;
    }
    public void gainWater(int amount) {
    	validatePositive(amount,"Water");
        waterSupply += amount;
    }
//fertilizer
    public void useFertilizer(int amount) {
    	validatePositive(amount,"Fertilizer");
        if (fertilizerSupply < amount) {
            throw new NotEnoughResourceException("Not enough fertilizer");
        }
        fertilizerSupply -= amount;
    }

    public void gainFertilizer(int amount) {
    	validatePositive(amount,"Fertilizer");
        fertilizerSupply += amount;
    }
    private void validatePositive(int amount, String name) {
    	if (amount <=0 ){
    		throw new IllegalArgumentException(name +" must be postive.");
    	}
    }
    @Override
    public String toString() {
        return "Player{" +
                "money=" + money +
                ", waterSupply=" + waterSupply +
                ", fertilizerSupply=" + fertilizerSupply +
                ", seedInventory=" + seedInventory +
                '}';
    }
    public void showInventory() {
        System.out.println("=== PLAYER INVENTORY ===");
        System.out.println("Money: " + money);
        System.out.println("Water: " + waterSupply);
        System.out.println("Fertilizer: " + fertilizerSupply);

        System.out.println("\n--- Seeds ---");
        for (CropType type : seedInventory.keySet()) {
            System.out.println(type + ": " + seedInventory.get(type));
        }
    }

}


