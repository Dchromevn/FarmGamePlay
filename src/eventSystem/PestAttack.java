package eventSystem;

import core.Crop;
import core.Farm;
import utility.NotificationType;

import java.util.List;
import java.util.Random;

public class PestAttack extends GameEvent{
    public PestAttack(){
        super("Pest Attack", NotificationType.EVENT);
    }
    @Override
    public String triggerEvent(Farm farm) {
        List<Crop> crops = farm.getAllCrops();
        if (crops.isEmpty()) return "Nothing on the farm for pests to attack";

        Random rand = new Random();
        int affected = 0;
        for (Crop crop : crops) {
            if (!crop.isDead() && rand.nextInt(100) < 30) {
                crop.takeDamage(30);
                affected++;
            }
        }
        if(affected==1) return "Pests appear and attack " + affected + " crop.";
        return "Pests appear and attack " + affected + " crops.";
    }
}
