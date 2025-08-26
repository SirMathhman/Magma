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
	private static final java.util.Set<String> ALLOWED_SUFFIXES = Set.of("U8", "U16", "U32", "U64", "I8", "I16", "I32",
			"I64");

	// parse a numeric literal (with optional +/- sign and optional suffix) into Num
	private static Option<Num> parseNumericLiteral(String s) {
		if (s == null)
			return new None<>();
		String t = s.trim();
		if (t.isEmpty())
			return new None<>();
		try {
			return new Some<>(new Num(Integer.parseInt(t), ""));
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
			return new None<>();
		String pref = t.substring(0, idx);
		String suf = t.substring(idx);
		var allowed = ALLOWED_SUFFIXES;
		if (suf.isEmpty()) {
			try {
				return new Some<>(new Num(Integer.parseInt(pref), ""));
			} catch (NumberFormatException e) {
				return new None<>();
			}
		}
		if (!allowed.contains(suf))
			return new None<>();
		if (pref.startsWith("-") && suf.startsWith("U"))
			return new None<>();
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
				if (token == null || token.isEmpty())
					return new None<>();
				String t = token.trim();
				// variable lookup
				if (env.containsKey(t))
					return new Some<>(env.get(t));
				Option<Num> pn = parseNumericLiteral(t);
				if (pn.isSome())
					return pn;
				return new None<>();
			};

			// helper to evaluate a single expression using parseToken and env
			Function<String, Option<String>> evalExpr = new Function<>() {
				@Override
				public Option<String> apply(String expr) {
					if (expr == null)
						return new None<>();
					String e = expr.trim();
					if (e.isEmpty())
						return new None<>();

					// evaluate parentheses first (innermost first)
					while (e.contains("(")) {
						int open = e.lastIndexOf('(');
						int close = e.indexOf(')', open);
						if (open < 0 || close < 0)
							return new None<>();
						Option<String> inner = this.apply(e.substring(open + 1, close));
						if (inner.isNone())
							return new None<>();
						if (!(inner instanceof Some<?> innerSome))
							return new None<>();
						String innerVal = (String) innerSome.value();
						e = e.substring(0, open) + innerVal + e.substring(close + 1);
					}

					// boolean operators: || (lowest) and && (next). Use a small helper
					// to avoid duplicating validation/iteration logic (CPD).
					final String e2 = e;
					java.util.function.BiFunction<String, Boolean, Option<String>> evalBoolOp = (opRegex, isOr) -> {
						String[] parts = e2.split(opRegex);
						boolean acc = isOr ? false : true;
						for (String p : parts) {
							Option<String> pr = this.apply(p);
							if (pr.isNone())
								return new None<>();
							if (!(pr instanceof Some<?> prSome))
								return new None<>();
							String v = ((String) prSome.value()).trim();
							if (!(v.equals("true") || v.equals("false")))
								return new None<>();
							if (isOr && v.equals("true"))
								acc = true;
							if (!isOr && v.equals("false"))
								acc = false;
						}
						return new Some<>(acc ? "true" : "false");
					};

					if (e2.contains("||"))
						return evalBoolOp.apply("\\|\\|", true);
					if (e2.contains("&&"))
						return evalBoolOp.apply("&&", false);

					// arithmetic: handle * / % first within terms, then + -
					// evaluate a term (factors separated by * / %)
					Function<String, Option<Num>> evalTerm = term -> {
						String t = term.trim();
						if (t.isEmpty())
							return new None<>();
						int idx = 0;
						long acc = 0;
						String accSuffix = "";
						boolean first = true;
						char pendingOp = 0;
						while (idx < t.length()) {
							int nextOp = -1;
							char op = 0;
							for (int i = idx; i < t.length(); i++) {
								char ch = t.charAt(i);
								if (ch == '*' || ch == '/' || ch == '%') {
									nextOp = i;
									op = ch;
									break;
								}
							}
							String factor = nextOp >= 0 ? t.substring(idx, nextOp).trim() : t.substring(idx).trim();
							Option<Num> fn = parseToken.apply(factor);
							if (fn.isNone())
								return new None<>();
							if (!(fn instanceof Some<?> fnSome))
								return new None<>();
							Num f = (Num) fnSome.value();
							if (first) {
								acc = f.value;
								accSuffix = f.suffix;
								first = false;
							} else {
								if (!accSuffix.isEmpty() && !f.suffix.isEmpty() && !accSuffix.equals(f.suffix))
									return new None<>();
								String resSuffix = "";
								if (!accSuffix.isEmpty() && !f.suffix.isEmpty() && accSuffix.equals(f.suffix))
									resSuffix = accSuffix;
								switch (pendingOp) {
									case '*':
										acc = acc * f.value;
										break;
									case '/':
										if (f.value == 0)
											return new None<>();
										acc = acc / f.value;
										break;
									case '%':
										if (f.value == 0)
											return new None<>();
										acc = acc % f.value;
										break;
									default:
										return new None<>();
								}
								accSuffix = resSuffix;
							}
							if (nextOp < 0)
								break;
							pendingOp = op;
							idx = nextOp + 1;
						}
						try {
							return new Some<>(new Num((int) acc, accSuffix));
						} catch (NumberFormatException ex) {
							return new None<>();
						}
					};

					// split by + and - (top-level; unary sign at start allowed)
					java.util.List<String> terms = new java.util.ArrayList<>();
					java.util.List<Character> ops = new java.util.ArrayList<>();
					int last = 0;
					for (int i = 0; i < e.length(); i++) {
						char ch = e.charAt(i);
						if ((ch == '+' || ch == '-') && i == 0)
							continue; // unary
						if (ch == '+' || ch == '-') {
							terms.add(e.substring(last, i).trim());
							ops.add(ch);
							last = i + 1;
						}
					}
					terms.add(e.substring(last).trim());

					if (ops.isEmpty()) {
						// only treat as a whole term when it contains multiplicative
						// operators; single-token expressions (like a lone boolean or
						// numeric literal with suffix) should fall through to the
						// single-token fallback below so we don't accidentally append
						// suffixes where tests expect the plain value.
						if (e.contains("*") || e.contains("/") || e.contains("%")) {
							Option<Num> whole = evalTerm.apply(e);
							if (whole.isNone())
								return new None<>();
							if (!(whole instanceof Some<?> wholeSome))
								return new None<>();
							Num wn = (Num) wholeSome.value();
							// when the term actually performed multiplicative ops we
							// preserve suffixes (if any) like before
							return new Some<>(String.valueOf(wn.value) + wn.suffix);
						}
					}

					if (!ops.isEmpty()) {
						Option<Num> leftNum = evalTerm.apply(terms.get(0));
						if (leftNum.isNone())
							return new None<>();
						if (!(leftNum instanceof Some<?> leftSome))
							return new None<>();
						Num leftVal = (Num) leftSome.value();
						long acc = leftVal.value;
						String accSuffix = leftVal.suffix;
						for (int k = 0; k < ops.size(); k++) {
							char op = ops.get(k);
							Option<Num> rightNum = evalTerm.apply(terms.get(k + 1));
							if (rightNum.isNone())
								return new None<>();
							if (!(rightNum instanceof Some<?> rightSome))
								return new None<>();
							Num r = (Num) rightSome.value();
							if (!accSuffix.isEmpty() && !r.suffix.isEmpty() && !accSuffix.equals(r.suffix))
								return new None<>();
							String resSuffix = "";
							if (!accSuffix.isEmpty() && !r.suffix.isEmpty() && accSuffix.equals(r.suffix))
								resSuffix = accSuffix;
							switch (op) {
								case '+':
									acc = acc + r.value;
									break;
								case '-':
									acc = acc - r.value;
									break;
								default:
									return new None<>();
							}
							accSuffix = resSuffix;
						}
						return new Some<>(String.valueOf((int) acc) + accSuffix);
					}

					// single token fallback: boolean, variable, or numeric literal
					if (e.equals("true") || e.equals("false"))
						return new Some<>(e);
					if (env.containsKey(e)) {
						Num n = env.get(e);
						return new Some<>(String.valueOf(n.value));
					}
					Option<Num> pn = parseNumericLiteral(e);
					if (pn.isSome()) {
						if (!(pn instanceof Some<?> pnSome2))
							return new None<>();
						Num n = (Num) pnSome2.value();
						return new Some<>(String.valueOf(n.value));
					}
					return new None<>();
				}
			};

			// process sequential parts: each part may be a let decl or the final expr
			for (String s : parts) {
				String part = s.trim();
				if (part.isEmpty())
					continue;
				if (part.startsWith("let ")) {
					String content = part.substring(4).trim();
					int eq = content.indexOf('=');
					if (eq < 0)
						return new None<>();
					String left = content.substring(0, eq).trim();
					String rhs = content.substring(eq + 1).trim();
					String name;
					int colon = left.indexOf(':');
					String declaredType = "";
					if (colon >= 0) {
						name = left.substring(0, colon).trim();
						declaredType = left.substring(colon + 1).trim();
					} else
						name = left;
					if (name.isEmpty())
						return new None<>();
					Option<String> r = evalExpr.apply(rhs);
					if (r.isNone())
						return new None<>();
					if (!(r instanceof Some<?> rSomeFinal))
						return new None<>();
					String s1 = (String) rSomeFinal.value();
					Option<Num> n = parseNumericLiteral(s1);
					if (n.isNone())
						return new None<>();

					// if the declaration included a type, ensure compatibility
					if (!declaredType.isEmpty()) {
						if (!ALLOWED_SUFFIXES.contains(declaredType))
							return new None<>();
						if (!(n instanceof Some<?> nSome))
							return new None<>();
						Num num = (Num) nSome.value();
						// plain numeric (no suffix) is allowed for any declared type
						if (!num.suffix.isEmpty() && !num.suffix.equals(declaredType))
							return new None<>();
					}
					if (!(n instanceof Some<?> nSome2))
						return new None<>();
					env.put(name, (Num) nSome2.value());
					// continue to next part
				} else {
					// final expression: evaluate with env available
					return evalExpr.apply(part);
				}
			}
			// if we reach here there was no final standalone expression
			return new None<>();
		});

		if (finalOpt.isSome()) {
			if (!(finalOpt instanceof Some<?> finalSome))
				return new Err<>(new InterpretError("Invalid numeric literal", input));
			return new Ok<>((String) finalSome.value());
		}
		return new Err<>(new InterpretError("Invalid numeric literal", input));
	}
}
