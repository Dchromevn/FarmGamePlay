package utility;

import core.Crop;

public class CropStatus {
    private final CropStage stage;
    private final int health;
    private final int waterLevel;
    private final int fertilizerLevel;
    private final int daysHarvest;
    private final boolean needWater;
    private final boolean needFertilizer;
    private final String statusMessage;
    public CropStatus(Crop crop) {
        this.stage = crop.getCurrentStage();
        this.health = crop.getHealth();
        this.waterLevel = crop.getWaterLevel();
        this.fertilizerLevel = crop.getFertilizerLevel();
        this.daysHarvest = crop.getDaysHarvest();
        this.needWater = crop.needWater();
        this.needFertilizer = crop.needFertilizer();
        this.statusMessage = generateStatusCrop(crop);
    }
    private String generateStatusCrop(Crop crop) {
        if (crop.isDead()) {
            return "Crop is dead! Needs recycling.";
        } else if (crop.isHarvestable()) {
            return "Ready to harvest!";
        } else if (this.needWater && this.needFertilizer) {
            return "Needs water and fertilizer!";
        } else if (this.needWater) {
            return "Needs water!";
        } else if (this.needFertilizer) {
            return "Needs fertilizer!";
        } else if (this.health < 50) {
            return "Low health!";
        } else {
            return "Growing healthy";
        }
    }
    public CropStage getStage() { return stage; }
    public int getHealth() { return health; }
    public int getWaterLevel() { return waterLevel; }
    public int getFertilizerLevel() { return fertilizerLevel; }
    public int getDaysHarvest() { return daysHarvest; }
    public boolean needsWater() { return needWater; }
    public boolean needsFertilizer() { return needFertilizer; }
    public String getStatusMessage() { return statusMessage; }
    @Override
    public String toString() {
        return String.format(
                "Stage: %s\nHealth: %d%%\nWater: %d%%\nFertilizer: %d%%\nDays to harvest: %d\nStatus: %s",
                stage.getDisplayStage(), health, waterLevel, fertilizerLevel, daysHarvest, statusMessage
        );
    }
}
