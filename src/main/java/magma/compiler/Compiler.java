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
		// Stubbed: no translation performed yet
		return new Result.Ok<>(new HashMap<>());
	}
}
