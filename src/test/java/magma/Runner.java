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
		// TODO: implement real execution
		return Tuple.of("", 0);
	}
}
