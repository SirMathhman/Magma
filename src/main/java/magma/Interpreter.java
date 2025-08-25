package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Interpreter {
	// parse a numeric literal (with optional +/- sign and optional suffix) into Num
	private static Option<Num> parseNumericLiteral(String s) {
		if (s == null) return new None<>();
		String t = s.trim();
		if (t.isEmpty()) return new None<>();
		try {
			return new Some<>(new Num(Integer.parseInt(t), ""));
		} catch (NumberFormatException ex) {
		}
		int len = t.length();
		int idx = 0;
		char c = t.charAt(idx);
		if (c == '+' || c == '-') idx++;
		int ds = idx;
		while (idx < len && Character.isDigit(t.charAt(idx))) idx++;
		if (idx <= ds) return new None<>();
		String pref = t.substring(0, idx);
		String suf = t.substring(idx);
		var allowed = Set.of("U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64");
		if (suf.isEmpty()) {
			try {
				return new Some<>(new Num(Integer.parseInt(pref), ""));
			} catch (NumberFormatException e) {
				return new None<>();
			}
		}
		if (!allowed.contains(suf)) return new None<>();
		if (pref.startsWith("-") && suf.startsWith("U")) return new None<>();
		try {
			return new Some<>(new Num(Integer.parseInt(pref), suf));
		} catch (NumberFormatException e) {
			return new None<>();
		}
	}

	// Assume that input can never be null.
	public static Result<String, InterpretError> interpret(String input) {
		Option<String> in = ((Option<String>) new Some<>(input)).map(String::trim);

		// handle either a binary operation or a plain/suffixed input
		Option<String> finalOpt = in.flatMap(trimmed -> {
			// support multiple let-bindings separated by ';', e.g.
			// "let x : I32 = 0; let y : I32 = 40; x"
			String[] parts = trimmed.split(";");
			Map<String, Num> env = new HashMap<>();

			// helper to parse a numeric/result string into Num (prefix and suffix)

			// parseToken: parse a token either as a numeric literal or a previously bound
			// variable
			Function<String, Option<Num>> parseToken = token -> {
				if (token == null || token.isEmpty()) return new None<>();
				String t = token.trim();
				// variable lookup
				if (env.containsKey(t)) return new Some<>(env.get(t));
				Option<Num> pn = parseNumericLiteral(t);
				if (pn.isSome()) return pn;
				return new None<>();
			};

			// helper to evaluate a single expression using parseToken and env
			Function<String, Option<String>> evalExpr = expr -> {
				if (expr == null) return new None<>();
				String e = expr.trim();
				if (e.isEmpty()) return new None<>();
				// detect binary operator (skip unary sign)
				int opIdx = -1;
				char op = 0;
				for (int i = 0; i < e.length(); i++) {
					char ch = e.charAt(i);
					if ((ch == '+' || ch == '-') && i == 0) continue;
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
					if (L.isNone() || R.isNone()) return new None<>();
					Num lnum = L.get();
					Num rnum = R.get();

					String resSuffix = "";
					if (!lnum.suffix.isEmpty() && !rnum.suffix.isEmpty()) {
						if (!lnum.suffix.equals(rnum.suffix)) return new None<>();
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
							if (rnum.value == 0) return new None<>();
							result = (long) lnum.value / (long) rnum.value;
							break;
						case '%':
							if (rnum.value == 0) return new None<>();
							result = (long) lnum.value % (long) rnum.value;
							break;
						default:
							return new None<>();
					}
					int resultInt = (int) result;
					return new Some<>(resultInt + resSuffix);
				}

				// single token: could be a variable or a numeric literal
				// variable lookup (return numeric prefix only)
				if (env.containsKey(e)) {
					Num n = env.get(e);
					return new Some<>(String.valueOf(n.value));
				}

				// numeric literal direct (strip suffix for direct input)
				Option<Num> pn = parseNumericLiteral(e);
				if (pn.isSome()) {
					Num n = pn.get();
					return new Some<>(String.valueOf(n.value));
				}
				return new None<>();
			};

			// process sequential parts: each part may be a let decl or the final expr
			for (String s : parts) {
				String part = s.trim();
				if (part.isEmpty()) continue;
				if (part.startsWith("let ")) {
					String content = part.substring(4).trim();
					int eq = content.indexOf('=');
					if (eq < 0) return new None<>();
					String left = content.substring(0, eq).trim();
					String rhs = content.substring(eq + 1).trim();
					String name;
					int colon = left.indexOf(':');
					if (colon >= 0) name = left.substring(0, colon).trim();
					else name = left;
					if (name.isEmpty()) return new None<>();
					Option<String> r = evalExpr.apply(rhs);
					if (r.isNone()) return new None<>();
					String s1 = r.get();
					Option<Num> n = parseNumericLiteral(s1);
					if (n.isNone()) return new None<>();
					env.put(name, n.get());
					// continue to next part
				} else {
					// final expression: evaluate with env available
					return evalExpr.apply(part);
				}
			}
			// if we reach here there was no final standalone expression
			return new None<>();
		});

		if (finalOpt.isSome()) return new Ok<>(finalOpt.get());
		return new Err<>(new InterpretError("Invalid numeric literal", input));
	}
}
