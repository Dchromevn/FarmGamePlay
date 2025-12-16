package resourceManagement;
import  player.Player;
import java.util.HashMap;
import java.util.Map;

import utility.CropType;

public class BalanceReport {
    private int money;
    private int water;
    private int fertilizer;
    private Map<CropType, Integer> seeds;
    private String status;
    private static final int LOW_MONEY_THRESHOLD = 100;
    private static final int LOW_WATER_THRESHOLD = 20;
    private static final int LOW_FERTILIZER_THRESHOLD = 10;
    private static final int LOW_SEED_THRESHOLD = 5;

    public BalanceReport(Player player) {
        if (player == null) {
            this.money = 0;
            this.water = 0;
            this.fertilizer = 0;
            this.seeds = new HashMap<>();
            this.status = "ERROR: No player data";
            return;
        }
        this.money = player.getMoney();
        this.water = player.getWaterSupply();
        this.fertilizer = player.getFertilizerSupply();
        this.seeds = new HashMap<>();
        for (CropType cropType : CropType.values()) {
            int seedCount = player.getSeedCount(cropType);
            this.seeds.put(cropType, seedCount);
        }
        this.status = determineStatus();
    }

    public int getMoney() {
        return money;
    }
    
    public int getWater() {
        return water;
    }
    
    public int getFertilizer() {
        return fertilizer;
    }
    
    public int getSeedCount(CropType cropType) {
        return seeds.getOrDefault(cropType, 0);
    }

    public Map<CropType, Integer> getAllSeeds() {
        return new HashMap<>(seeds);
    }

    public String getStatus() {
        return status;
    }

    public boolean hasLowResources() {
        if (money < LOW_MONEY_THRESHOLD) return true;
        if (water < LOW_WATER_THRESHOLD) return true;
        if (fertilizer < LOW_FERTILIZER_THRESHOLD) return true;
        int lowSeedCount = 0;
        for (Integer count : seeds.values()) {
            if (count < LOW_SEED_THRESHOLD) {
                lowSeedCount++;
            }
        }
        
        return lowSeedCount >= seeds.size();
    }

    public boolean isResourceLow(String resourceType) {
        switch (resourceType.toLowerCase()) {
            case "money":
                return money < LOW_MONEY_THRESHOLD;
            case "water":
                return water < LOW_WATER_THRESHOLD;
            case "fertilizer":
                return fertilizer < LOW_FERTILIZER_THRESHOLD;
            default:
                return false;
        }
    }

    public boolean canContinueFarming() {
        return money > 0 && water > 0 && getTotalSeeds() > 0;
    }

    public int getTotalSeeds() {
        int total = 0;
        for (Integer count : seeds.values()) {
            total += count;
        }
        return total;
    }

    private String determineStatus() {
        if (!canContinueFarming()) {
            return "Cannot continue farming! Need to buy resources.";
        }
        if (money < LOW_MONEY_THRESHOLD && water < LOW_WATER_THRESHOLD) {
            return "Multiple resources  critically low!";
        }
        if (hasLowResources()) {
            return "Some resources are running low.";
        }
        return "Resources adequate for operations.";
    }

    public String getLowResourcesDetails() {
        StringBuilder details = new StringBuilder();
        if (money < LOW_MONEY_THRESHOLD) {
            details.append("• Money is low (").append(money).append(")\n");
        }
        if (water < LOW_WATER_THRESHOLD) {
            details.append("• Water is low (").append(water).append(")\n");
        }
        if (fertilizer < LOW_FERTILIZER_THRESHOLD) {
            details.append("• Fertilizer is low (").append(fertilizer).append(")\n");
        }
        for (Map.Entry<CropType, Integer> entry : seeds.entrySet()) {
            if (entry.getValue() < LOW_SEED_THRESHOLD) {
                details.append("• ").append(entry.getKey().getCropName())
                       .append(" seeds are low (").append(entry.getValue()).append(")\n");
            }
        }
        return details.length() > 0 ? details.toString() : "No low resources";
    }

    @Override
    public String toString() {
        StringBuilder report = new StringBuilder();
        report.append("╔════════════════════════════════════════╗\n");
        report.append("║        BALANCE REPORT                  ║\n");
        report.append("╠════════════════════════════════════════╣\n");
        report.append(String.format("║ Money:       $%-25d ║\n", money));
        report.append("╠════════════════════════════════════════╣\n");
        report.append(String.format("║ Water:       %-26d ║\n", water));
        report.append(String.format("║ Fertilizer:  %-26d ║\n", fertilizer));
        report.append("╠════════════════════════════════════════╣\n");
        report.append("║ SEED INVENTORY:                        ║\n");
        for (CropType cropType : CropType.values()) {
            int count = seeds.getOrDefault(cropType, 0);
            String indicator = count < LOW_SEED_THRESHOLD ? "⚠" : " ";
            report.append(String.format("║  %s %-10s: %-20d ║\n", 
                                       indicator, cropType.getCropName(), count));
        }
        report.append("╠════════════════════════════════════════╣\n");
        report.append(String.format("║ Status: %-30s ║\n", status));
        if (hasLowResources()) {
            report.append("╠════════════════════════════════════════╣\n");
            report.append("║ ⚠️  LOW RESOURCES DETECTED:            ║\n");
            String[] lowResources = getLowResourcesDetails().split("\n");
            for (String line : lowResources) {
                if (!line.isEmpty()) {
                    report.append(String.format("║ %-38s ║\n", line));
                }
            }
        }
        report.append("╚════════════════════════════════════════╝\n");
        
        return report.toString();
    }
    
    public String toCompactString() {
        return String.format("Money: $%d | Water: %d | Fertilizer: %d | Seeds: %d | Status: %s",
                           money, water, fertilizer, getTotalSeeds(), status);
    }
}