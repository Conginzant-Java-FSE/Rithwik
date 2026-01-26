public class Asynch {
    private static final int SIZE = 6;
    private static int[][] matrix = {
        {1, 2, 3, 4, 5, 6},
        {7, 8, 9, 10, 11, 12},
        {13, 14, 15, 16, 17, 18},
        {19, 20, 21, 22, 23, 24},
        {25, 26, 27, 28, 29, 30},
        {31, 32, 33, 34, 35, 36}
    };
    private static int[] rowSums = new int[SIZE];
    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[SIZE];
        for (int i = 0; i < SIZE; i++) {
            final int row = i;
            threads[i] = new Thread(() -> {
                int sum = 0;
                for (int j = 0; j < SIZE; j++) {
                    sum += matrix[row][j];
                }
                rowSums[row] = sum;
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        for (int i = 0; i < SIZE; i++) {
            System.out.println("Sum of row " + (i + 1) + ": " + rowSums[i]);
        }
    }
}
