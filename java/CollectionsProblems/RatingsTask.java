// Ratings Task - clean and analyze ratings

import java.util.*;

// cleans and analyzes ratings
public class RatingsTask {
    public static void main(String[] args) {
        // raw ratings with some invalid values
        List<Integer> ratings = Arrays.asList(8, 10, 15, 7, -2, 9, 11, 8, 6, 10, 0, 5);
        System.out.println("Raw Ratings: " + ratings);

        // filter valid ratings (1-10)
        List<Integer> clean = new ArrayList<>();
        for (int r : ratings) {
            if (r >= 1 && r <= 10) {
                clean.add(r);
            }
        }
        System.out.println("Clean Ratings: " + clean);

        // sort ratings
        Collections.sort(clean);
        System.out.println("Sorted: " + clean);

        // find min and max
        int min = clean.get(0);
        int max = clean.get(clean.size() - 1);
        System.out.println("Lowest: " + min);
        System.out.println("Highest: " + max);

        // calculate average
        int sum = 0;
        for (int r : clean) {
            sum += r;
        }
        double avg = (double) sum / clean.size();
        System.out.printf("Average: %.2f%n", avg);
    }
}
