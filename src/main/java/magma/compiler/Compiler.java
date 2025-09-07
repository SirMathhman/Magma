package magma.compiler;

import magma.model.Location;
import magma.util.Tuple;
import magma.util.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal Compiler stub. The real implementation is not required yet.
 * For now this returns an empty successful result.
 */
public class Compiler {
	public Result<Map<Location, Tuple<String, String>>, CompileError> compile(Map<Location, String> sources) {
		Map<Location, Tuple<String, String>> out = new HashMap<>();

		for (Map.Entry<Location, String> e : sources.entrySet()) {
			Location loc = e.getKey();

			String c = "/* Generated C for " + loc.toString() + " */\n"
					+ "#include <stdio.h>\n\n"
					+ "int main(void) {\n"
					+ "    return 0;\n"
					+ "}\n";

			String h = "/* header stub for " + loc.toString() + " */\n";

			out.put(loc, Tuple.of(c, h));
		}

		return new Result.Ok<>(out);
	}
}
