package eventSystem;

import core.Crop;
import core.Farm;
import utility.NotificationType;

public class Rain extends GameEvent{
    public Rain(){
        super("Raining", NotificationType.EVENT);
    }
    @Override
    public String triggerEvent(Farm farm){
        int waterAmount=25;
        for (Crop crop: farm.getAllCrops()){
            if(!crop.isDead()){
                crop.water(waterAmount);            }
        }
        return ("Rain appears, all crop gain "+ waterAmount+" water");
    }
}
