class PrimePrinter implements Runnable {

    int start;
    int end;

    PrimePrinter(int start, int end) {
        this.start = start;
        this.end = end;
    }

    boolean isPrime(int n) {
        if (n <= 1) return false;

        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    public void run() {
        for (int i = start; i <= end; i++) {
            if (isPrime(i)) {
                System.out.println(Thread.currentThread().getName() + " : " + i);
            }
        }
    }
}

public class PrimeNoThreads {

    public static void main(String[] args) throws InterruptedException {

        int totalThreads = 10;
        int rangePerThread = 100;

        Thread[] threads = new Thread[totalThreads];

        int start = 1;

        for (int i = 0; i < totalThreads; i++) {

            int end = start + rangePerThread - 1;

            threads[i] = new Thread(
                    new PrimePrinter(start, end),
                    "Thread-" + (i + 1)
            );

            threads[i].start();
            start = end + 1;
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Done");
    }
}
