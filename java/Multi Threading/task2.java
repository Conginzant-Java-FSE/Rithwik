import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class task2 {
    public static void main(String[] args) {
        List<Integer> prices = List.of(500, 1200, 2500, 1200, 3000, 800, 2500);
        List<Integer> num = prices.stream()
            .filter(n -> n > 1000)
            .distinct()
                    .sorted(Comparator.reverseOrder())
                            .collect(Collectors.toList());
        System.out.println("Processed Prices: " + num);
        long count = num.stream().filter(n -> n > 2000).count();
        System.out.println("Count of prices > 2000: "+count);

    }
}