// Potion Brewing - checked exception

// custom checked exception
class PotionExplosionException extends Exception {
    PotionExplosionException(String msg) {
        super(msg);
    }
}

public class PotionBrewing {
    // brews potion with dragon blood
    public static void brewPotion(int drops) throws PotionExplosionException {
        System.out.println("Adding " + drops + " drops of dragon blood...");
        if (drops > 5) {
            throw new PotionExplosionException("Too many drops! Max is 5");
        }
        System.out.println("Potion brewed successfully!");
    }

    public static void main(String[] args) {
        // attempt 1: safe brewing
        System.out.println("Attempt 1:");
        try {
            brewPotion(4);
        } catch (PotionExplosionException e) {
            System.out.println("EXPLOSION! " + e.getMessage());
        }

        // attempt 2: too many drops
        System.out.println("\nAttempt 2:");
        try {
            brewPotion(8);
        } catch (PotionExplosionException e) {
            System.out.println("EXPLOSION! " + e.getMessage());
        }

        // attempt 3: edge case
        System.out.println("\nAttempt 3:");
        try {
            brewPotion(5);
        } catch (PotionExplosionException e) {
            System.out.println("EXPLOSION! " + e.getMessage());
        }
    }
}
