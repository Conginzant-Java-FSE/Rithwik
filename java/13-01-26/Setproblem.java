package collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Setproblem {
    public static void main(String[] args) {
        List<String> candidateList = List.of("John", "Aisha", "Ravi", "John", "Mina", "Ravi", "Aisha", "Tom");
        Set<String> distinctNames = new HashSet<>();
        int duplicateCount = 0;
        for (String name : candidateList) {
            boolean isAdded = distinctNames.add(name);
            if (!isAdded) {
                duplicateCount++;
            }
        }
        System.out.println("Unique Applicants: " + distinctNames);
        System.out.println("Duplicates: " + duplicateCount);
    }
}