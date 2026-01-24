import java.util.*;

public class RemoveDuplicates {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(5, 3, 8, 3, 1, 5, 9, 1, 8, 2, 5);
        List<Integer> result = removeDuplicates(numbers);
        System.out.println("Original: " + numbers);
        System.out.println("After removing duplicates: " + result);
    }

    public static List<Integer> removeDuplicates(List<Integer> input) {
        Set<Integer> seen = new LinkedHashSet<>();
        for (Integer num : input) {
            seen.add(num);
        }
        return new ArrayList<>(seen);
    }
}
