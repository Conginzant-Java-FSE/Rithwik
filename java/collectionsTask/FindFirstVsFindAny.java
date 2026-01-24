import java.util.*;
import java.util.stream.*;

public class FindFirstVsFindAny {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(7, 11, 4, 9, 2, 13, 6, 8);

        System.out.println("=== Using findFirst() with sequential stream ===");
        Optional<Integer> firstEven = numbers.stream()
                .filter(n -> n % 2 == 0)
                .findFirst();
        System.out.println("First even number: " + firstEven.orElse(null));

        System.out.println("\n=== Using findAny() with parallel stream ===");
        Optional<Integer> anyEven = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .findAny();
        System.out.println("Any even number: " + anyEven.orElse(null));

        System.out.println("\n=== Explanation ===");
        System.out.println("findFirst(): Returns the first element in encounter order");
        System.out.println("findAny(): Returns any element, optimized for parallel streams");
        System.out.println("In parallel streams, findAny() may return different results each run");
    }
}
