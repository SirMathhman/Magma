package magma;

import magma.Option.None;
import magma.Option.Some;

import java.util.Objects;

public class Structs {
	public static Option<String> getFieldFromStructValue(String val, String fld) {
		if (Objects.isNull(val) || val.isEmpty() || !val.startsWith("STRUCT:"))
			return new None<>();
		var meta = val.substring(7);
		var needle = "|" + fld + "=";
		var pos = meta.indexOf(needle);
		if (pos < 0)
			return new None<>();
		var rest = meta.substring(pos + needle.length());
		var firstSegment = rest.split(";", 2)[0];
		var sval = firstSegment.split("#", 2)[0];
		return new Some<>(sval);
	}
}
