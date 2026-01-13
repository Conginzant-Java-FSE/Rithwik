import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Listproblem {
    public static void main(String[] args) {
        List<Integer> rawScores = new ArrayList<>();
        rawScores.add(9);
        rawScores.add(10);
        rawScores.add(12);
        rawScores.add(8);
        rawScores.add(-1);
        rawScores.add(7);
        rawScores.add(11);
        rawScores.add(10);
        rawScores.add(6);
        List<Integer> validScores = new ArrayList<>();
        double totalSum = 0;
        for (int score : rawScores) {
            if (score >= 1 && score <= 10) {
                validScores.add(score);
                totalSum = totalSum + score;
            }
        }
        Collections.sort(validScores);
        int maxScore = Collections.max(validScores);
        int minScore = Collections.min(validScores);
        double finalAvg = totalSum / validScores.size();
        System.out.println("Cleaned Ratings: " + validScores);
        System.out.println("Highest Rating: " + maxScore);
        System.out.println("Lowest Rating: " + minScore);
        System.out.printf("Average Rating: %.2f%n", finalAvg);
    }
}