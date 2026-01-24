import java.util.*;

public class IteratorBehavior {
    public static void main(String[] args) {
        System.out.println("=== Fail-Fast Demo with Iterator ===");
        demonstrateFailFast();

        System.out.println("\n=== Safe Removal using Iterator.remove() ===");
        safeRemoval();
    }

    public static void demonstrateFailFast() {
        List<Integer> list = new ArrayList<>(Arrays.asList(10, 20, 30, 40, 50));

        try {
            Iterator<Integer> iterator = list.iterator();
            while (iterator.hasNext()) {
                Integer value = iterator.next();
                if (value == 30) {
                    list.remove(value);
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException thrown!");
            System.out.println("Reason: List was modified directly during iteration");
        }
    }

    public static void safeRemoval() {
        List<Integer> list = new ArrayList<>(Arrays.asList(10, 20, 30, 40, 50));
        System.out.println("Before: " + list);

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            if (value == 30) {
                iterator.remove();
            }
        }

        System.out.println("After safe removal: " + list);
    }
}
