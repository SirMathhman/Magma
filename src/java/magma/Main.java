package magma;

public class Main {
	static String run(String value) {
		if (value.contains("+")) {
			String[] parts = value.split("\\+");
			int num1 = Integer.parseInt(parts[0].trim());
			int num2 = Integer.parseInt(parts[1].trim());
			return String.valueOf(num1 + num2);
		}
		return value;
	}
}
