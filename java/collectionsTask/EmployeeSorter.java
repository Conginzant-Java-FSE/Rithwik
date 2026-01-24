import java.util.*;

class Staff {
    int id;
    String name;
    double salary;

    Staff(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public String toString() {
        return "Staff{id=" + id + ", name='" + name + "', salary=" + salary + "}";
    }
}

public class EmployeeSorter {
    public static void main(String[] args) {
        List<Staff> staffList = new ArrayList<>();
        staffList.add(new Staff(3, "Ravi", 50000));
        staffList.add(new Staff(1, "Kumar", 75000));
        staffList.add(new Staff(5, "Anil", 75000));
        staffList.add(new Staff(2, "Anil", 75000));
        staffList.add(new Staff(4, "Suresh", 60000));

        Collections.sort(staffList, new Comparator<Staff>() {
            public int compare(Staff s1, Staff s2) {
                if (s1.salary != s2.salary) {
                    return Double.compare(s2.salary, s1.salary);
                }
                if (!s1.name.equals(s2.name)) {
                    return s1.name.compareTo(s2.name);
                }
                return Integer.compare(s1.id, s2.id);
            }
        });

        System.out.println("Sorted Staff List:");
        for (Staff s : staffList) {
            System.out.println(s);
        }
    }
}
