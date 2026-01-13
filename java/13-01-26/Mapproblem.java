import java.util.Map;
import java.util.TreeMap;

public class Mapproblem {
    public static void main(String[] args) {
        Map<String, Integer> warehouse = new TreeMap<>();
        warehouse.put("MacBook", 5);
        warehouse.put("iPhone", 10);
        warehouse.put("AirPods", 25);
        Map<String, Integer> newArrivals = new TreeMap<>();
        newArrivals.put("iPhone", 5);
        newArrivals.put("AirPods", 5);
        newArrivals.put("VisionPro", 2);
        for (String item : newArrivals.keySet()) {
            int amountToAdd = newArrivals.get(item);
            if (warehouse.containsKey(item)) {
                int oldAmount = warehouse.get(item);
                warehouse.put(item, oldAmount + amountToAdd);
            } else {
                warehouse.put(item, amountToAdd);
            }
        }
        System.out.println("Updated Stock:");
        int globalTotal = 0;
        for (String key : warehouse.keySet()) {
            int val = warehouse.get(key);
            System.out.println(key + " -> " + val);
            globalTotal = globalTotal + val;
        }

        System.out.println("\nTotal Units in Store: " + globalTotal);
    }
}