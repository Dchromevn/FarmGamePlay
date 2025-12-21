package service.eventSystem;

import model.crops.Crop;
import model.core.Farm;
import utility.NotificationType;

public class Drought extends GameEvent{
    public Drought(){
        super("Drought", NotificationType.EVENT);
    }
    @Override
    public String triggerEvent(Farm farm){
        int damageTaken= 10;
        for (Crop crop: farm.getAllCrops()){
            if(!crop.isDead()){
                crop.takeDamage(damageTaken);            }
        }
        return ("Drought appears, all crop suffer "+ damageTaken+" damages");
    }
}