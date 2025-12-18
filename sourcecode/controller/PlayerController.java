package controller;
import core.*;
import eventSystem.RandomEventManager;
import player.Player;
import utility.*;
import exceptions.*;
public class PlayerController {

    private Player player;
    private Farm farm;

    public PlayerController(Player player, Farm farm) {
        this.player = player;
        this.farm = farm;
    }
    public boolean plantCrop(CropType type, Point position) {
        try {
            FarmCell cell = farm.getCell(position);  
            Crop crop = CropFactory.createCrop(type, position);

            cell.plantCrop(crop);     
            player.removeSeed(type, 1); 

            return true;

        } catch (InvalidPositionException |
                 CellOccupiedException |
                 NotEnoughResourceException e) {

            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean waterCrop(Point position, int amount) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();
            player.useWater(amount);
            crop.water(amount);

            return true;

        } catch (InvalidPositionException |
                 NotEnoughResourceException |
                 IllegalStateException e) {

            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean fertilizeCrop(Point position, int amount) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop =  cell.requireCrop();

            player.useFertilizer(amount);
            crop.fertilize(amount);
            return true;
        } catch (InvalidPositionException |
                 NotEnoughResourceException |
                 IllegalStateException e) {

            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean harvestCrop(Point position) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();
            int moneyEarned = crop.harvest();
            player.earnMoney(moneyEarned);
            cell.removeCrop();

            return true;

        } catch (InvalidPositionException |
                 IllegalStateException e) {

            System.out.println(e.getMessage());
            return false;
        }
    
}
    public boolean buySeed(CropType type, int amount) {
    	try{
    		int cost = type.getSeedPrice() * amount;
    		player.spendMoney(cost);
    		player.addSeed(type,amount);
    		return true;
    	}catch (NotEnoughResourceException e) {
    		System.out.println(e.getMessage());
    		return false;
    	}
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


