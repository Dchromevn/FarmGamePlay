package model.crops;

import utility.CropType;
import utility.Point;

public class Pumpkin extends Crop {
    public Pumpkin(String id, Point position) {
        super(id, position, CropType.PUMPKIN);
    }
    @Override
    protected void consumeResource() {
        waterLevel = Math.max(0, this.waterLevel - 10);
        fertilizerLevel =Math.max(0, this.fertilizerLevel -6);
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