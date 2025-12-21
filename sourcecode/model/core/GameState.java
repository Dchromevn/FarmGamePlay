package model.core;

import model.player.Player;
import model.notification.NotificationManager;
import service.eventSystem.RandomEventManager;
import java.io.Serializable;
public class GameState implements Serializable {
    private Farm farm;
    private Player player;
    private NotificationManager notificationManager;
    private RandomEventManager eventManager;
    public GameState(Farm farm, Player player, NotificationManager notificationManager, RandomEventManager eventManager) {
        this.farm = farm;
        this.player = player;
        this.notificationManager = notificationManager;
        this.eventManager = eventManager;
    }
    public Farm getFarm() {
        return farm;
    }
    public Player getPlayer() {
        return player;
    }
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
    public RandomEventManager getEventManager() {
        return eventManager;
    }
}