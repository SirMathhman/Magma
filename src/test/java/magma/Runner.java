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
				// Build using clang; always produce main.exe in the temp directory
				String exeName = "main.exe";
				java.util.List<String> cmd = java.util.Arrays.asList("clang", file.getFileName().toString(), "-o", exeName);
				ProcessBuilder pb = new ProcessBuilder(cmd).directory(dir.toFile()).redirectErrorStream(true);
				Process compileProc = pb.start();
				String compileOutput = new String(compileProc.getInputStream().readAllBytes(),
						java.nio.charset.StandardCharsets.UTF_8);
				int compileExit = compileProc.waitFor();
				if (compileExit != 0) {
					return Result.err(compileOutput);
				}

				// Run the produced executable by absolute path so the file sits in the temp dir
				String exePath = dir.resolve(exeName).toAbsolutePath().toString();
				ProcessBuilder runPb = new ProcessBuilder(java.util.Arrays.asList(exePath)).directory(dir.toFile())
						.redirectErrorStream(true);
				Process runProc = runPb.start();
				// write stdIn to the process stdin
				if (stdIn != null && !stdIn.isEmpty()) {
					try (java.io.OutputStream os = runProc.getOutputStream()) {
						os.write(stdIn.getBytes(java.nio.charset.StandardCharsets.UTF_8));
						os.flush();
					}
				} else {
					runProc.getOutputStream().close();
				}
				String runOutput = new String(runProc.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
				int runExit = runProc.waitFor();
				return Result.ok(Tuple.of(runOutput, runExit));
			} catch (java.io.IOException e) {
				return Result.err("IO error writing temp file: " + e.getMessage());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return Result.err("Interrupted: " + e.getMessage());
			}
		} else if (result instanceof Result.Err<String, String> err) {
			return Result.err(String.valueOf(err.error()));
		} else {
			return Result.err("unknown");
		}
	}
}
