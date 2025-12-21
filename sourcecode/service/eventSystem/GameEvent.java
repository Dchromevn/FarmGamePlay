package service.eventSystem;

import model.core.Farm;
import utility.NotificationType;

public abstract class GameEvent {
    protected String name;
    protected NotificationType notificationType;
    public GameEvent(String name, NotificationType notificationType){
        this.name=name;
        this.notificationType=notificationType;
    }
    public abstract String triggerEvent(Farm farm);
}
