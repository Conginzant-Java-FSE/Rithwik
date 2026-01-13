public class task6 {
    public static void main(String[] args) {
        int[] scores = {9, 10, 12, 8, -1, 7, 11, 10, 6};
        int[] valid = new int[scores.length];
        int idx = 0, sum = 0, min = 10, max = 1;
        for (int s : scores) {
            if (s >= 1 && s <= 10) {
                valid[idx++] = s;
                sum += s;
                if (s < min) min = s;
                if (s > max) max = s;
            }
        }
        for (int i = 0; i < idx - 1; i++) {
            for (int j = i + 1; j < idx; j++) {
                if (valid[i] > valid[j]) {
                    int t = valid[i];
                    valid[i] = valid[j];
                    valid[j] = t;
                }
            }
        }
        System.out.print("Good scores: ");
        for (int i = 0; i < idx; i++) System.out.print(valid[i] + " ");
        System.out.println("\nHighest: " + max);
        System.out.println("Lowest: " + min);
        System.out.printf("Mean: %.2f", (double) sum / idx);
    }
}