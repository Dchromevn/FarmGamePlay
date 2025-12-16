package eventSystem;

import java.util.Random;

public class RandomEventManager {
    private final Random random = new Random();

    public GameEvent triggerRandomEvent() {
        int roll = random.nextInt(100);
        if (roll < 20) {
            return new Rain();
        } else if (roll < 30) {
            return new Drought();
        } else if (roll < 40) {
            return new PestAttack();
        } else {
            return null;
        }
    }
}

