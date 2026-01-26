
/*
**Problem 3:**
You are given a list of bank transactions in the format:
`TransactionId:AccountType:Amount`

Example account types: SAVINGS, CURRENT

Using Java Streams, perform the following:
1) Extract the transaction amount
2) Consider only SAVINGS account transactions
3) Filter transactions with amount ≥ 10,000
4) Convert the amount to GST-included amount (amount + 18%)
5) Remove duplicate final amounts
6) Sort the amounts in descending order
7) Count how many distinct GST-included amounts are greater than 20,000

SAMPLE INPUT:
`List<String> transactions = List.of(
    "TXN1001:SAVINGS:12000",
    "TXN1002:CURRENT:15000",
    "TXN1003:SAVINGS:20000",
    "TXN1004:SAVINGS:12000",
    "TXN1005:SAVINGS:8000",
    "TXN1006:CURRENT:30000"
);`

SAMPLE OUTPUT:
`Processed Amounts: [23600, 14160]
Count of amounts > 20000: 1
`
*/

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class task3 {
    public static void main(String args[]){
        List<String> transactions = List.of(
    "TXN1001:SAVINGS:12000",
    "TXN1002:CURRENT:15000",
    "TXN1003:SAVINGS:20000",
    "TXN1004:SAVINGS:12000",
    "TXN1005:SAVINGS:8000",
    "TXN1006:CURRENT:30000"
);
  List<Integer> res = transactions.stream()
             .map(n->n.split(":"))
             .filter(n -> n[1].equals("SAVINGS"))
             .map(n->n[2])
             .map(Integer::parseInt)
             .filter(n -> n >= 10000)
             .map(amt -> (int) Math.round(amt + amt*0.18))
             .distinct()
             .sorted(Comparator.reverseOrder())
             .collect(Collectors.toList());

     System.out.println("Processed Amounts: " + res);

     long count = res.stream().filter(n -> n > 20000).count();
     System.out.println("Count of amounts > 20000: " + count);

 }

    }

