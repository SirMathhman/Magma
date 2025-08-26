package magma;

import magma.ast.ArrayVal;
import magma.ast.BoolVal;
import magma.ast.Expression;
import magma.ast.Num;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Interpreter {

	// Note: prefer pattern-matching on Option (Some/None) or use map/flatMap
	// Instead of a helper that extracts values to nullable references, use
	// `instanceof Some(var value)` at call sites to get the inner value.
	private static final Set<String> ALLOWED_SUFFIXES = Set.of("U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64");

	// Split a string by top-level '+' and '-' (ignoring a leading unary sign).
	// Returns an entry where key = list of terms, value = list of ops.
	private static AbstractMap.SimpleEntry<List<String>, List<Character>> splitAddSub(String s) {
		List<String> terms = new ArrayList<>();
		List<Character> ops = new ArrayList<>();
		int last = 0;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if ((ch == '+' || ch == '-') && i == 0) continue; // unary
			if (ch == '+' || ch == '-') {
				terms.add(s.substring(last, i).trim());
				ops.add(ch);
				last = i + 1;
			}
		}
		terms.add(s.substring(last).trim());
		return new AbstractMap.SimpleEntry<>(terms, ops);
	}

	// Evaluate a term consisting of factors separated by * / % using the provided
	// parseToken function to resolve factors to Num. Returns Option<Num>.
	private static Option<Num> evalTermString(String term, Function<String, Option<Num>> parseToken) {
		String t = term.trim();
		if (t.isEmpty()) return new None<>();
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
			if (fn.isNone()) return new None<>();
			if (!(fn instanceof Some(var fVal))) return new None<>();
			if (first) {
				acc = fVal.value;
				accSuffix = fVal.suffix;
				first = false;
			} else {
				if (!accSuffix.isEmpty() && !fVal.suffix.isEmpty() && !accSuffix.equals(fVal.suffix)) return new None<>();
				String resSuffix = "";
				if (!accSuffix.isEmpty() && !fVal.suffix.isEmpty()) resSuffix = accSuffix;
				switch (pendingOp) {
					case '*':
						acc = acc * fVal.value;
						break;
					case '/':
						if (fVal.value == 0) return new None<>();
						acc = acc / fVal.value;
						break;
					case '%':
						if (fVal.value == 0) return new None<>();
						acc = acc % fVal.value;
						break;
					default:
						return new None<>();
				}
				accSuffix = resSuffix;
			}
			if (nextOp < 0) break;
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
	private static Option<Num> combineAddSub(List<String> terms,
																					 List<Character> ops,
																					 Function<String, Option<Num>> parseToken) {
		if (ops.isEmpty()) {
			return evalTermString(terms.getFirst(), parseToken);
		}
		Option<Num> leftNum = evalTermString(terms.getFirst(), parseToken);
		if (leftNum.isNone()) return new None<>();
		if (!(leftNum instanceof Some(var leftVal))) return new None<>();
		long acc = leftVal.value;
		String accSuffix = leftVal.suffix;
		for (int k = 0; k < ops.size(); k++) {
			char op = ops.get(k);
			Option<Num> rightNum = evalTermString(terms.get(k + 1), parseToken);
			if (rightNum.isNone()) return new None<>();
			if (!(rightNum instanceof Some(var rightVal))) return new None<>();
			if (!accSuffix.isEmpty() && !rightVal.suffix.isEmpty() && !accSuffix.equals(rightVal.suffix)) return new None<>();
			String resSuffix = "";
			if (!accSuffix.isEmpty() && !rightVal.suffix.isEmpty()) resSuffix = accSuffix;
			switch (op) {
				case '+':
					acc = acc + rightVal.value;
					break;
				case '-':
					acc = acc - rightVal.value;
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
		if (suf.isEmpty()) {
			try {
				return new Some<>(new Num(Integer.parseInt(pref), ""));
			} catch (NumberFormatException e) {
				return new None<>();
			}
		}
		if (!ALLOWED_SUFFIXES.contains(suf)) return new None<>();
		if (pref.startsWith("-") && suf.startsWith("U")) return new None<>();
		try {
			return new Some<>(new Num(Integer.parseInt(pref), suf));
		} catch (NumberFormatException e) {
			return new None<>();
		}
	}

	// Parse an array type string like "[I32; 3]" or "[[I32;2];2]" into a TypeDesc
	private static Option<ArrayType> parseArrayType(String dt) {
		String t = dt.trim();
		if (!t.startsWith("[") || !t.endsWith("]")) return new None<>();
		String inside = t.substring(1, t.length() - 1).trim();
		int bracket = 0;
		int semi = -1;
		for (int i = 0; i < inside.length(); i++) {
			char ch = inside.charAt(i);
			if (ch == '[') bracket++;
			else if (ch == ']') bracket--;
			else if (ch == ';' && bracket == 0) {
				semi = i;
				break;
			}
		}
		if (semi < 0) return new None<>();
		String left = inside.substring(0, semi).trim();
		String right = inside.substring(semi + 1).trim();
		if (right.isEmpty()) return new None<>();
		int len;
		try {
			len = Integer.parseInt(right);
		} catch (NumberFormatException ex) {
			return new None<>();
		}
		// left may be a nested array type or a base suffix
		if (left.startsWith("[")) {
			Option<ArrayType> innerOpt = parseArrayType(left);
			if (!(innerOpt instanceof Some(var innerVal))) return new None<>();
			return new Some<>(new ArrayType(null, innerVal, len));
		} else {
			if (!ALLOWED_SUFFIXES.contains(left)) return new None<>();
			return new Some<>(new ArrayType(left, null, len));
		}
	}

	// resolve a named array element to an ArrayElem if present and index in range
	private static Option<Expression> resolveArrayElement(Map<String, Expression> env, String name, int idx) {
		if (!env.containsKey(name)) return new None<>();
		Expression v = env.get(name);
		return indexInto(v, idx);
	}

	// helper: find matching closing ']' starting at position start (which must
	// point at '[')
	// returns the index of the matching ']' or -1 if not found
	private static int findMatchingBracket(String s, int start) {
		int depth = 0;
		for (int i = start; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '[') depth++;
			else if (ch == ']') {
				depth--;
				if (depth == 0) return i;
			}
		}
		return -1;
	}

	// helper: index into an Expression value if it's an ArrayVal
	private static Option<Expression> indexInto(Expression v, int idx) {
		if (!(v instanceof ArrayVal arr)) return new None<>();
		try {
			return new Some<>(arr.items[idx]);
		} catch (IndexOutOfBoundsException ex) {
			return new None<>();
		}
	}

	// helper: validate that an array's elements conform to the provided TypeDesc
	private static boolean validateArrayAgainstType(ArrayType td, Expression[] elems) {
		if (td.len != elems.length) return false;
		if (td.baseSuffix != null) {
			for (Expression e : elems) {
				if (!(e instanceof Num nn)) return false;
				if (!nn.suffix.isEmpty() && !nn.suffix.equals(td.baseSuffix)) return false;
				if (td.baseSuffix.startsWith("U") && nn.value < 0) return false;
			}
			return true;
		} else {
			for (Expression e : elems) {
				if (!(e instanceof ArrayVal av)) return false;
				if (av.items.length != td.inner.len) return false;
			}
			return true;
		}
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
			List<String> partsList = new ArrayList<>();
			int last = 0;
			int round = 0;
			int square = 0;
			for (int i = 0; i < trimmed.length(); i++) {
				char ch = trimmed.charAt(i);
				if (ch == '(') round++;
				else if (ch == ')') round--;
				else if (ch == '[') square++;
				else if (ch == ']') square--;
				else if (ch == ';' && round == 0 && square == 0) {
					partsList.add(trimmed.substring(last, i).trim());
					last = i + 1;
				}
			}
			if (last < trimmed.length()) partsList.add(trimmed.substring(last).trim());
			String[] parts = partsList.toArray(new String[0]);
			System.err.println("DEBUG interpret parts:" + Arrays.toString(parts));
			Map<String, Expression> env = new HashMap<>();

			// helper: resolve a token to Num either from env (Num) or as a numeric literal
			BiFunction<Map<String, Expression>, String, Option<Num>> resolveNumToken = (environment, tok) -> {
				if (environment.containsKey(tok)) {
					Expression v = environment.get(tok);
					if (v instanceof Num numV) return new Some<>(numV);
					return new None<>();
				}
				Option<Num> pn = parseNumericLiteral(tok);
				if (pn.isSome()) return pn;
				return new None<>();
			};

			// helper: resolve name[index] where index is parsed by the provided idxParser
			// Supports chained indexing like name[0][1]
			BiFunction<String, Function<String, Option<Num>>, Option<Expression>> resolveIndexed = (s, idxParser) -> {
				String t = s.trim();
				if (!t.contains("[") || !t.endsWith("]")) return new None<>();
				// extract base name before first '['
				int pos = t.indexOf('[');
				if (pos <= 0) return new None<>();
				String base = t.substring(0, pos).trim();
				// find matching close for first '['
				int j = findMatchingBracket(t, pos);
				if (j < 0) return new None<>();
				String firstIdxStr = t.substring(pos + 1, j).trim();
				Option<Num> firstIdxOpt = idxParser.apply(firstIdxStr);
				if (!(firstIdxOpt instanceof Some(var firstIdxNum))) return new None<>();
				int firstIdx = firstIdxNum.value;
				// resolve the first indexing using the helper to avoid duplicating
				// ArrayVal bounds-checking logic
				Option<Expression> first = resolveArrayElement(env, base, firstIdx);
				if (first.isNone()) return new None<>();
				Expression current = ((Some<Expression>) first).value();
				// continue resolving any further chained indices
				int i = j + 1;
				while (i < t.length()) {
					// skip whitespace
					while (i < t.length() && Character.isWhitespace(t.charAt(i))) i++;
					if (i == t.length()) break;
					if (t.charAt(i) != '[') return new None<>();
					// find closing for this '['
					j = findMatchingBracket(t, i);
					if (j < 0) return new None<>();
					String idxStr = t.substring(i + 1, j).trim();
					Option<Num> idxOpt = idxParser.apply(idxStr);
					if (!(idxOpt instanceof Some(var idxNum))) return new None<>();
					int idx = idxNum.value;
					Option<Expression> next = indexInto(current, idx);
					if (next.isNone()) return new None<>();
					current = ((Some<Expression>) next).value();
					i = j + 1;
				}
				return new Some<>(current);
			};

			// helper: format a stored env value to a string for final-expression lookup
			BiFunction<Map<String, Expression>, String, Option<String>> envValueToString = (environment, key) -> {
				if (!environment.containsKey(key)) return new None<>();
				Expression val = environment.get(key);
				if (val instanceof Num n) return new Some<>(String.valueOf(n.value));
				if (val instanceof ArrayVal arr) return new Some<>("[array:" + arr.items.length + "]");
				return new None<>();
			};

			// helper to parse a numeric/result string into Num (prefix and suffix)

			// parseToken: parse a token either as a numeric literal or a previously bound
			// variable (supports literal array indexing returning numeric elements)
			Function<String, Option<Num>> parseToken = token -> {
				String t = token.trim();
				if (t.isEmpty()) return new None<>();
				// variable lookup: support array indexing like name[index] (literal index)
				if (t.contains("[") && t.endsWith("]")) {
					Option<Expression> res = resolveIndexed.apply(t, Interpreter::parseNumericLiteral);
					if (!(res instanceof Some(var obj))) return new None<>();
					if (!(obj instanceof Num)) return new None<>();
					return new Some<>((Num) obj);
				}
				// delegate numeric token resolution to helper
				return resolveNumToken.apply(env, t);
			};

			// helper to evaluate a single expression using parseToken and env
			Function<String, Option<String>> evalExpr = new Function<>() {
				@Override
				public Option<String> apply(String expr) {
					String e = expr.trim();
					if (e.isEmpty()) return new None<>();

					// evaluate parentheses first (innermost first)
					while (e.contains("(")) {
						int open = e.lastIndexOf('(');
						int close = e.indexOf(')', open);
						if (open < 0 || close < 0) return new None<>();
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
					Function<String, Option<Num>> evalNumeric = ex -> {
						String s = ex.trim();
						if (s.isEmpty()) return new None<>();

						var split = splitAddSub(s);
						List<String> termsLocal = split.getKey();
						List<Character> opsLocal = split.getValue();

						// evaluate using shared helpers
						if (opsLocal.isEmpty()) {
							return evalTermString(s, parseToken);
						}
						return combineAddSub(termsLocal, opsLocal, parseToken);
					};

					// numeric comparison operators: evaluate comparisons between two
					// numeric expressions. Require both sides to be numeric and of
					// the same suffix/type (including both plain).
					String[] comparators = new String[]{">=", "<=", "==", "!=", ">", "<"};
					for (String comp : comparators) {
						int pos = e.indexOf(comp);
						if (pos >= 0) {
							String leftExpr = e.substring(0, pos).trim();
							String rightExpr = e.substring(pos + comp.length()).trim();
							if (leftExpr.isEmpty() || rightExpr.isEmpty()) return new None<>();
							Option<Num> lnOpt = evalNumeric.apply(leftExpr);
							Option<Num> rnOpt = evalNumeric.apply(rightExpr);
							if (!(lnOpt instanceof Some(var lnval)) || !(rnOpt instanceof Some(var rnval))) return new None<>();
							// require matching suffixes
							if (!lnval.suffix.equals(rnval.suffix)) return new None<>();
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
					BiFunction<String, Boolean, Option<String>> evalBoolOp = (opRegex, isOr) -> {
						String[] parts = e2.split(opRegex);
						boolean acc = !isOr;
						for (String p : parts) {
							Option<String> pr = this.apply(p);
							if (!(pr instanceof Some(var vRaw))) return new None<>();
							String v = vRaw.trim();
							if (!(v.equals("true") || v.equals("false"))) return new None<>();
							if (isOr && v.equals("true")) acc = true;
							if (!isOr && v.equals("false")) acc = false;
						}
						return new Some<>(acc ? "true" : "false");
					};

					if (e2.contains("||")) return evalBoolOp.apply("\\|\\|", true);
					if (e2.contains("&&")) return evalBoolOp.apply("&&", false);

					// arithmetic: handle * / % first within terms, then + -
					var split2 = splitAddSub(e);
					List<String> terms = split2.getKey();
					List<Character> ops = split2.getValue();

					if (ops.isEmpty()) {
						// only treat as a whole term when it contains multiplicative
						// operators; single-token expressions (like a lone boolean or
						// numeric literal with suffix) should fall through to the
						// single-token fallback below so we don't accidentally append
						// suffixes where tests expect the plain value.
						if (e.contains("*") || e.contains("/") || e.contains("%")) {
							Option<Num> wnOpt = evalTermString(e, parseToken);
							if (!(wnOpt instanceof Some(var wn))) return new None<>();
							// when the term actually performed multiplicative ops we
							// preserve suffixes (if any) like before
							return new Some<>(wn.value + wn.suffix);
						}
					}

					if (!ops.isEmpty()) {
						Option<Num> combOpt = combineAddSub(terms, ops, parseToken);
						if (!(combOpt instanceof Some(var comb))) return new None<>();
						return new Some<>(comb.value + comb.suffix);
					}

					// single token fallback: boolean, variable, or numeric literal
					if (e.equals("true") || e.equals("false")) return new Some<>(e);
					// support array indexing in final expression: name[index]
					if (e.contains("[") && e.endsWith("]")) {
						Option<Expression> resolved = resolveIndexed.apply(e, evalNumeric);
						if (!(resolved instanceof Some(var elemObj))) return new None<>();
						if (elemObj instanceof Num n) return new Some<>(String.valueOf(n.value));
						if (elemObj instanceof BoolVal bv) return new Some<>(bv.value ? "true" : "false");
						return new None<>();
					}
					// try resolving stored env value to a string
					Option<String> envStr = envValueToString.apply(env, e);
					if (envStr.isSome()) return envStr;
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
				if (part.isEmpty()) continue;
				if (part.startsWith("let ")) {
					System.err.println("DEBUG processing let part='" + part + "'");
					String content = part.substring(4).trim();
					int eq = content.indexOf('=');
					if (eq < 0) return new None<>();
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
					} else name = left;
					if (name.isEmpty()) return new None<>();
					// Support numeric initialization or array literal initialization
					if (rhs.startsWith("[") && rhs.endsWith("]")) {
						System.err.println("DEBUG: parsing array literal rhs='" + rhs + "' declaredType='" + declaredType + "'");

						@SuppressWarnings("unchecked")
						final BiFunction<String, Option<ArrayType>, Option<Expression>>[] parseArrayLiteralRef =
								(BiFunction<String, Option<ArrayType>, Option<Expression>>[]) new BiFunction[1];
						parseArrayLiteralRef[0] = (lit, tdOpt) -> {
							String s = lit.trim();
							if (!s.startsWith("[") || !s.endsWith("]")) return new None<>();
							String inner = s.substring(1, s.length() - 1).trim();
							List<String> partsArr = new ArrayList<>();
							if (!inner.isEmpty()) {
								int lastComma = 0;
								int r = 0;
								int sq = 0;
								for (int ii = 0; ii < inner.length(); ii++) {
									char ch2 = inner.charAt(ii);
									if (ch2 == '(') r++;
									else if (ch2 == ')') r--;
									else if (ch2 == '[') sq++;
									else if (ch2 == ']') sq--;
									else if (ch2 == ',' && r == 0 && sq == 0) {
										partsArr.add(inner.substring(lastComma, ii).trim());
										lastComma = ii + 1;
									}
								}
								partsArr.add(inner.substring(lastComma).trim());
							}

							Expression[] elems = new Expression[partsArr.size()];
							String elemSuffix = "";
							boolean isBool = false;

							for (int i = 0; i < partsArr.size(); i++) {
								String it = partsArr.get(i);
								// nested array element
								if (it.startsWith("[") && it.endsWith("]")) {
									Option<ArrayType> innerTd = new None<>();
									if (tdOpt instanceof Some(var tdv) && tdv.inner != null) innerTd = new Some<>(tdv.inner);
									Option<Expression> child = parseArrayLiteralRef[0].apply(it, innerTd);
									if (!(child instanceof Some(var cval))) return new None<>();
									elems[i] = cval;
									continue;
								}

								Option<String> ev = evalExpr.apply(it);
								System.err.println("DEBUG: array item eval of '" + it + "' -> " + ev);
								if (!(ev instanceof Some(var evVal))) return new None<>();
								if (evVal.equals("true") || evVal.equals("false")) {
									if (i == 0) {
										isBool = true;
										elemSuffix = "";
									} else if (!isBool) {
										return new None<>();
									}
									elems[i] = new BoolVal(evVal.equals("true"));
									continue;
								}
								Option<Num> pn = parseNumericLiteral(evVal);
								if (!(pn instanceof Some(var numVal))) return new None<>();
								if (i == 0) elemSuffix = numVal.suffix;
								else if (!elemSuffix.equals(numVal.suffix)) return new None<>();
								elems[i] = numVal;
							}

							// default to I32 for plain numeric arrays when no declared type
							if (tdOpt instanceof None && !isBool && elemSuffix.isEmpty()) elemSuffix = "I32";

							// if declared type present, validate
							if (tdOpt instanceof Some(var tdv2)) {
								if (tdv2.len != elems.length) return new None<>();
								// if element is numeric base type
								if (tdv2.baseSuffix != null) {
									if (!validateArrayAgainstType(tdv2, elems)) return new None<>();
									elemSuffix = tdv2.baseSuffix;
								} else {
									// nested array declared; ensure elements are ArrayVal and lengths match
									// recursively
									if (!validateArrayAgainstType(tdv2, elems)) return new None<>();
									elemSuffix = "";
								}
							}

							return new Some<>(new ArrayVal(elems, elemSuffix));
						};

						Option<ArrayType> typeDescOpt = new None<>();
						if (!declaredType.isEmpty()) {
							Option<ArrayType> parsed = parseArrayType(declaredType);
							if (parsed.isNone()) return new None<>();
							typeDescOpt = parsed;
						}
						Option<Expression> arr = parseArrayLiteralRef[0].apply(rhs, typeDescOpt);
						if (!(arr instanceof Some(var arrv))) return new None<>();
						env.put(name, arrv);
						mutMap.put(name, isMutable);
						continue;
					}

					Option<String> s1Opt = evalExpr.apply(rhs);
					if (!(s1Opt instanceof Some(var s1Val))) return new None<>();
					Option<Num> n = parseNumericLiteral(s1Val);
					if (n.isNone()) return new None<>();

					// if the declaration included a type, ensure compatibility
					if (!declaredType.isEmpty()) {
						if (!ALLOWED_SUFFIXES.contains(declaredType)) return new None<>();
						if (!(n instanceof Some(var num))) return new None<>();
						// reject assigning a negative plain numeric to an unsigned declared type
						if (declaredType.startsWith("U") && num.value < 0) return new None<>();
						// plain numeric (no suffix) is allowed for any declared type
						if (!num.suffix.isEmpty() && !num.suffix.equals(declaredType)) return new None<>();
					}
					if (!(n instanceof Some(var stored))) return new None<>();
					System.err.println(
							"DEBUG storing var '" + name + "' = " + stored + " mutable=" + isMutable + " declaredType='" +
							declaredType + "'");
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
					if (leftName.isEmpty()) return new None<>();
					if (!env.containsKey(leftName)) return new None<>();
					if (!mutMap.getOrDefault(leftName, false)) return new None<>();
					Option<String> sValOpt = evalExpr.apply(rhs);
					if (!(sValOpt instanceof Some(var sVal))) return new None<>();
					Option<Num> n = parseNumericLiteral(sVal);
					if (n.isNone()) return new None<>();
					if (!(n instanceof Some(var stored))) return new None<>();
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
