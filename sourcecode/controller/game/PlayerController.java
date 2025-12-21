package controller.game;

import model.core.*;
import model.crops.Crop;
import model.notification.NotificationManager;
import model.player.*;
import utility.*;
import model.exceptions.*;
import model.resourceManagement.ResourceManager;
import service.eventSystem.RandomEventManager;
import service.save.GameSaveManager;
import java.io.*;
public class PlayerController {
    private Player player;
    private Farm farm;
    private ResourceManager shop;
    private NotificationManager notificationManager;
    private GameSaveManager gameSaveManager;
    private RandomEventManager eventManager;
    public PlayerController(Player player, Farm farm, NotificationManager notificationManager, ResourceManager shop, RandomEventManager eventManager) {
        this.player = player;
        this.farm = farm;
        this.notificationManager = notificationManager;
        this.shop = shop;
        this.gameSaveManager = new GameSaveManager();
        this.eventManager = eventManager;
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

        } catch (InvalidPositionException |
                 CellOccupiedException |
                 NotEnoughResourceException e) {
            // Nếu có lỗi (hết hạt, ô đất có cây rồi...), thông báo và trả về false
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }
    public boolean waterCrop(Point position, int amount) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();

            if (crop.isHarvestable()) {
                notificationManager.addNotification(
                        crop.getCropType().getCropName() + " is harvestable. Cannot water",
                        NotificationType.WARNING,
                        farm.getCurrentDay()
                );
                return false;
            }
            
            if (crop.isDead()) {
                notificationManager.addNotification(
                        "Cannot water dead crop! Recycle it instead.",
                        NotificationType.WARNING,
                        farm.getCurrentDay()
                );
                return false;
            }

            player.getInventory().useWater(amount);
            crop.water(amount);
            return true;

        } catch (InvalidPositionException |
                 NotEnoughResourceException |
                 IllegalStateException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }

    public boolean fertilizeCrop(Point position, int amount) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();

            if (crop.isHarvestable()) {
                notificationManager.addNotification(
                        crop.getCropType().getCropName() + " is harvestable. Cannot fertilize",
                        NotificationType.WARNING,
                        farm.getCurrentDay()
                );
                return false;
            }
            
            if (crop.isDead()) {
                notificationManager.addNotification(
                        "Cannot fertilize dead crop! Recycle it instead.",
                        NotificationType.WARNING,
                        farm.getCurrentDay()
                );
                return false;
            }

            player.getInventory().useFertilizer(amount);
            crop.fertilize(amount);
            return true;

        } catch (InvalidPositionException |
                 NotEnoughResourceException |
                 IllegalStateException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }

    public boolean harvestCrop(Point position) {
        try {
            FarmCell cell = farm.getCell(position);
            Crop crop = cell.requireCrop();
            if (!crop.isHarvestable()) {
                notificationManager.addNotification(
                        crop.getCropType().getCropName() + " is not ready for harvesting",
                        NotificationType.WARNING,
                        farm.getCurrentDay()
                );
                return false;
            }
            int moneyEarned = crop.harvest();
            player.getInventory().earnMoney(moneyEarned);
            cell.removeCrop();
            notificationManager.addNotification(
                "Harvested " + crop.getCropType().getCropName() + " (+" + moneyEarned + "$)", 
                NotificationType.SUCCESS, 
                farm.getCurrentDay()
            );

            return true;

        } catch (InvalidPositionException | IllegalStateException e) {
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

        } catch (NotEnoughResourceException | IllegalStateException | InvalidPositionException e) {
            notificationManager.addNotification(e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
            return false;
        }
    }
    public void buySeed(CropType type, int amount) {
        try {
            shop.sellSeedToPlayer(player.getInventory(), type, amount);
            notificationManager.addNotification(
                    "Purchased " + amount + " " + type.getCropName() + " seeds",
                    NotificationType.SUCCESS,
                    farm.getCurrentDay()
            );
        } catch (NotEnoughResourceException e) {
            notificationManager.addNotification("Failed to buy seeds: Not enough money", NotificationType.ERROR, farm.getCurrentDay());
        }
    }

    public void buyWater(int amount) {
        try {
            shop.sellWaterToPlayer(player.getInventory(), amount);
            notificationManager.addNotification("Bought " + amount + " water units", NotificationType.SUCCESS, farm.getCurrentDay());
        } catch (NotEnoughResourceException e) {
            notificationManager.addNotification("Failed to buy water: Not enough money", NotificationType.ERROR, farm.getCurrentDay());
        }
    }

    public void buyFertilizer(int amount) {
        try {
            shop.sellFertilizerToPlayer(player.getInventory(), amount);
            notificationManager.addNotification("Bought " + amount + " fertilizer units", NotificationType.SUCCESS, farm.getCurrentDay());
        } catch (NotEnoughResourceException e) {
            notificationManager.addNotification("Failed to buy fertilizer: Not enough money", NotificationType.ERROR, farm.getCurrentDay());
        }
    }

    public void buyMedicine(int amount) {
        try {
            shop.sellMedicineToPLayer(player.getInventory(), amount);
            notificationManager.addNotification("Bought " + amount + " medicine.", NotificationType.SUCCESS, farm.getCurrentDay());
        } catch (NotEnoughResourceException e) {
            notificationManager.addNotification("Failed to buy medicine: " + e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
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

    public void saveGameCommand(String filename) {
        try {
            GameState state = new GameState(this.farm, this.player, this.notificationManager,this.eventManager);
            gameSaveManager.saveGame(state, filename);
            notificationManager.addNotification("Save successfully." + filename, NotificationType.SUCCESS, farm.getCurrentDay());

        } catch (IOException e) {
            notificationManager.addNotification("Save failed: " + e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
        }
    }

    public void loadGameCommand(String filename) {
        try {
            GameState loadState = gameSaveManager.loadGame(filename);
            this.farm = loadState.getFarm();
            this.player = loadState.getPlayer();
            this.notificationManager = loadState.getNotificationManager();
            this.eventManager = loadState.getEventManager();
            notificationManager.addNotification("Welcome back! " , NotificationType.SUCCESS, farm.getCurrentDay());
            

        } catch (FileNotFoundException e) {
            notificationManager.addNotification("Cannot find saved game.", NotificationType.WARNING, farm.getCurrentDay());
        } catch (IOException | ClassNotFoundException e) {
        	notificationManager.addNotification("Load failed: " + e.getMessage(), NotificationType.ERROR, farm.getCurrentDay());
        }
    }
}