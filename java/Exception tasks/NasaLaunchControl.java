// NASA Launch Control - exception chaining

// custom exception with cause
class MissionAbortException extends Exception {
    MissionAbortException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

public class NasaLaunchControl {
    // validates launch conditions
    public static void validateLaunch(int fuel, String weather) throws MissionAbortException {
        try {
            // check fuel level
            if (fuel < 80) {
                throw new Exception("Low fuel: " + fuel + "%");
            }
            // check weather
            if (!weather.equals("CLEAR")) {
                throw new Exception("Bad weather: " + weather);
            }
        } catch (Exception e) {
            throw new MissionAbortException("Launch aborted", e);
        }
    }

    public static void main(String[] args) {
        // test 1: low fuel
        System.out.println("Test 1: Low Fuel");
        try {
            validateLaunch(60, "CLEAR");
            System.out.println("Launch successful!");
        } catch (MissionAbortException e) {
            System.out.println(e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
        }

        // test 2: bad weather
        System.out.println("\nTest 2: Bad Weather");
        try {
            validateLaunch(90, "STORM");
            System.out.println("Launch successful!");
        } catch (MissionAbortException e) {
            System.out.println(e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
        }

        // test 3: all good
        System.out.println("\nTest 3: All Good");
        try {
            validateLaunch(95, "CLEAR");
            System.out.println("Launch successful!");
        } catch (MissionAbortException e) {
            System.out.println(e.getMessage());
        }
    }
}
