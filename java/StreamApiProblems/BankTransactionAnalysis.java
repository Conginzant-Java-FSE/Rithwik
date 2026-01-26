// Stream API - Bank Transaction Analysis

import java.util.*;
import java.util.stream.Collectors;

// Analyzes bank transactions and GST
public class BankTransactionAnalysis {
    public static void main(String[] args) {
        // ID : Type : Amount
        List<String> txns = Arrays.asList(
                "T1:SAVINGS:12000",
                "T2:CURRENT:15000",
                "T3:SAVINGS:20000",
                "T4:SAVINGS:12000",
                "T5:SAVINGS:8000",
                "T6:CURRENT:30000",
                "T7:SAVINGS:25000");

        System.out.println("Transactions: " + txns);

        List<Integer> finalAmounts = txns.stream()
                // parse transaction data
                .map(t -> t.split(":"))
                // filter Savings accounts only
                .filter(parts -> parts[1].equals("SAVINGS"))
                // extract amount
                .map(parts -> Integer.parseInt(parts[2]))
                // filter large transactions (>= 10000)
                .filter(amt -> amt >= 10000)
                // add 18% GST
                .map(amt -> (int) (amt * 1.18))
                // remove duplicates
                .distinct()
                // sort descending
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        System.out.println("Final Amounts (with GST): " + finalAmounts);

        // count high value transactions
        long count = finalAmounts.stream()
                .filter(amt -> amt > 20000)
                .count();

        System.out.println("High Value Txns (> 20k): " + count);
    }
}
