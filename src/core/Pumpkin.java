package core;

import utility.CropType;
import utility.Point;

public class Pumpkin extends Crop{
    public Pumpkin(String id, Point position) {
        super(id, position, CropType.PUMPKIN);
        this.dayPerStage = new int[]{3,3,3};
        this.maxWaterLevel=120;
        this.maxFertilizerLevel= 150;
        this.waterNeedThreshold = 45;
        this.fertilizerNeedThreshold = 50;
        this.waterLevel = this.maxWaterLevel / 2;
        this.fertilizerLevel = this.maxFertilizerLevel / 2;
    }
    @Override
    protected void consumeResource() {
        waterLevel = Math.max(0, this.waterLevel - 18);
        fertilizerLevel =Math.max(0, this.fertilizerLevel -15);
    }
    @Override
    public int harvest() {
        if (!isHarvestable()) {
            System.out.println("Pumpkin is not ready for harvesting");
            return 0;
        }
        int value = calculateHarvestValue();
        System.out.println("Harvested Pumpkin: $" + value);
        return value;
    }
}