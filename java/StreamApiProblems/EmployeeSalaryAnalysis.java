// Stream API - Employee Salary Analysis

import java.util.*;
import java.util.stream.Collectors;

// Analyzes employee salary data
public class EmployeeSalaryAnalysis {
    public static void main(String[] args) {
        // data format: Name:Salary
        List<String> data = Arrays.asList(
                "Arjun:60000", "Bhavya:45000", "Chirag:75000",
                "Divya:60000", "Esha:90000", "Farhan:55000");

        System.out.println("Raw Data: " + data);

        List<Integer> result = data.stream()
                // 1. extract salary part
                .map(s -> s.split(":")[1])
                // 2. convert to integer
                .map(Integer::parseInt)
                // 3. filter > 50000
                .filter(sal -> sal > 50000)
                // 4. calculate annual package
                .map(sal -> sal * 12)
                // 5. remove duplicates
                .distinct()
                // 6. sort descending
                .sorted(Comparator.reverseOrder())
                // 7. skip top earner
                .skip(1)
                // 8. take next 2
                .limit(2)
                .collect(Collectors.toList());

        System.out.println("Processed Salaries: " + result);
        System.out.println("Count: " + result.size());
    }
}
