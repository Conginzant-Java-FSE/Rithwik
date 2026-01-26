// Duplicate Free Hiring Portal using HashSet

import java.util.*;

// removes duplicate applicants using HashSet
public class DuplicateFreeHiringPortal {
    public static void main(String[] args) {
        // list with duplicate names
        List<String> applicants = Arrays.asList(
                "Ananya", "Rohan", "Kavya", "Ananya",
                "Deepak", "Rohan", "Kavya", "Priyanka");

        System.out.println("Total Applications: " + applicants.size());

        // use hashset to remove duplicates
        Set<String> unique = new HashSet<>(applicants);

        int duplicates = applicants.size() - unique.size();
        System.out.println("Duplicates Found: " + duplicates);
        System.out.println("Unique Applicants: " + unique.size());

        // print unique names
        System.out.println("\nUnique Applicants:");
        for (String name : unique) {
            System.out.println("- " + name);
        }
    }
}
