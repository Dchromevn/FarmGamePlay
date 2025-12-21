package model.crops;

import utility.CropType;
import utility.Point;

public class Corn extends Crop {
    public Corn(String id, Point position) {
        super(id, position, CropType.CORN);
    }
    @Override
    protected void consumeResource() {
        waterLevel = Math.max(0, this.waterLevel - 5);
        fertilizerLevel =Math.max(0, this.fertilizerLevel -3);
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