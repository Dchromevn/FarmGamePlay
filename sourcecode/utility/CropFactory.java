package utility;

import model.crops.Crop;

public class CropFactory {
    private static int cropIdCounter = 0;
    public static Crop createCrop(CropType type, Point position) {
        String id = type.getCropName() + "_" + (++cropIdCounter);
        return new Crop(id, position, type);
    }
}

