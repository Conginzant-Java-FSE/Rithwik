// Hospital Emergency Queue using Collections

import java.util.*;

// Patient class to store name and severity
class Patient {
    String name;
    int severity;

    Patient(String name, int severity) {
        this.name = name;
        this.severity = severity;
    }
}

public class HospitalEmergencyQueue {
    public static void main(String[] args) {
        // create queue of patients
        Queue<Patient> queue = new LinkedList<>();
        queue.offer(new Patient("Rahul", 6));
        queue.offer(new Patient("Priya", 9));
        queue.offer(new Patient("Amit", 4));
        queue.offer(new Patient("Sneha", 10));
        queue.offer(new Patient("Vikram", 7));

        // list to store critical patients
        List<Patient> critical = new ArrayList<>();
        Iterator<Patient> it = queue.iterator();

        // separate critical patients (severity > 8)
        while (it.hasNext()) {
            Patient p = it.next();
            if (p.severity > 8) {
                critical.add(p);
                it.remove();
            }
        }

        // print critical cases first
        System.out.println("Critical Cases:");
        for (Patient p : critical) {
            System.out.println(p.name + " - Severity: " + p.severity);
        }

        // treat remaining patients
        System.out.println("\nRegular Queue:");
        while (!queue.isEmpty()) {
            Patient p = queue.poll();
            System.out.println("Treating: " + p.name);
        }
    }
}
