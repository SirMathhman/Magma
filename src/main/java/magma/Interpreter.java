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

	// Note: prefer pattern-matching on Option (Some/None) or use map/flatMap
	// Instead of a helper that extracts values to nullable references, use
	// `instanceof Some(var value)` at call sites to get the inner value.

	// Split a string by top-level '+' and '-' (ignoring a leading unary sign).
	// Returns an entry where key = list of terms, value = list of ops.
	private static java.util.AbstractMap.SimpleEntry<java.util.List<String>, java.util.List<Character>> splitAddSub(
			String s) {
		java.util.List<String> terms = new java.util.ArrayList<>();
		java.util.List<Character> ops = new java.util.ArrayList<>();
		int last = 0;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if ((ch == '+' || ch == '-') && i == 0)
				continue; // unary
			if (ch == '+' || ch == '-') {
				terms.add(s.substring(last, i).trim());
				ops.add(ch);
				last = i + 1;
			}
		}
		terms.add(s.substring(last).trim());
		return new java.util.AbstractMap.SimpleEntry<java.util.List<String>, java.util.List<Character>>(terms, ops);
	}

	// Evaluate a term consisting of factors separated by * / % using the provided
	// parseToken function to resolve factors to Num. Returns Option<Num>.
	private static Option<Num> evalTermString(String term, Function<String, Option<Num>> parseToken) {
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
			if (!(fn instanceof Some(var fVal)))
				return new None<>();
			Num f = fVal;
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
	}

	// Combine a list of terms with '+' and '-' operators using the provided
	// parseToken via evalTermString. Returns Option<Num>.
	private static Option<Num> combineAddSub(java.util.List<String> terms, java.util.List<Character> ops,
			Function<String, Option<Num>> parseToken) {
		if (ops.isEmpty()) {
			return evalTermString(terms.get(0), parseToken);
		}
		Option<Num> leftNum = evalTermString(terms.get(0), parseToken);
		if (leftNum.isNone())
			return new None<>();
		if (!(leftNum instanceof Some(var leftVal)))
			return new None<>();
		long acc = leftVal.value;
		String accSuffix = leftVal.suffix;
		for (int k = 0; k < ops.size(); k++) {
			char op = ops.get(k);
			Option<Num> rightNum = evalTermString(terms.get(k + 1), parseToken);
			if (rightNum.isNone())
				return new None<>();
			if (!(rightNum instanceof Some(var rightVal)))
				return new None<>();
			Num r = rightVal;
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
		return new Some<>(new Num((int) acc, accSuffix));
	}

	// parse a numeric literal (with optional +/- sign and optional suffix) into Num
	private static Option<Num> parseNumericLiteral(String s) {
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

	// resolve a named array element to a Num if present and index in range
	private static Option<Num> resolveArrayElement(Map<String, Object> env, String name, int idx) {
		if (!env.containsKey(name))
			return new None<>();
		Object v = env.get(name);
		if (!(v instanceof ArrayVal arr))
			return new None<>();
		if (idx < 0 || idx >= arr.items.length)
			return new None<>();
		return new Some<>(arr.items[idx]);
	}

	// Assume that input can never be null.
	public static Result<String, InterpretError> interpret(String input) {
		Option<String> in = ((Option<String>) new Some<>(input)).map(String::trim);

		// handle either a binary operation or a plain/suffixed input
		Option<String> finalOpt = in.flatMap(trimmed -> {
			// support multiple let-bindings separated by top-level ';', e.g.
			// "let x : I32 = 0; let y : I32 = 40; x"
			// but avoid splitting on semicolons that appear inside brackets or
			// parentheses (e.g. typed array declarations like [I32; 3]).
			java.util.List<String> partsList = new java.util.ArrayList<>();
			int last = 0;
			int round = 0;
			int square = 0;
			for (int i = 0; i < trimmed.length(); i++) {
				char ch = trimmed.charAt(i);
				if (ch == '(')
					round++;
				else if (ch == ')')
					round--;
				else if (ch == '[')
					square++;
				else if (ch == ']')
					square--;
				else if (ch == ';' && round == 0 && square == 0) {
					partsList.add(trimmed.substring(last, i).trim());
					last = i + 1;
				}
			}
			partsList.add(trimmed.substring(last).trim());
			String[] parts = partsList.toArray(new String[0]);
			System.err.println("DEBUG interpret parts:" + java.util.Arrays.toString(parts));
			Map<String, Object> env = new HashMap<>();

			// helper to parse a numeric/result string into Num (prefix and suffix)

			// parseToken: parse a token either as a numeric literal or a previously bound
			// variable
			Function<String, Option<Num>> parseToken = token -> {
				String t = token.trim();
				if (t.isEmpty())
					return new None<>();
				// variable lookup: support array indexing like name[index] (literal index)
				if (t.contains("[") && t.endsWith("]")) {
					int b = t.indexOf('[');
					String name = t.substring(0, b).trim();
					String idxStr = t.substring(b + 1, t.length() - 1).trim();
					Option<Num> idxOpt = parseNumericLiteral(idxStr);
					if (!(idxOpt instanceof Some(var idxNum)))
						return new None<>();
					int idx = idxNum.value;
					return resolveArrayElement(env, name, idx);
				}
				// variable lookup: only return Num in numeric contexts
				if (env.containsKey(t)) {
					Object v = env.get(t);
					if (v instanceof Num numV)
						return new Some<>(numV);
					return new None<>();
				}
				Option<Num> pn = parseNumericLiteral(t);
				if (pn.isSome())
					return pn;
				return new None<>();
			};

			// helper to evaluate a single expression using parseToken and env
			Function<String, Option<String>> evalExpr = new Function<>() {
				@Override
				public Option<String> apply(String expr) {
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
						if (inner instanceof Some(var innerVal)) {
							e = e.substring(0, open) + innerVal + e.substring(close + 1);
						} else {
							return new None<>();
						}
					}

					// helper to evaluate an expression as a numeric value (preserve
					// suffixes). This mirrors the arithmetic logic but returns
					// Option<Num> so callers (like comparisons) can inspect types.
					java.util.function.Function<String, Option<Num>> evalNumeric = ex -> {
						String s = ex.trim();
						if (s.isEmpty())
							return new None<>();

						var split = splitAddSub(s);
						java.util.List<String> termsLocal = split.getKey();
						java.util.List<Character> opsLocal = split.getValue();

						// evaluate using shared helpers
						if (opsLocal.isEmpty()) {
							return evalTermString(s, parseToken);
						}
						return combineAddSub(termsLocal, opsLocal, parseToken);
					};

					// numeric comparison operators: evaluate comparisons between two
					// numeric expressions. Require both sides to be numeric and of
					// the same suffix/type (including both plain).
					String[] comparators = new String[] { ">=", "<=", "==", "!=", ">", "<" };
					for (String comp : comparators) {
						int pos = e.indexOf(comp);
						if (pos >= 0) {
							String leftExpr = e.substring(0, pos).trim();
							String rightExpr = e.substring(pos + comp.length()).trim();
							if (leftExpr.isEmpty() || rightExpr.isEmpty())
								return new None<>();
							Option<Num> lnOpt = evalNumeric.apply(leftExpr);
							Option<Num> rnOpt = evalNumeric.apply(rightExpr);
							if (!(lnOpt instanceof Some(var lnval)) || !(rnOpt instanceof Some(var rnval)))
								return new None<>();
							// require matching suffixes
							if (!lnval.suffix.equals(rnval.suffix))
								return new None<>();
							boolean cmp;
							switch (comp) {
								case ">":
									cmp = lnval.value > rnval.value;
									break;
								case "<":
									cmp = lnval.value < rnval.value;
									break;
								case ">=":
									cmp = lnval.value >= rnval.value;
									break;
								case "<=":
									cmp = lnval.value <= rnval.value;
									break;
								case "==":
									cmp = lnval.value == rnval.value;
									break;
								case "!=":
									cmp = lnval.value != rnval.value;
									break;
								default:
									return new None<>();
							}
							return new Some<>(cmp ? "true" : "false");
						}
					}

					// boolean operators: || (lowest) and && (next). Use a small helper
					// to avoid duplicating validation/iteration logic (CPD).
					final String e2 = e;
					java.util.function.BiFunction<String, Boolean, Option<String>> evalBoolOp = (opRegex, isOr) -> {
						String[] parts = e2.split(opRegex);
						boolean acc = isOr ? false : true;
						for (String p : parts) {
							Option<String> pr = this.apply(p);
							if (!(pr instanceof Some(var vRaw)))
								return new None<>();
							String v = vRaw.trim();
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
					var split2 = splitAddSub(e);
					java.util.List<String> terms = split2.getKey();
					java.util.List<Character> ops = split2.getValue();

					if (ops.isEmpty()) {
						// only treat as a whole term when it contains multiplicative
						// operators; single-token expressions (like a lone boolean or
						// numeric literal with suffix) should fall through to the
						// single-token fallback below so we don't accidentally append
						// suffixes where tests expect the plain value.
						if (e.contains("*") || e.contains("/") || e.contains("%")) {
							Option<Num> wnOpt = evalTermString(e, parseToken);
							if (!(wnOpt instanceof Some(var wn)))
								return new None<>();
							// when the term actually performed multiplicative ops we
							// preserve suffixes (if any) like before
							return new Some<>(String.valueOf(wn.value) + wn.suffix);
						}
					}

					if (!ops.isEmpty()) {
						Option<Num> combOpt = combineAddSub(terms, ops, parseToken);
						if (!(combOpt instanceof Some(var comb)))
							return new None<>();
						return new Some<>(String.valueOf((int) comb.value) + comb.suffix);
					}

					// single token fallback: boolean, variable, or numeric literal
					if (e.equals("true") || e.equals("false"))
						return new Some<>(e);
					// support array indexing in final expression: name[index]
					if (e.contains("[") && e.endsWith("]")) {
						int b = e.indexOf('[');
						String name = e.substring(0, b).trim();
						String idxExpr = e.substring(b + 1, e.length() - 1).trim();
						Option<Num> idxOpt = evalNumeric.apply(idxExpr);
						if (!(idxOpt instanceof Some(var idxNum)))
							return new None<>();
						int idx = idxNum.value;
						Option<Num> resolved = resolveArrayElement(env, name, idx);
						if (!(resolved instanceof Some(var elem)))
							return new None<>();
						return new Some<>(String.valueOf(elem.value));
					}
					if (env.containsKey(e)) {
						Object val = env.get(e);
						if (val instanceof Num n)
							return new Some<>(String.valueOf(n.value));
						if (val instanceof ArrayVal arr)
							return new Some<>("[array:" + arr.items.length + "]");
					}
					Option<Num> pn = parseNumericLiteral(e);
					if (pn instanceof Some(var nVal)) {
						return new Some<>(String.valueOf(nVal.value));
					}
					return new None<>();
				}
			};

			// process sequential parts: each part may be a let decl, assignment, or the
			// final expr
			Map<String, Boolean> mutMap = new HashMap<>();
			for (int pi = 0; pi < parts.length; pi++) {
				String part = parts[pi].trim();
				if (part.isEmpty())
					continue;
				if (part.startsWith("let ")) {
					System.err.println("DEBUG processing let part='" + part + "'");
					String content = part.substring(4).trim();
					int eq = content.indexOf('=');
					if (eq < 0)
						return new None<>();
					String left = content.substring(0, eq).trim();
					String rhs = content.substring(eq + 1).trim();

					boolean isMutable = false;
					if (left.startsWith("mut ")) {
						isMutable = true;
						left = left.substring(4).trim();
					}

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
					// Support numeric initialization or array literal initialization
					if (rhs.startsWith("[") && rhs.endsWith("]")) {
						System.err.println("DEBUG: parsing array literal rhs='" + rhs + "' declaredType='" + declaredType + "'");
						// parse array literal items
						String inner = rhs.substring(1, rhs.length() - 1).trim();
						java.util.List<String> partsArr = new java.util.ArrayList<>();
						if (!inner.isEmpty()) {
							for (String it : inner.split(","))
								partsArr.add(it.trim());
						}
						Num[] items = new Num[partsArr.size()];
						String elemSuffix = "";
						for (int i = 0; i < partsArr.size(); i++) {
							String it = partsArr.get(i);
							Option<String> ev = evalExpr.apply(it);
							System.err.println("DEBUG: array item eval of '" + it + "' -> " + ev);
							if (!(ev instanceof Some(var evVal)))
								return new None<>();
							Option<Num> pn = parseNumericLiteral(evVal);
							if (!(pn instanceof Some(var numVal)))
								return new None<>();
							if (i == 0)
								elemSuffix = numVal.suffix;
							else if (!elemSuffix.equals(numVal.suffix))
								return new None<>();
							items[i] = numVal;
						}
						// if declaredType is present, validate like [I32; 3]
						if (!declaredType.isEmpty()) {
							String dt = declaredType.trim();
							if (!dt.startsWith("[") || !dt.contains(";") || !dt.endsWith("]"))
								return new None<>();
							String innerDt = dt.substring(1, dt.length() - 1).trim();
							int semi = innerDt.indexOf(';');
							if (semi < 0)
								return new None<>();
							String typePart = innerDt.substring(0, semi).trim();
							String lenPart = innerDt.substring(semi + 1).trim();
							if (lenPart.endsWith("]"))
								lenPart = lenPart.substring(0, lenPart.length());
							// validate element type
							if (!ALLOWED_SUFFIXES.contains(typePart))
								return new None<>();
							// validate length is integer and matches
							int declLen;
							try {
								declLen = Integer.parseInt(lenPart);
							} catch (NumberFormatException ex) {
								return new None<>();
							}
							if (declLen != items.length)
								return new None<>();
							// element suffix must match declared type (or be plain)
							if (!elemSuffix.isEmpty() && !elemSuffix.equals(typePart))
								return new None<>();
						}
						env.put(name, new ArrayVal(items, elemSuffix));
						mutMap.put(name, isMutable);
						continue;
					}

					Option<String> s1Opt = evalExpr.apply(rhs);
					if (!(s1Opt instanceof Some(var s1Val)))
						return new None<>();
					String s1 = s1Val;
					Option<Num> n = parseNumericLiteral(s1);
					if (n.isNone())
						return new None<>();

					// if the declaration included a type, ensure compatibility
					if (!declaredType.isEmpty()) {
						if (!ALLOWED_SUFFIXES.contains(declaredType))
							return new None<>();
						if (!(n instanceof Some(var num)))
							return new None<>();
						// plain numeric (no suffix) is allowed for any declared type
						if (!num.suffix.isEmpty() && !num.suffix.equals(declaredType))
							return new None<>();
					}
					if (!(n instanceof Some(var stored)))
						return new None<>();
					System.err.println("DEBUG storing var '" + name + "' = " + stored + " mutable=" + isMutable
							+ " declaredType='" + declaredType + "'");
					env.put(name, stored);
					mutMap.put(name, isMutable);
					continue;
				}

				// detect simple assignment `name = expr` (not comparisons like ==, >=, etc.)
				int eqIdx = part.indexOf('=');
				boolean isComparison = part.contains("==") || part.contains(">=") || part.contains("<=") || part.contains("!=");
				if (eqIdx >= 0 && !isComparison) {
					String leftName = part.substring(0, eqIdx).trim();
					String rhs = part.substring(eqIdx + 1).trim();
					if (leftName.isEmpty())
						return new None<>();
					if (!env.containsKey(leftName))
						return new None<>();
					if (!mutMap.getOrDefault(leftName, false))
						return new None<>();
					Option<String> sValOpt = evalExpr.apply(rhs);
					if (!(sValOpt instanceof Some(var sVal)))
						return new None<>();
					Option<Num> n = parseNumericLiteral(sVal);
					if (n.isNone())
						return new None<>();
					if (!(n instanceof Some(var stored)))
						return new None<>();
					env.put(leftName, stored);
					// if this was the final part, return the assigned value
					if (pi == parts.length - 1) {
						return new Some<>(String.valueOf(stored.value));
					}
					continue;
				}

				// final expression: evaluate with env available
				System.err.println("DEBUG evaluating final expression '" + part + "'");
				Option<String> res = evalExpr.apply(part);
				System.err.println("DEBUG final eval result: " + res);
				return res;
			}
			// if we reach here there was no final standalone expression
			return new None<>();
		});

		if (finalOpt instanceof Some(var out)) {
			return new Ok<>(out);
		}
		return new Err<>(new InterpretError("Invalid numeric literal", input));
	}
}
