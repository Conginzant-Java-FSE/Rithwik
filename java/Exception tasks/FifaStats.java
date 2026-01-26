// FIFA Stats - custom exception handling

// custom exception for invalid score
class InvalidScoreException extends RuntimeException {
    InvalidScoreException(String msg) {
        super(msg);
    }
}

public class FifaStats {
    // calculates goal difference
    public static int getGoalDifference(int scored, int conceded) {
        if (scored < 0 || conceded < 0) {
            throw new InvalidScoreException("Score cannot be negative");
        }
        return scored - conceded;
    }

    public static void main(String[] args) {
        // test with valid scores
        try {
            int diff = getGoalDifference(4, 2);
            System.out.println("Goal Difference: " + diff);
        } catch (InvalidScoreException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Match 1 processed");
        }

        // test with invalid score
        try {
            int diff = getGoalDifference(2, -1);
            System.out.println("Goal Difference: " + diff);
        } catch (InvalidScoreException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Match 2 processed");
        }
    }
}
