package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Main {
	private static final Map<String, Integer> variables = new HashMap<>();

	static String run(String value) {
		// Handle empty input
		if (value.trim().isEmpty()) {
			return "";
		}

		// Handle code blocks
		String trimmed = value.trim();
		if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
			// Empty block
			if (trimmed.equals("{}")) {
				return "";
			}

			// Extract the content inside the block
			String blockContent = trimmed.substring(1, trimmed.length() - 1).trim();
			return run(blockContent);
		}

		// Check if this is a variable assignment
		if (trimmed.startsWith("let ")) {
			// Handle variable assignment
			String[] parts = trimmed.split(";");
			String assignment = parts[0].substring(4).trim(); // Remove "let " prefix
			String[] assignmentParts = assignment.split("=");
			String varNamePart = assignmentParts[0].trim();
			String varName;

			// Check if there's a type annotation
			if (varNamePart.contains(":")) {
				String[] typeAnnotationParts = varNamePart.split(":");
				varName = typeAnnotationParts[0].trim();
				// Type annotation is ignored for now
			} else {
				varName = varNamePart;
			}

			int varValue = evaluateExpression(assignmentParts[1].trim());
			variables.put(varName, varValue);

			// If there's an expression after the semicolon, evaluate it
			if (parts.length > 1) {
				String expression = parts[1].trim();
				// Check if it's just a variable reference
				if (variables.containsKey(expression)) {
					return String.valueOf(variables.get(expression));
				}
				return String.valueOf(evaluateExpression(expression));
			}
			return String.valueOf(varValue);
		}

		// Check if it's just a variable reference
		if (variables.containsKey(trimmed)) {
			return String.valueOf(variables.get(trimmed));
		}

		// Handle boolean literals
		if (trimmed.equals("true")) {
			return "1";
		}
		if (trimmed.equals("false")) {
			return "0";
		}

		// Remove all spaces from the input for checking if it's a simple number
		String noSpaces = trimmed.replaceAll("\\s+", "");

		// If it's a single number, return as is
		if (!noSpaces.contains("+") && !noSpaces.contains("*") && !noSpaces.contains("/") && !noSpaces.contains("(") &&
				!noSpaces.contains(")") && (noSpaces.indexOf("-") == 0 || !noSpaces.contains("-"))) {
			return value;
		}

		// Evaluate the expression and return the result as a string
		return String.valueOf(evaluateExpression(value));
	}

	private static int evaluateExpression(String expression) {
		Stack<Integer> numbers = new Stack<>();
		Stack<Character> operators = new Stack<>();

		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);

			// Skip spaces
			if (c == ' ') {
				continue;
			}

			// If current character is a digit, parse the full number
			if (Character.isDigit(c) ||
					(c == '-' && (i == 0 || expression.charAt(i - 1) == '(' || expression.charAt(i - 1) == ' ') &&
					 i + 1 < expression.length() && Character.isDigit(expression.charAt(i + 1)))) {
				StringBuilder numBuilder = new StringBuilder();

				// Handle negative numbers
				if (c == '-') {
					numBuilder.append(c);
					i++;
				}

				// Parse the number
				while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
					numBuilder.append(expression.charAt(i++));
				}
				i--; // Move back one position as the for loop will increment

				numbers.push(Integer.parseInt(numBuilder.toString()));
			}
			// If current character is an opening parenthesis, push it to operators stack
			else if (c == '(') {
				operators.push(c);
			}
			// If current character is a closing parenthesis, solve the parenthesis
			else if (c == ')') {
				while (operators.peek() != '(') {
					numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
				}
				operators.pop(); // Remove the opening parenthesis
			}
			// If current character is an operator
			else if (c == '+' || c == '-' || c == '*' || c == '/') {
				// While top of 'operators' has same or greater precedence to current
				// operator, apply operator on top of 'operators' to top two elements
				// in numbers stack
				while (!operators.empty() && hasPrecedence(c, operators.peek())) {
					numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
				}
				// Push current operator to 'operators'
				operators.push(c);
			}
		}

		// Apply remaining operators to remaining numbers
		while (!operators.empty()) {
			numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
		}

		// The final result is at the top of the 'numbers' stack
		return numbers.pop();
	}

	private static boolean hasPrecedence(char op1, char op2) {
		if (op2 == '(' || op2 == ')') {
			return false;
		}
		return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
	}

	private static int applyOperation(char operator, int b, int a) {
		switch (operator) {
			case '+':
				return a + b;
			case '-':
				return a - b;
			case '*':
				return a * b;
			case '/':
				return a / b;
		}
		return 0;
	}
}
