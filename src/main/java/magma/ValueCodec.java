package magma;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Bridge between the existing string encodings used by Interpreter and the
 * typed Value hierarchy. This allows incremental migration: Interpreter can
 * keep returning strings for now, while internal logic can adopt Value.
 */
final class ValueCodec {
	private static final String REF_PREFIX = "@REF:";
	private static final String REFMUT_PREFIX = "@REFMUT:";
	private static final String ARR_PREFIX = "@ARR:";
	private static final String STR_PREFIX = "@STR:";

	private ValueCodec() {
	}

	static Value fromEncoded(String encoded) {
		Objects.requireNonNull(encoded, "encoded");
		// booleans
		if (encoded.equals("true"))
			return new Value.BoolVal(true);
		if (encoded.equals("false"))
			return new Value.BoolVal(false);
		// references
		if (encoded.startsWith(REFMUT_PREFIX)) {
			return new Value.RefVal(encoded.substring(REFMUT_PREFIX.length()), true);
		}
		if (encoded.startsWith(REF_PREFIX)) {
			return new Value.RefVal(encoded.substring(REF_PREFIX.length()), false);
		}
		// arrays
		if (encoded.startsWith(ARR_PREFIX)) {
			String rest = encoded.substring(ARR_PREFIX.length());
			List<Value> vals = new ArrayList<>();
			if (!rest.isEmpty()) {
				for (String p : rest.split("\\|"))
					vals.add(fromEncoded(p));
			}
			return new Value.ArrayVal(vals);
		}
		// structs: STR_PREFIX + Type|field=val|...
		if (encoded.startsWith(STR_PREFIX)) {
			String payload = encoded.substring(STR_PREFIX.length());
			String[] parts = payload.split("\\|");
			String type = parts.length > 0 ? parts[0] : "";
			Map<String, Value> fields = new HashMap<>();
			for (int i = 1; i < parts.length; i++) {
				String part = parts[i];
				int eq = part.indexOf('=');
				if (eq > 0) {
					String name = part.substring(0, eq);
					String val = part.substring(eq + 1);
					fields.put(name, fromEncoded(val));
				}
			}
			return new Value.StructVal(type, fields);
		}
		// unit/empty string
		if (encoded.isEmpty())
			return Value.UnitVal.INSTANCE;
		// default: parse as integer
		try {
			return new Value.IntVal(new BigInteger(encoded));
		} catch (NumberFormatException ex) {
			// Fallback to unit to avoid throwing in bridge; Interpreter should avoid
			// passing malformed encodings here.
			return Value.UnitVal.INSTANCE;
		}
	}

	static String toEncoded(Value v) {
		if (v instanceof Value.BoolVal b)
			return b.value() ? "true" : "false";
		if (v instanceof Value.IntVal i)
			return i.value().toString();
		if (v instanceof Value.RefVal r)
			return (r.mutable() ? REFMUT_PREFIX : REF_PREFIX) + r.targetName();
		if (v instanceof Value.ArrayVal a) {
			List<String> enc = new ArrayList<>();
			for (Value e : a.elements())
				enc.add(toEncoded(e));
			return ARR_PREFIX + String.join("|", enc);
		}
		if (v instanceof Value.StructVal s) {
			StringBuilder sb = new StringBuilder();
			sb.append(s.typeName());
			for (Map.Entry<String, Value> e : s.fields().entrySet()) {
				sb.append('|').append(e.getKey()).append('=').append(toEncoded(e.getValue()));
			}
			return STR_PREFIX + sb;
		}
		// Unit
		return "";
	}
}
