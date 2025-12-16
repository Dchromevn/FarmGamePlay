package player;

import java.util.HashMap;
import java.util.Map;
import utility.CropType;

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

        // Initialize all seeds to 0
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
    public void addSeed(CropType cropType, int amount) {
        seedInventory.put(cropType, getSeedCount(cropType) + amount);
    }

    public boolean removeSeed(CropType cropType, int amount) {
        int current = getSeedCount(cropType);
        if (current >= amount) {
            seedInventory.put(cropType, current - amount);
            return true;
        }
        return false;
    }
    public boolean spendMoney(int amount) {
        if (money >= amount) {
            money -= amount;
            return true;
        }
        return false;
    }

    public void earnMoney(int amount) {
        this.money += amount;
    }
    public boolean useWater(int amount) {
        if (waterSupply >= amount) {
            waterSupply -= amount;
            return true;
        }
        return false;
    }

    public void addWater(int amount) {
        waterSupply += amount;
    }
    public boolean useFertilizer(int amount) {
        if (fertilizerSupply >= amount) {
            fertilizerSupply -= amount;
            return true;
        }
        return false;
    }

    public void addFertilizer(int amount) {
        fertilizerSupply += amount;
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
