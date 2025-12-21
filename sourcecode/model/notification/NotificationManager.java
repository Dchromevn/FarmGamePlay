package model.notification;

import utility.NotificationType;
import java.io.Serializable;
import java.util.LinkedList;

public class NotificationManager implements Serializable {
    private LinkedList<Notification> notifications;
    private static final int HISTORY_LIMIT = 100;

    public NotificationManager() {
        this.notifications = new LinkedList<>();
    }

    public void addNotification(Notification notification) {
        if (notification == null) {
            System.out.println("Cannot add null model.notification!");
            return;
        }
        notifications.add(notification);
        if (notifications.size() > HISTORY_LIMIT) {
            notifications.removeFirst();
        }
        System.out.println(notification.toString());
    }

    public void addNotification(String message, NotificationType type, int gameDay) {
        Notification notification = new Notification(message, type, gameDay);
        addNotification(notification);
    }
}
