import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class task1 {
	public static void main(String[] args) {
		List<String> employees = List.of(
			"Alice:60000",
			"Bob:45000",
			"Charlie:75000",
			"David:60000",
			"Eva:90000"
		);

		var annualSalaries = employees.stream()
			.map(s -> s.split(":")[1])
			.map(Integer::parseInt)
			.filter(sal -> sal > 50000)
			.map(sal -> sal * 12)            
			.distinct()
			.sorted(Comparator.reverseOrder())
			.skip(1)
			.limit(2)
			.collect(Collectors.toList());

		System.out.println("Annual Salaries After Processing: " + annualSalaries);
		System.out.println("Count: " + annualSalaries.size());
	}
}
