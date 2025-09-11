package magma;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Runtime value model for the interpreter. This sealed hierarchy provides
 * typed representations rather than string encodings. The Interpreter can
 * gradually migrate to using these types internally.
 */
public sealed interface Value
		permits Value.IntVal, Value.BoolVal, Value.ArrayVal, Value.RefVal, Value.StructVal, Value.UnitVal {
	/** Integer value (arbitrary precision). */
	record IntVal(BigInteger value) implements Value {
	}

	/** Boolean value. */
	record BoolVal(boolean value) implements Value {
	}

	/** Array value: an ordered list of values. */
	record ArrayVal(List<Value> elements) implements Value {
	}

	/** Reference value: refers to a variable by name; may be mutable. */
	record RefVal(String targetName, boolean mutable) implements Value {
	}

	/** Struct value: a named type with field values. */
	record StructVal(String typeName, Map<String, Value> fields) implements Value {
		public Value get(String field) {
			return fields.get(field);
		}
	}

	/** Unit/empty value (represents absence of a result). */
	enum UnitVal implements Value {
		INSTANCE
	}
}
