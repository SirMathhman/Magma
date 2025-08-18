package magma;

public class Compiler {
	static String compile(String input) {
		// Try to parse the input as an integer first. If that fails,
		// support a simple addition expression like "2 + 3" by splitting
		// on '+' and summing the integer operands. On any parse failure
		// fall back to returning 0.
		int value = 0;
		if (input != null) {
			String trimmed = input.trim();
			try {
				value = Integer.parseInt(trimmed);
			} catch (NumberFormatException e) {
				// try simple addition: one or more integers separated by '+'
				try {
					String[] parts = trimmed.split("\\+");
					int sum = 0;
					for (String p : parts) {
						sum += Integer.parseInt(p.trim());
					}
					value = sum;
				} catch (Exception ignored) {
					// leave value as 0 on invalid input
				}
			}
		}
		return "int main(){return " + value + ";}";
	}
}
