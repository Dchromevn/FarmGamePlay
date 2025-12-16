package controller;
import core.Farm;
import core.FarmCell;
import core.Crop;
import eventSystem.RandomEventManager;
import player.Player;
import utility.CropType;
import utility.Point;

public class PlayerController {

    private Player player;
    private Farm farm;

    public PlayerController(Player player, Farm farm) {
        this.player = player;
        this.farm = farm;
    }

    public boolean plantCrop(CropType type, Point position) {
        if (player.getSeedCount(type) <= 0) return false;

        FarmCell cell = farm.getCell(position);
        if (cell == null || !cell.isEmpty()) return false;

        Crop crop = utility.CropFactory.createCrop(type, position);
        cell.plantCrop(crop);

        player.removeSeed(type, 1);
        return true;
    }

    public boolean waterCrop(Point position, int amount) {
        FarmCell cell = farm.getCell(position);
        if (cell == null || cell.isEmpty()) return false;

        if (player.useWater(amount)) {
            cell.getCrop().water(amount);
            return true;
        }

        return false;
    }
    public boolean fertilizeCrop(Point position, int amount) {
    	FarmCell cell = farm.getCell(position);
    	if (cell == null || cell.isEmpty()) return false;
    	if (player.useFertilizer(amount)) {
    		cell.getCrop().fertilize(amount);
    		return true;
    	}
    	return false;
    }
    public boolean harvestCrop(Point position) {
    	FarmCell cell = farm.getCell(position);
    	if (cell == null || cell.isEmpty()) return false;
    	Crop crop = cell.getCrop();
    	if (!crop.isHarvestable()) return false;
    	int moneyEarned = crop.harvest();
    	player.earnMoney(moneyEarned);
    	cell.removeCrop();
    	return true;
    }
    public boolean buySeed(CropType type, int amount) {
    	int cost = type.getSeedPrice() * amount;
    	if (!player.spendMoney(cost)) return false;
    	player.addSeed(type, amount);
    	return true;
    }
    public void nextDay(RandomEventManager eventManager) {
        farm.advanceDay(eventManager);
    }
    public void displayInventory() {
    	player.showInventory();
    }
    public void printPlayerStatus() {
    	System.out.println(player);
    }
    public void printFarmStatus() {
    	farm.printFarm();
    }
}
