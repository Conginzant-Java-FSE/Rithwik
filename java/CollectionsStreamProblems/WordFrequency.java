import java.util.*;

public class WordFrequency {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("Apple", "Banana", "apple", "Cherry", "banana", "APPLE", "Date", "cherry");
        
        Map<String, Integer> frequencyMap = new TreeMap<>();
        
        for (String word : words) {
            String lowercaseWord = word.toLowerCase();
            frequencyMap.put(lowercaseWord, frequencyMap.getOrDefault(lowercaseWord, 0) + 1);
        }
        
        System.out.println("Word frequencies in alphabetical order:");
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
