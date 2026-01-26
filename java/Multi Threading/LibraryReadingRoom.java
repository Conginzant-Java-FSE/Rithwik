// Library Reading Room - wait/notify

// reading room with limited seats
class ReadingRoom {
    int totalSeats;
    int occupied = 0;

    ReadingRoom(int seats) {
        this.totalSeats = seats;
    }

    // enter room - wait if full
    public synchronized void enter(String name) {
        while (occupied >= totalSeats) {
            System.out.println(name + " waiting...");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        occupied++;
        System.out.println(name + " entered. Seats: " + occupied + "/" + totalSeats);
    }

    // leave room - notify waiting
    public synchronized void leave(String name) {
        occupied--;
        System.out.println(name + " left. Seats: " + occupied + "/" + totalSeats);
        notifyAll();
    }
}

// student thread
class Student extends Thread {
    String name;
    ReadingRoom room;

    Student(String name, ReadingRoom room) {
        this.name = name;
        this.room = room;
    }

    public void run() {
        room.enter(name);
        try {
            Thread.sleep(2000); // study time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        room.leave(name);
    }
}

public class LibraryReadingRoom {
    public static void main(String[] args) {
        // room with 2 seats only
        ReadingRoom room = new ReadingRoom(2);

        // 5 students trying to enter
        new Student("Arjun", room).start();
        new Student("Priya", room).start();
        new Student("Rahul", room).start();
        new Student("Sneha", room).start();
        new Student("Vikram", room).start();
    }
}
