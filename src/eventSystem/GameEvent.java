package eventSystem;

import core.Farm;
import utility.NotificationType;

public abstract class GameEvent {
    protected String name;
    protected NotificationType notificationType;
    public GameEvent(String name, NotificationType notificationType){
        this.name=name;
        this.notificationType=notificationType;
    }
    public String getNameEvent(String name ){
        return name;
    }
    public NotificationType getNotificationType(NotificationType notificationType){
        return notificationType;
    }
    public abstract String triggerEvent(Farm farm);
}
