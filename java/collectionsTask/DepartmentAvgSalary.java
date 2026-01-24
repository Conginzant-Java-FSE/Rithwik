import java.util.*;
import java.util.stream.*;

class TeamMember {
    String department;
    double salary;

    TeamMember(String department, double salary) {
        this.department = department;
        this.salary = salary;
    }
}

public class DepartmentAvgSalary {
    public static void main(String[] args) {
        List<TeamMember> team = Arrays.asList(
                new TeamMember("Engineering", 95000),
                new TeamMember("Marketing", 55000),
                new TeamMember("Engineering", 85000),
                new TeamMember("HR", 50000),
                new TeamMember("Marketing", 60000),
                new TeamMember("HR", 55000),
                new TeamMember("Engineering", 90000));

        Map<String, Double> avgSalaryByDept = team.stream()
                .collect(Collectors.groupingBy(
                        t -> t.department,
                        Collectors.averagingDouble(t -> t.salary)));

        System.out.println("Average salary by department:");
        avgSalaryByDept.forEach((dept, avg) -> System.out.println(dept + " : " + avg));
    }
}
