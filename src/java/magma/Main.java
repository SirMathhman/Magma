package magma;

public class Main {
	static String run(String value) {
		// Check if the value contains spaces, which indicates an operation
		if (value.contains(" ")) {
			String[] tokens = value.split(" ");
			if (tokens.length == 3) {
				int num1 = Integer.parseInt(tokens[0]);
				String operator = tokens[1];
				int num2 = Integer.parseInt(tokens[2]);
				
				switch (operator) {
					case "+":
						return String.valueOf(num1 + num2);
					case "-":
						return String.valueOf(num1 - num2);
					case "*":
						return String.valueOf(num1 * num2);
				}
			}
		}
		// If it's a single number or not a recognized operation format, return as is
		return value;
	}
}
