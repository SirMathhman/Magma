package magma;

public class Compiler {
	static String compile(String input) {
		// try to parse the input as an integer and generate a C program
		// that returns that integer from main. If parsing fails, return 0.
		int value = 0;
		if (input != null) {
			try {
				value = Integer.parseInt(input.trim());
			} catch (NumberFormatException ignored) {
				// leave value as 0 on invalid input
			}
		}
		return "int main(){return " + value + ";}";
	}
}
