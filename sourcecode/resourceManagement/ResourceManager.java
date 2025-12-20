package resourceManagement;
import player.Inventory;
import utility.CropType;

public class ResourceManager {
    private static final int WATER_PRICE = 2;
    private static final int FERTILIZER_PRICE = 3;

    public ResourceManager() {
    }

    public void sellWaterToPlayer(Inventory inventory, int amount) {
        int totalCost = amount * WATER_PRICE;
        inventory.spendMoney(totalCost);
        inventory.gainWater(amount*25);

    }

    public void sellFertilizerToPlayer(Inventory inventory, int amount) {
        int totalCost = amount * FERTILIZER_PRICE;
        inventory.spendMoney(totalCost);
        inventory.gainFertilizer(amount*25);

    }

    public void sellSeedToPlayer(Inventory inventory,CropType type, int amount) {
        int cost = type.getSeedPrice() * amount;
        inventory.spendMoney(cost);
        inventory.addSeed(type,amount);
    }
    public void sellMedicineToPLayer(Inventory inventory, int amount) {
    	int MEDICINE_PRICE = 50;
    	int totalCost = MEDICINE_PRICE * amount;
    	inventory.spendMoney(totalCost);
    	inventory.gainMedicine(amount);
    }
}