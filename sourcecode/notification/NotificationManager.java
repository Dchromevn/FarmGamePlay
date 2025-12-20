package notification;

import utility.NotificationType;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationManager {
    private LinkedList<Notification> notifications;
    private static final int HISTORY_LIMIT = 100;
    public NotificationManager(){
        this.notifications=new LinkedList<>();
    }
    public void addNotification(Notification notification) {
        if (notification == null) {
            System.out.println("Cannot add null notification!");
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
    public List<Notification> getUnreadNotifications() {
        return notifications.stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }
    public List<Notification> getAllNotifications() {
        return new LinkedList<>(notifications);
    }
    public List<Notification> getRecentNotifications(int count) {
        int size = notifications.size();
        int actualCount = Math.min(count, size);
        int startIndex = size - actualCount;
        return new LinkedList<>(notifications.subList(startIndex, size));
    }
    public List<Notification> getNotificationsByType(NotificationType type) {
        return notifications.stream()
                .filter(n -> n.getType() == type)
                .collect(Collectors.toList());
    }
    public void markAllAsRead() {
        for (Notification notification : notifications) {
            notification.markAsRead();
        }
        System.out.println("All notifications marked as read");
    }
    public int getUnreadCount() {
        return (int) notifications.stream().filter(n -> !n.isRead()).count();
    }
}

