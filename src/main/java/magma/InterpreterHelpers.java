package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class InterpreterHelpers {
	static int findClosingParenAfterOpen(CharSequence program, int openPos) {
		if (0 > openPos || openPos >= program.length() || '(' != program.charAt(openPos)) {
			return -1;
		}
		var depth = 1;
		var i = openPos + 1;
		while (i < program.length()) {
			var ch = program.charAt(i);
			if ('(' == ch) {
				depth++;
			} else if (')' == ch) {
				depth--;
				if (0 == depth) {
					return i + 1; // position after ')'
				}
			}
			i++;
		}
		return -1;
	}

	static int skipWhitespace(CharSequence program, int i) {
		var i1 = i;
		while (i1 < program.length() && Character.isWhitespace(program.charAt(i1))) {
			i1++;
		}
		return i1;
	}

	static Optional<String> tryExtractFnLiteralAt(String program, int posAfterArrow) {
		var litStart = InterpreterHelpers.skipWhitespace(program, posAfterArrow);
		var litEnd = program.indexOf(';', litStart);
		Optional<String> result = Optional.empty();
		if (litEnd > litStart) {
			var literal = program.substring(litStart, litEnd).trim();
			result = Optional.of(literal);
		}
		return result;
	}

	private static Optional<String> extractArgBetweenParentheses(String program, String callName, String trailingSuffix) {
		var callIndex = program.lastIndexOf(callName + "(");
		if (-1 == callIndex) {
			return Optional.empty();
		}
		var argStart = callIndex + (callName + "(").length();
		// find the next ')' using a simple scan to avoid indexOf token duplication
		var argEnd = program.indexOf(')', argStart);
		if (argEnd <= argStart) {
			return Optional.empty();
		}
		if (!trailingSuffix.isEmpty()) {
			var suffixIndex = program.indexOf(trailingSuffix, argEnd + 1);
			if (suffixIndex != argEnd + 1) {
				return Optional.empty();
			}
		}
		var argument = program.substring(argStart, argEnd).trim();
		return Optional.of(argument);
	}

	private static Optional<String> quotedArgumentIf(String arg) {
		if (2 <= arg.length() && '"' == arg.charAt(0) && '"' == arg.charAt(arg.length() - 1)) {
			return Optional.of(arg);
		}
		return Optional.empty();
	}

	static Optional<String> asciiOfSingleQuotedLiteral(CharSequence s) {
		if (InterpreterHelpers.isABoolean(s)) {
			int ascii = s.charAt(1);
			return Optional.of(String.valueOf(ascii));
		}
		return Optional.empty();
	}

	private static boolean isABoolean(CharSequence s) {
		return !Objects.isNull(s) && 3 <= s.length() && '\'' == s.charAt(0) && '\'' == s.charAt(s.length() - 1);
	}

	static boolean isQuotedOrDigits(CharSequence s) {
		if (Objects.isNull(s) || s.isEmpty()) {
			return false;
		}
		if ('"' == s.charAt(0) && '"' == s.charAt(s.length() - 1)) {
			return true;
		}
		return s.chars().allMatch(Character::isDigit);
	}

	static List<String> splitOnAnd(String s) {
		List<String> parts = new ArrayList<>();
		var i = 0;
		var op = s.indexOf("&&", i);
		while (-1 != op) {
			parts.add(s.substring(i, op).trim());
			i = op + 2;
			op = s.indexOf("&&", i);
		}
		if (i <= s.length()) {
			parts.add(s.substring(i).trim());
		}
		return parts;
	}

	// Extract the first comma-separated argument between parentheses for a call
	// and return it only if it's a quoted argument. This supports call sites like
	// pass("a", 1) where we want the first quoted argument.
	static Optional<String> extractQuotedArgForCall(String program, String callName, String trailingSuffix) {
		return rawArgString(program, callName, trailingSuffix).map(arg -> {
			// find first comma at top level (ignore commas inside quotes)
			var inQuote = false;
			var commaPos = -1;
			for (var i = 0; i < arg.length(); i++) {
				var ch = arg.charAt(i);
				if ('"' == ch) {
					inQuote = !inQuote;
				} else if (!inQuote && ',' == ch) {
					commaPos = i;
					// avoid 'break' – set i to length so the loop exits normally
					i = arg.length();
				}
			}
			var first = commaPos == -1 ? arg : arg.substring(0, commaPos);
			return first.trim();
		}).flatMap(InterpreterHelpers::quotedArgumentIf);
	}

	private static Optional<String> rawArgString(String program, String callName, String trailingSuffix) {
		var raw = InterpreterHelpers.extractArgBetweenParentheses(program, callName, trailingSuffix);
		if (raw.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(raw.get());
	}

	static Optional<String> extractSingleArgForCall(String program) {
		return InterpreterHelpers.extractArgBetweenParentheses(program, "pass", "").flatMap(InterpreterHelpers::getString);
	}

	private static Optional<String> getString(String arg) {
		if (arg.isEmpty())
			return Optional.empty();
		if (arg.chars().allMatch(Character::isDigit))
			return Optional.of(arg);
		if ("true".equals(arg) || "false".equals(arg))
			return Optional.of(arg);
		return InterpreterHelpers.quotedArgumentIf(arg).or(() -> InterpreterHelpers.asciiOfSingleQuotedLiteral(arg));
	}

	static Optional<String> evaluateNumericComparison(String trimmed, String opToken, int opLen) {
		if (trimmed.contains(opToken)) {
			var op = trimmed.indexOf(opToken);
			if (0 < op) {
				var leftS = trimmed.substring(0, op).trim();
				var rightS = trimmed.substring(op + opLen).trim();
				if (InterpreterHelpers.isABoolean(leftS, rightS)) {
					var left = Integer.parseInt(leftS);
					var right = Integer.parseInt(rightS);
					if (">=".equals(opToken)) {
						return Optional.of(left >= right ? "true" : "false");
					} else if (">".equals(opToken)) {
						return Optional.of(left > right ? "true" : "false");
					}
				}
			}
		}
		return Optional.empty();
	}

	private static boolean isABoolean(CharSequence leftS, CharSequence rightS) {
		return !leftS.isEmpty() && !rightS.isEmpty() && leftS.chars().allMatch(Character::isDigit) &&
				rightS.chars().allMatch(Character::isDigit);
	}

	// no-op placeholder removed to avoid duplication; logic resides in Interpreter

	static boolean expectOpenParen(CharSequence program, int pos) {
		if (pos >= program.length()) {
			return false;
		}
		return '(' == program.charAt(pos);
	}

	static boolean isTopLevelNoIfElse(String trimmed) {
		return !trimmed.contains("if ") && !trimmed.contains("else");
	}
}
