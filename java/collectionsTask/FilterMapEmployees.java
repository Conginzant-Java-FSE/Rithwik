import java.util.*;
import java.util.stream.*;

class Worker {
    int id;
    String name;
    double salary;

    Worker(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
}

public class FilterMapEmployees {
    public static void main(String[] args) {
        List<Worker> workers = Arrays.asList(
                new Worker(1, "Ramesh", 80000),
                new Worker(2, "Sunil", 65000),
                new Worker(3, "Deepak", 90000),
                new Worker(4, "Vijay", 72000),
                new Worker(5, "Arjun", 85000));

        List<String> highEarnerNames = workers.stream()
                .filter(w -> w.salary > 75000)
                .map(w -> w.name)
                .sorted()
                .collect(Collectors.toList());

        System.out.println("Employees with salary > 75000 (sorted): " + highEarnerNames);
    }
}
