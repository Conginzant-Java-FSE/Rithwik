// Train Station Display - synchronization

import java.text.SimpleDateFormat;
import java.util.Date;

// shared display board
class DisplayBoard {
    // synchronized update
    public synchronized void update(String trainName) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        System.out.println(trainName + " arrived at " + time);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(trainName + " update done\n");
    }
}

// train thread
class Train extends Thread {
    String trainName;
    DisplayBoard board;

    Train(String name, DisplayBoard board) {
        this.trainName = name;
        this.board = board;
    }

    public void run() {
        try {
            // random arrival delay
            Thread.sleep((long) (Math.random() * 3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        board.update(trainName);
    }
}

public class TrainStationDisplay {
    public static void main(String[] args) {
        DisplayBoard board = new DisplayBoard();

        // create trains
        Train t1 = new Train("Rajdhani Express", board);
        Train t2 = new Train("Shatabdi Express", board);
        Train t3 = new Train("Duronto Express", board);

        // start all trains
        t1.start();
        t2.start();
        t3.start();
    }
}
