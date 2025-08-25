package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		try {
			if (input != null) {
				String trimmed = input.trim();
				int plusIdx = trimmed.indexOf('+');
				// treat as binary addition only if '+' is not the first or last char
				if (plusIdx > 0 && plusIdx < trimmed.length() - 1) {
					String left = trimmed.substring(0, plusIdx).trim();
					String right = trimmed.substring(plusIdx + 1).trim();

					if (!left.isEmpty() && !right.isEmpty()) {
						// parse both sides including optional suffixes
						class Num {
							int value;
							String suffix;

							Num(int v, String s) {
								value = v;
								suffix = s;
							}
						}

						java.util.function.Function<String, Num> parseToken = token -> {
							if (token == null || token.isEmpty())
								return null;
							// try plain integer first
							try {
								int v = Integer.parseInt(token);
								return new Num(v, "");
							} catch (NumberFormatException ex) {
								// fall through to suffix parsing
							}

							int tlen = token.length();
							int i = 0;
							if (i < tlen) {
								char c = token.charAt(i);
								if (c == '+' || c == '-')
									i++;
							}
							int ds = i;
							while (i < tlen && Character.isDigit(token.charAt(i)))
								i++;
							if (i <= ds)
								return null;
							String pref = token.substring(0, i);
							String suf = token.substring(i);
							var allowed = java.util.Set.of("U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64");
							if (suf.isEmpty()) {
								try {
									return new Num(Integer.parseInt(pref), "");
								} catch (NumberFormatException e) {
									return null;
								}
							}
							if (!allowed.contains(suf))
								return null;
							if (pref.startsWith("-") && suf.startsWith("U"))
								return null;
							try {
								return new Num(Integer.parseInt(pref), suf);
							} catch (NumberFormatException e) {
								return null;
							}
						};

						Num L = parseToken.apply(left);
						if (L == null)
							return new Err<>(new InterpretError("Invalid numeric literal", input));
						Num R = parseToken.apply(right);
						if (R == null)
							return new Err<>(new InterpretError("Invalid numeric literal", input));

						// suffix handling:
						// - if either operand is plain (no suffix), the result is plain
						// - if both have suffixes they must match and the result keeps that suffix
						String resSuffix = "";
						if (!L.suffix.isEmpty() && !R.suffix.isEmpty()) {
							if (!L.suffix.equals(R.suffix)) {
								return new Err<>(new InterpretError("Mismatched numeric suffixes", input));
							}
							resSuffix = L.suffix;
						} else {
							// one or both are plain -> result is plain (no suffix)
							resSuffix = "";
						}

						long sum = (long) L.value + (long) R.value;
						// keep as int string, let overflow wrap as per Integer.parseInt earlier
						// behaviour
						int sumInt = (int) sum;
						return new Ok<>(String.valueOf(sumInt) + resSuffix);
					}
				}
			}
			Integer.parseInt(input);
			return new Ok<>(input);
		} catch (NumberFormatException e) {
			// If input has a numeric prefix followed by a type-suffix (e.g. "5I32"),
			// accept the leading integer portion as the value.
			if (input == null || input.isEmpty()) {
				return new Err<>(new InterpretError("Invalid numeric literal", input));
			}

			int len = input.length();
			int idx = 0;

			// optional sign
			if (idx < len) {
				char c = input.charAt(idx);
				if (c == '+' || c == '-') {
					idx++;
				}
			}

			int digitsStart = idx;
			while (idx < len && Character.isDigit(input.charAt(idx))) {
				idx++;
			}

			if (idx > digitsStart) {
				// include sign if present
				String prefix = input.substring(0, idx);
				String suffix = input.substring(idx);

				if (suffix.isEmpty()) {
					return new Ok<>(prefix);
				}

				var allowed = java.util.Set.of(
						"U8", "U16", "U32", "U64",
						"I8", "I16", "I32", "I64");

				if (allowed.contains(suffix)) {
					// negative values with unsigned suffixes are invalid (e.g. "-5U8")
					if (prefix.startsWith("-") && suffix.startsWith("U")) {
						return new Err<>(new InterpretError("Negative value for unsigned type", input));
					}
					return new Ok<>(prefix);
				} else {
					return new Err<>(new InterpretError("Unsupported numeric suffix", input));
				}
			}

			return new Err<>(new InterpretError("Invalid numeric literal", input));
		}
	}
}
