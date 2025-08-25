package magma;

import magma.option.Option;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

public class Interpreter {
	// parse a numeric literal (with optional +/- sign and optional suffix) into Num
	private static Option<Num> parseNumericLiteral(String s) {
		if (s == null)
			return Option.none();
		String t = s.trim();
		if (t.isEmpty())
			return Option.none();
		try {
			return Option.some(new Num(Integer.parseInt(t), ""));
		} catch (NumberFormatException ex) {
		}
		int len = t.length();
		int idx = 0;
		char c = t.charAt(idx);
		if (c == '+' || c == '-')
			idx++;
		int ds = idx;
		while (idx < len && Character.isDigit(t.charAt(idx)))
			idx++;
		if (idx <= ds)
			return Option.none();
		String pref = t.substring(0, idx);
		String suf = t.substring(idx);
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
	}

	public static Result<String, InterpretError> interpret(String input) {
		// avoid nulls: wrap the input in Option
		Option<String> in = Option.ofNullable(input).map(String::trim);

		// handle either a binary operation or a plain/suffixed input
		Option<String> finalOpt = in.flatMap(trimmed -> {
			// support multiple let-bindings separated by ';', e.g.
			// "let x : I32 = 0; let y : I32 = 40; x"
			String[] parts = trimmed.split(";");
			Map<String, Num> env = new HashMap<>();

			// helper to parse a numeric/result string into Num (prefix and suffix)
			Function<String, Option<Num>> stringToNum = str -> parseNumericLiteral(str);

			// parseToken: parse a token either as a numeric literal or a previously bound
			// variable
			Function<String, Option<Num>> parseToken = token -> {
				if (token == null || token.isEmpty())
					return Option.none();
				String t = token.trim();
				// variable lookup
				if (env.containsKey(t))
					return Option.some(env.get(t));
				Option<Num> pn = parseNumericLiteral(t);
				if (pn.isSome())
					return pn;
				return Option.none();
			};

			// helper to evaluate a single expression using parseToken and env
			Function<String, Option<String>> evalExpr = expr -> {
				if (expr == null)
					return Option.none();
				String e = expr.trim();
				if (e.isEmpty())
					return Option.none();
				// detect binary operator (skip unary sign)
				int opIdx = -1;
				char op = 0;
				for (int i = 0; i < e.length(); i++) {
					char ch = e.charAt(i);
					if ((ch == '+' || ch == '-') && i == 0)
						continue;
					if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
						opIdx = i;
						op = ch;
						break;
					}
				}
				if (opIdx > 0 && opIdx < e.length() - 1) {
					String left = e.substring(0, opIdx).trim();
					String right = e.substring(opIdx + 1).trim();
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
					return Option.some(String.valueOf(resultInt) + resSuffix);
				}

				// single token: could be a variable or a numeric literal
				// variable lookup (return numeric prefix only)
				if (env.containsKey(e)) {
					Num n = env.get(e);
					return Option.some(String.valueOf(n.value));
				}

				// numeric literal direct (strip suffix for direct input)
				Option<Num> pn = parseNumericLiteral(e);
				if (pn.isSome()) {
					Num n = pn.get();
					return Option.some(String.valueOf(n.value));
				}
				return Option.none();
			};

			// process sequential parts: each part may be a let decl or the final expr
			for (int pi = 0; pi < parts.length; pi++) {
				String part = parts[pi].trim();
				if (part.isEmpty())
					continue;
				if (part.startsWith("let ")) {
					String content = part.substring(4).trim();
					int eq = content.indexOf('=');
					if (eq < 0)
						return Option.none();
					String left = content.substring(0, eq).trim();
					String rhs = content.substring(eq + 1).trim();
					String name;
					int colon = left.indexOf(':');
					if (colon >= 0)
						name = left.substring(0, colon).trim();
					else
						name = left;
					if (name.isEmpty())
						return Option.none();
					Option<String> r = evalExpr.apply(rhs);
					if (r.isNone())
						return Option.none();
					Option<Num> n = stringToNum.apply(r.get());
					if (n.isNone())
						return Option.none();
					env.put(name, n.get());
					// continue to next part
				} else {
					// final expression: evaluate with env available
					return evalExpr.apply(part);
				}
			}
			// if we reach here there was no final standalone expression
			return Option.none();
		});

		if (finalOpt.isSome())
			return new Ok<>(finalOpt.get());
		return new Err<>(new InterpretError("Invalid numeric literal", input));
	}
}
