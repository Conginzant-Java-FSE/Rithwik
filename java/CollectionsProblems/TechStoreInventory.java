// Tech Store Inventory using TreeMap

import java.util.*;

// inventory management using TreeMap
public class TechStoreInventory {
    public static void main(String[] args) {
        // current stock
        Map<String, Integer> stock = new TreeMap<>();
        stock.put("Laptop", 8);
        stock.put("Phone", 15);
        stock.put("Earbuds", 30);

        // new arrival
        Map<String, Integer> newStock = new HashMap<>();
        newStock.put("Phone", 5);
        newStock.put("Earbuds", 5);
        newStock.put("Watch", 12);

        System.out.println("Current Stock:");
        for (String item : stock.keySet()) {
            System.out.println(item + ": " + stock.get(item));
        }

        // merge new stock
        for (String item : newStock.keySet()) {
            int qty = newStock.get(item);
            if (stock.containsKey(item)) {
                stock.put(item, stock.get(item) + qty);
            } else {
                stock.put(item, qty);
            }
        }

        System.out.println("\nUpdated Stock:");
        int total = 0;
        for (String item : stock.keySet()) {
            int qty = stock.get(item);
            System.out.println(item + ": " + qty);
            total += qty;
        }
        System.out.println("\nTotal Units: " + total);
    }
}
