package utility;

import core.*;

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
    public static String getCropInfo(CropType type) {
        int totalDays = getTotalGrowthDays(type);
        return String.format(
                """
                        %s
                        Seed Price: $%d
                        Harvest Value: $%d
                        Growth Time: %d days
                       """,
                type.getCropName(),
                type.getSeedPrice(),
                type.getBasePriceCrop(),
                totalDays
        );
    }
    private static int getTotalGrowthDays(CropType type) {
        return switch (type) {
            case WHEAT -> 3;
            case CORN -> 5;
            case TOMATO -> 7;
            case CARROT -> 6;
            case PUMPKIN -> 9;
        };
    }
}

