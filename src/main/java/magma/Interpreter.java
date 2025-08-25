package magma;

import magma.option.Option;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.Set;
import java.util.function.Function;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		// avoid nulls: wrap the input in Option
		Option<String> in = Option.ofNullable(input).map(String::trim);

		// handle either a binary operation or a plain/suffixed input
		Option<String> finalOpt = in.flatMap(trimmed -> {
			// detect a binary operator (skip unary sign at start)
			int opIdx = -1;
			char op = 0;
			for (int i = 0; i < trimmed.length(); i++) {
				char ch = trimmed.charAt(i);
				if ((ch == '+' || ch == '-') && i == 0)
					continue;
				if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
					opIdx = i;
					op = ch;
					break;
				}
			}

			Function<String, Option<Num>> parseToken = token -> {
				if (token == null || token.isEmpty())
					return Option.none();
				try {
					return Option.some(new Num(Integer.parseInt(token), ""));
				} catch (NumberFormatException ex) {
				}
				int tlen = token.length();
				int i = 0;
				char c = token.charAt(i);
				if (c == '+' || c == '-')
					i++;
				int ds = i;
				while (i < tlen && Character.isDigit(token.charAt(i)))
					i++;
				if (i <= ds)
					return Option.none();
				String pref = token.substring(0, i);
				String suf = token.substring(i);
				var allowed = Set.of("U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64");
				if (suf.isEmpty()) {
					try {
						return Option.some(new Num(Integer.parseInt(pref), ""));
					} catch (NumberFormatException e) {
						return Option.none();
					}
				}
				if (!allowed.contains(suf))
					return Option.none();
				if (pref.startsWith("-") && suf.startsWith("U"))
					return Option.none();
				try {
					return Option.some(new Num(Integer.parseInt(pref), suf));
				} catch (NumberFormatException e) {
					return Option.none();
				}
			};

			if (opIdx > 0 && opIdx < trimmed.length() - 1) {
				// binary expression
				String left = trimmed.substring(0, opIdx).trim();
				String right = trimmed.substring(opIdx + 1).trim();
				if (left.isEmpty() || right.isEmpty())
					return Option.none();
				Option<Num> L = parseToken.apply(left);
				Option<Num> R = parseToken.apply(right);
				if (L.isNone() || R.isNone())
					return Option.none();
				Num lnum = L.get();
				Num rnum = R.get();

				String resSuffix = "";
				if (!lnum.suffix.isEmpty() && !rnum.suffix.isEmpty()) {
					if (!lnum.suffix.equals(rnum.suffix))
						return Option.none();
					resSuffix = lnum.suffix;
				} else if (!lnum.suffix.isEmpty() || !rnum.suffix.isEmpty()) {
					// mixing with plain yields a plain result
					resSuffix = "";
				}

				long result;
				switch (op) {
					case '+':
						result = (long) lnum.value + (long) rnum.value;
						break;
					case '-':
						result = (long) lnum.value - (long) rnum.value;
						break;
					case '*':
						result = (long) lnum.value * (long) rnum.value;
						break;
					case '/':
						if (rnum.value == 0)
							return Option.none();
						result = (long) lnum.value / (long) rnum.value;
						break;
					case '%':
						if (rnum.value == 0)
							return Option.none();
						result = (long) lnum.value % (long) rnum.value;
						break;
					default:
						return Option.none();
				}
				int resultInt = (int) result;
				// for computed results keep suffix if both sides were suffixed
				return Option.some(String.valueOf(resultInt) + resSuffix);
			}

			// no binary operator: validate plain or suffixed input (direct user input)
			String s = trimmed;
			try {
				Integer.parseInt(s);
				return Option.some(s);
			} catch (NumberFormatException e) {
				if (s.isEmpty())
					return Option.none();
				int len = s.length();
				int idx = 0;
				char c = s.charAt(idx);
				if (c == '+' || c == '-')
					idx++;
				int digitsStart = idx;
				while (idx < len && Character.isDigit(s.charAt(idx)))
					idx++;
				if (idx > digitsStart) {
					String prefix = s.substring(0, idx);
					String suffix = s.substring(idx);
					var allowed = Set.of("U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64");
					if (suffix.isEmpty())
						return Option.some(prefix);
					if (allowed.contains(suffix)) {
						if (prefix.startsWith("-") && suffix.startsWith("U"))
							return Option.none();
						// direct input with suffix -> return numeric prefix only
						return Option.some(prefix);
					} else
						return Option.none();
				}
				return Option.none();
			}
		});

		if (finalOpt.isSome())
			return new Ok<>(finalOpt.get());
		return new Err<>(new InterpretError("Invalid numeric literal", input));
	}
}
