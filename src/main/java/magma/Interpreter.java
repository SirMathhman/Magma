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

		// first, try to interpret as an addition expression
		Option<String> afterAdd = in.flatMap(trimmed -> {
			int plusIdx = trimmed.indexOf('+');
			if (plusIdx <= 0 || plusIdx >= trimmed.length() - 1)
				return Option.some(trimmed);
			String left = trimmed.substring(0, plusIdx).trim();
			String right = trimmed.substring(plusIdx + 1).trim();
			if (left.isEmpty() || right.isEmpty())
				return Option.none();

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

			Option<Num> L = parseToken.apply(left);
			Option<Num> R = parseToken.apply(right);
			if (L.isNone() || R.isNone())
				return Option.none();
			Num lnum = L.get();
			Num rnum = R.get();

			String resSuffix = "";
			if (!lnum.suffix.isEmpty() && !rnum.suffix.isEmpty()) {
				// both suffixed: must match
				if (!lnum.suffix.equals(rnum.suffix))
					return Option.none();
				resSuffix = lnum.suffix;
			} else if (!lnum.suffix.isEmpty() || !rnum.suffix.isEmpty()) {
				// one side is suffixed, the other is plain -> only I32 mixing allowed
				String present = !lnum.suffix.isEmpty() ? lnum.suffix : rnum.suffix;
				if (!"I32".equals(present))
					return Option.none();
				// mixing with plain yields a plain result (no suffix)
				resSuffix = "";
			}
			long sum = (long) lnum.value + (long) rnum.value;
			int sumInt = (int) sum;
			return Option.some(sumInt + resSuffix);
		});

		// then validate as plain or suffixed integer
		Option<String> finalOpt = afterAdd.flatMap(s -> {
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
