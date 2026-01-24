import java.util.*;

public class HighestFrequency {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(4, 5, 6, 4, 5, 4, 6, 6, 7, 5);

        Map<Integer, Integer> countMap = new HashMap<>();

        for (Integer num : numbers) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        }

        int highestFreq = 0;
        Integer result = null;

        for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
            int currentNum = entry.getKey();
            int currentFreq = entry.getValue();

            if (currentFreq > highestFreq) {
                highestFreq = currentFreq;
                result = currentNum;
            } else if (currentFreq == highestFreq && currentNum < result) {
                result = currentNum;
            }
        }

        System.out.println("Most frequent element: " + result);
        System.out.println("Its frequency: " + highestFreq);
    }
}
