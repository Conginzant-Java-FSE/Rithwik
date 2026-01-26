// Stream API - Product Price Analysis

import java.util.*;
import java.util.stream.Collectors;

// Analyzes product prices
public class ProductPriceAnalysis {
    public static void main(String[] args) {
        List<Integer> prices = Arrays.asList(500, 1200, 2500, 1200, 3000, 800, 2500, 4000);
        System.out.println("All Prices: " + prices);

        List<Integer> processed = prices.stream()
                // filter high value items (> 1000)
                .filter(p -> p > 1000)
                // remove duplicates
                .distinct()
                // sort high to low
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        System.out.println("High Value Prices: " + processed);

        // count premium items (> 2000)
        long count = processed.stream()
                .filter(p -> p > 2000)
                .count();

        System.out.println("Premium Items (> 2000): " + count);
    }
}
