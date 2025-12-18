package utility;

public enum NotificationType {
    INFO("Information"),
    WARNING("Warning"),
    SUCCESS("Success"),
    EVENT("Event"),
    ERROR("Error"),
    CROP ("Crop Status");
    private final String notificationName;
    NotificationType(String notificationName){
        this.notificationName=notificationName;
    }
    public String getNotificationName(){
        return notificationName;
    }
}
