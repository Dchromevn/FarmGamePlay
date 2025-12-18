package core;

import utility.CropType;
import utility.Point;

public class Tomato extends Crop{
    public Tomato (String id, Point position) {
        super(id, position, CropType.TOMATO);
        this.dayPerStage = new int[]{2,3,2};
        this.maxWaterLevel=100;
        this.maxFertilizerLevel= 100;
        this.waterNeedThreshold = 30;
        this.fertilizerNeedThreshold = 30;
        this.waterLevel = this.maxWaterLevel / 2;
        this.fertilizerLevel = this.maxFertilizerLevel / 2;
    }
    @Override
    protected void consumeResource() {
        waterLevel = Math.max(0, this.waterLevel - 12);
        fertilizerLevel =Math.max(0, this.fertilizerLevel -8);
    }
    @Override
    public int harvest() {
        if (!isHarvestable()) {
            System.out.println("Tomato is not ready for harvesting");
            return 0;
        }
        int value = calculateHarvestValue();
        System.out.println("Harvested Tomato: $" + value);
        return value;
    }
}