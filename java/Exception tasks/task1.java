package Exception_Tasks;
//import org.slf4j.Logger;

//import org.slf4j.LoggerFactory;

class PotionExplosionException extends Exception {
    public PotionExplosionException(String message) {
        super(message);
    }
}

public class task1 {
    // private static final org.slf4j.Logger log =
    // org.slf4j.LoggerFactory.getLogger(PotionBrewing.class);

    // TODO: Create PotionExplosionException class extending Exception

    public static void brewPotion(int dragonBloodDrops) throws PotionExplosionException {
        // TODO: If drops > 5, throw new PotionExplosionException

        if (dragonBloodDrops > 5) {
            throw new PotionExplosionException("Potion Explosion");
        } else {
            System.out.println("Log Success");
        }
        // TODO: Log success if potion brewed properly

    }

    public static void main(String[] args) {
        try {
            brewPotion(6); // Expect failure
        } catch (PotionExplosionException e) {
            // TODO: Log the exception
            System.out.println(e.getMessage());
        }
    }
}
