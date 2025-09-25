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
	public static Result<Tuple<String, Integer>, String> run(String source, String stdIn) {
		Compiler compiler = new Compiler();
		var result = compiler.compile(source);
		if (result instanceof Result.Ok<String, String> ok) {
			String stdout = ok.value();
			try {
				java.nio.file.Path dir = java.nio.file.Files.createTempDirectory("magma-run-");
				java.nio.file.Path file = dir.resolve("main.c");
				java.nio.file.Files.writeString(file, stdout, java.nio.charset.StandardCharsets.UTF_8);
				// return stdout and exit code 0
				return Result.ok(Tuple.of(stdout, 0));
			} catch (java.io.IOException e) {
				return Result.err("IO error writing temp file: " + e.getMessage());
			}
		} else if (result instanceof Result.Err<String, String> err) {
			return Result.err(String.valueOf(err.error()));
		} else {
			return Result.err("unknown");
		}
	}
}
