import java.util.*;
import java.util.stream.*;

public class FlatMapDemo {
    public static void main(String[] args) {
        List<List<String>> nestedData = Arrays.asList(
                Arrays.asList("React", "Angular", "Vue"),
                Arrays.asList("Angular", "Spring", "Django"),
                Arrays.asList("React", "Node", "Express"));

        System.out.println("Original nested list: " + nestedData);

        List<String> flattened = nestedData.stream()
                .flatMap(List::stream)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        System.out.println("Flattened, deduplicated, reverse sorted: " + flattened);
    }
}
