// Airport Runway Simulation - synchronization

// shared runway resource
class Runway {
    // only one plane can use runway at a time
    public synchronized void useRunway(String planeId) {
        System.out.println(planeId + " requesting takeoff...");
        System.out.println(planeId + " taking off...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(planeId + " has taken off!\n");
    }
}

// plane thread
class Plane extends Thread {
    String planeId;
    Runway runway;

    Plane(String planeId, Runway runway) {
        this.planeId = planeId;
        this.runway = runway;
    }

    public void run() {
        runway.useRunway(planeId);
    }
}

public class AirportRunwaySimulation {
    public static void main(String[] args) {
        Runway runway = new Runway();

        // create plane threads
        Plane p1 = new Plane("AI-101", runway);
        Plane p2 = new Plane("BA-202", runway);
        Plane p3 = new Plane("EK-303", runway);

        // start all planes
        p1.start();
        p2.start();
        p3.start();
    }
}
