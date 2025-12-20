package core;

import utility.CropType;
import utility.Point;

public class Wheat extends Crop{
    public Wheat(String id, Point position) {
        super(id, position, CropType.WHEAT);
    }
    @Override
    protected void consumeResource() {
       waterLevel = Math.max(0, this.waterLevel - 4);
       fertilizerLevel =Math.max(0, this.fertilizerLevel -2);
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