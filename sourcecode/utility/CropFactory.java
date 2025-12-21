package utility;

import model.crops.*;

public class CropFactory {
    private static int cropIdCounter = 0;
    public static Crop createCrop(CropType type, Point position) {
        String id = type.getCropName() + "_" + (++cropIdCounter);
        return switch (type) {
            case WHEAT -> new Wheat(id, position);
            case CORN -> new Corn(id, position);
            case TOMATO -> new Tomato(id, position);
            case CARROT -> new Carrot(id, position);
            case PUMPKIN -> new Pumpkin(id, position);
        };
    }
}

