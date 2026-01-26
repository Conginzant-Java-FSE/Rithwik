package Exception_Tasks;

class InvalidScoreException extends RuntimeException {
    public InvalidScoreException(String message) {
        super(message);
    }
}

public class task2 {
    // private static final org.slf4j.Logger log =
    // org.slf4j.LoggerFactory.getLogger(FifaStats.class);

    // TODO: Create InvalidScoreException class (RuntimeException)

    public static int calculateGoalDifference(int scored, int conceded) {
        // TODO: If any < 0 throw InvalidScoreException
        if (scored < 0 || conceded < 0) {
            throw new InvalidScoreException("Invalid Score");
        }
        int difference = scored - conceded;
        return difference; // TODO: compute correct difference
    }

    public static void main(String[] args) {
        try {
            int diff = calculateGoalDifference(3, -1);
            // TODO: Log goal difference
            System.out.println(diff);
        } catch (InvalidScoreException e) {
            // TODO: Log invalid score case
            System.out.println(e.getMessage());
        } catch (Exception e) {
            // TODO: log generic error
            System.out.println("Unexpected Error: generic Error");

        } finally {
            // TODO: Log "Match stats processed"
            System.out.println("Match stats processed");
        }
    }
}
