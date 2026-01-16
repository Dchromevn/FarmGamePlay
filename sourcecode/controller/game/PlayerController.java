package controller.game;

import model.core.*;
import model.crops.Crop;
import model.notification.NotificationManager;
import model.player.*;
import utility.*;
import model.exceptions.*;
import model.resourceManagement.*;
import service.eventSystem.RandomEventManager;
public class PlayerController {
    private Player player;
    private Farm farm;
    private Store shop;
    private NotificationManager notificationManager;
    private UIManager uiManager;
    public PlayerController(Player player, Farm farm, NotificationManager notificationManager, Store shop, RandomEventManager eventManager,UIManager uiManager) {
        this.player = player;
        this.farm = farm;
        this.notificationManager = notificationManager;
        this.shop = shop;
        this.uiManager = uiManager;
    }
    protected void setUIManager(UIManager uiManager) {
        if (this.uiManager != null) {
            throw new IllegalStateException("UIManager already set!");
        }
        this.uiManager = uiManager;
    }
    public Farm getFarm() { return this.farm; }
    public Player getPlayer() { return this.player; }
    public NotificationManager getNotificationManager() { return this.notificationManager; }

    public boolean plantCrop(CropType type, Point position) {
        try {
            FarmCell cell = farm.getCell(position);
            player.getInventory().removeSeed(type, 1);
            Crop crop = CropFactory.createCrop(type, position);
            cell.plantCrop(crop); 

            return true;

        } catch (InvalidPositionException |CellOccupiedException |NotEnoughResourceException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }
    public boolean waterCrop(Point position, int amount) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();
            if (crop.isHarvestable()) {
                notificationManager.addNotification(crop.getCropType().getCropName() + " is harvestable. Cannot water",NotificationType.WARNING,farm.getCurrentDay());
                return false;
            }
            if (crop.isDead()) {
                notificationManager.addNotification("Cannot water dead crop! Recycle it instead.",NotificationType.WARNING,farm.getCurrentDay());
                return false;
            }
            if (crop.isWaterFull()) {
            	notificationManager.addNotification(crop.getCropType().getCropName() +" is fully watered!", NotificationType.WARNING, farm.getCurrentDay());
            	return false;
            }
            player.getInventory().useWater(amount);
            crop.water(amount);
            return true;
        } catch (InvalidPositionException |NotEnoughResourceException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }

    public boolean fertilizeCrop(Point position, int amount) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();

            if (crop.isHarvestable()) {
                notificationManager.addNotification(crop.getCropType().getCropName() + " is harvestable. Cannot fertilize",NotificationType.WARNING,farm.getCurrentDay());
                return false;
            }
            if (crop.isDead()) {
                notificationManager.addNotification("Cannot fertilize dead crop! Recycle it instead.",NotificationType.WARNING,farm.getCurrentDay());
                return false;
            }
            if (crop.isFertilizerFull()) {
            	notificationManager.addNotification(crop.getCropType().getCropName() +" is fully fertilized!", NotificationType.WARNING, farm.getCurrentDay());
            	return false;
            }
            player.getInventory().useFertilizer(amount);
            crop.fertilize(amount);
            return true;
        } catch (InvalidPositionException | NotEnoughResourceException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }
    public boolean harvestCrop(Point position) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();
            if (!crop.isHarvestable()) {
                notificationManager.addNotification(crop.getCropType().getCropName() + " is not ready for harvesting", NotificationType.WARNING,farm.getCurrentDay());
                return false;
            }
            int moneyEarned = crop.harvest();
            player.getInventory().earnMoney(moneyEarned);
            cell.removeCrop();
            String message = "Harvested " + crop.getCropType().getCropName() + " (+" + moneyEarned + "$)";
            notificationManager.addNotification(message, NotificationType.SUCCESS, farm.getCurrentDay());
            if(uiManager != null) {
                uiManager.showNotification(message,farm.getCurrentDay());

            }
            return true;
        } catch (InvalidPositionException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }
    public boolean cureCrop(Point position) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();
            if (crop.isDead()) {
                notificationManager.addNotification("Cannot cure dead crop! Recycle it.", NotificationType.WARNING, farm.getCurrentDay());
                return false;
            }
            if (crop.getHealth() >= 100) {
                notificationManager.addNotification("Crop is already healthy!", NotificationType.WARNING, farm.getCurrentDay());
                return false;
            }
            player.getInventory().useMedicine(1);

            int remainingMeds = player.getInventory().getMedicine(); 
            
            crop.recoverHealth(30);

            notificationManager.addNotification(
                "Used Medicine on " + crop.getCropType().getCropName() +
                " (Left: " + remainingMeds + ")",
                NotificationType.SUCCESS,
                farm.getCurrentDay()
            );
            
            return true;

        } catch (NotEnoughResourceException | InvalidPositionException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }
    public boolean buyItem(StoreItem item, int amount) {
        try {
            shop.sellToPlayer(item, player.getInventory(), amount);
            
            String message = "Purchased " + amount + " " + item.getName();
            notificationManager.addNotification(message, NotificationType.SUCCESS, farm.getCurrentDay());
            return true;
            
        } catch (NotEnoughResourceException e) {
            notificationManager.addNotification("Failed to buy " + item.getName() + ": " + e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }
   

    public void recycleCrop(Point position) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();
            if (crop.canBeRecycled()) {
                int fertilizerGained = crop.recycle();
                player.getInventory().gainFertilizer(fertilizerGained);
                cell.removeCrop();
                notificationManager.addNotification("Recycled crop. Gained " + fertilizerGained + " fertilizer.", NotificationType.SUCCESS, farm.getCurrentDay());
            } else {
                notificationManager.addNotification("Crop is still healthy, cannot recycle.", NotificationType.WARNING, farm.getCurrentDay());
            }
        } catch (InvalidPositionException | IllegalStateException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());

        }
    }
}