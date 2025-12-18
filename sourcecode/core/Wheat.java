package core;

import utility.CropType;
import utility.Point;

public class Wheat extends Crop{
    public Wheat(String id, Point position) {
        super(id, position, CropType.WHEAT);
        this.dayPerStage = new int[]{1,1,1};
        this.maxWaterLevel=90;
        this.maxFertilizerLevel= 80;
        this.waterNeedThreshold = 20;
        this.fertilizerNeedThreshold = 20;
        this.waterLevel = this.maxWaterLevel / 2;
        this.fertilizerLevel = this.maxFertilizerLevel / 2;
    }
    @Override
    protected void consumeResource() {
       waterLevel = Math.max(0, this.waterLevel - 6);
       fertilizerLevel =Math.max(0, this.fertilizerLevel -4);
    }
    @Override
    public int harvest() {
        if (!isHarvestable()) {
            System.out.println("Wheat is not ready for harvesting");
            return 0;
        }
        int value = calculateHarvestValue();
        System.out.println("Harvested Wheat: $" + value);
        return value;
    }
}
