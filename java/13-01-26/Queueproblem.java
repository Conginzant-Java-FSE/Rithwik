import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
public class Queueproblem {
    static class Case {
        String patientName;
        int urgencyLevel;

        Case(String name, int level) {
            this.patientName = name;
            this.urgencyLevel = level;
        }
    }
    public static void main(String[] args) {
        Queue<Case> hospitalQueue = new LinkedList<>();
        hospitalQueue.add(new Case("Arjun", 5));
        hospitalQueue.add(new Case("Mia", 9));
        hospitalQueue.add(new Case("Leo", 7));
        hospitalQueue.add(new Case("Sara", 10));
        List<String> criticalList = new ArrayList<>();
        List<String> normalList = new ArrayList<>();
        while (!hospitalQueue.isEmpty()) {
            Case currentCase = hospitalQueue.poll();
            if (currentCase.urgencyLevel > 8) {
                criticalList.add(currentCase.patientName);
            } else {
                normalList.add(currentCase.patientName);
            }
        }
        for (String name : criticalList) {
            System.out.println("Emergency case -> " + name);
        }
        for (String name : normalList) {
            System.out.println("Treating -> " + name);
        }
    }
}