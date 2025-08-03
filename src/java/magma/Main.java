package magma;

public class Main {
	static String run(String value) {
		// Check if the value contains spaces, which indicates an operation
		if (value.contains(" ")) {
			String[] tokens = value.split(" ");

			// Handle single number case
			if (tokens.length == 1) {
				return value;
			}

			// Process the first number
			int result = Integer.parseInt(tokens[0]);

			// Process the operations sequentially
			for (int i = 1; i < tokens.length; i += 2) {
				if (i + 1 < tokens.length) {
					String operator = tokens[i];
					int num2 = Integer.parseInt(tokens[i + 1]);

					switch (operator) {
						case "+":
							result += num2;
							break;
						case "-":
							result -= num2;
							break;
						case "*":
							result *= num2;
							break;
					}
				}
			}

			return String.valueOf(result);
		}
		// If it's a single number or not a recognized operation format, return as is
		return value;
	}
}
