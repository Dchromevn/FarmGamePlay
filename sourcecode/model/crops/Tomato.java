package model.crops;

import utility.CropType;
import utility.Point;

public class Tomato extends Crop {
    public Tomato (String id, Point position) {
        super(id, position, CropType.TOMATO);
    }
    @Override
    protected void consumeResource() {
        waterLevel = Math.max(0, this.waterLevel - 8);
        fertilizerLevel =Math.max(0, this.fertilizerLevel -4);
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