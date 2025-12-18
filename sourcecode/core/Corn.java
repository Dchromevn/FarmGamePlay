package core;

import utility.CropType;
import utility.Point;

public class Corn extends Crop{
    public Corn(String id, Point position) {
        super(id, position, CropType.CORN);
        this.dayPerStage = new int[]{1,2,2};
        this.maxWaterLevel=110;
        this.maxFertilizerLevel= 90;
        this.waterNeedThreshold = 40;
        this.fertilizerNeedThreshold = 35;
        this.waterLevel = this.maxWaterLevel / 2;
        this.fertilizerLevel = this.maxFertilizerLevel / 2;
    }
    @Override
    protected void consumeResource() {
        waterLevel = Math.max(0, this.waterLevel - 10);
        fertilizerLevel =Math.max(0, this.fertilizerLevel -10);
    }
    @Override
    public int harvest() {
        if (!isHarvestable()) {
            System.out.println("Corn is not ready for harvesting");
            return 0;
        }
        int value = calculateHarvestValue();
        System.out.println("Harvested Corn: $" + value);
        return value;
    }
}