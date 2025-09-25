package magma;

/**
 * Test helper runner stub.
 */
public class Runner {

	/**
	 * Stub runner that "runs" source with provided stdin and returns stdout and
	 * exit code.
	 * Currently a placeholder that returns empty stdout and exit code 0.
	 *
	 * @param source the source input
	 * @param stdIn  stdin to provide to the run
	 * @return a Tuple of (stdout, exitCode)
	 */
	public static Tuple<String, Integer> run(String source, String stdIn) {
		Compiler compiler = new Compiler();
		var result = compiler.compile(source);
		if (result instanceof Result.Ok<String, String> ok) {
			return Tuple.of(ok.value(), 0);
		} else if (result instanceof Result.Err<String, String> err) {
			return Tuple.of(String.valueOf(err.error()), 1);
		} else {
			return Tuple.of("", 1);
		}
	}
}
