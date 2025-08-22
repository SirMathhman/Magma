import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Runner {
	// Counter to ensure each .c/.exe pair has a unique number for parallel runs
	private static final AtomicLong COUNTER = new AtomicLong();

	/**
	 * Previously this was the `main` method. It's renamed to `run` to accept a
	 * single
	 * String and return an int status code.
	 *
	 * @param input arbitrary string input
	 * @return 0 on success, non-zero on failure
	 */
	public static int run(String input) throws RunnerException {
		Compiler compiler = new Compiler();
		try {
			String result = compiler.compile(input);

			// Write the result into a temporary .c file
			try {
				long id = COUNTER.incrementAndGet();
				Path dir = Files.createTempDirectory("magma-runner-");
				try {
					Path temp = dir.resolve("magma-" + id + ".c");
					Files.writeString(temp, result, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

					// Build the temporary C file using clang. On Windows produce a .exe next to the
					// .c file with the same id so pairs are unique.
					String exeName = "magma-" + id + ".exe";
					Path exePath = dir.resolve(exeName);

					ProcessBuilder pb = new ProcessBuilder("clang", temp.toString(), "-o", exePath.toString());
					pb.redirectErrorStream(true);
					Process proc = pb.start();

					// Read combined stdout+stderr
					try (InputStream is = proc.getInputStream()) {
						// Wait with timeout to avoid blocking indefinitely
						boolean finished;
						try {
							finished = proc.waitFor(30, TimeUnit.SECONDS);
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
							proc.destroyForcibly();
							throw new RunnerException("Interrupted while waiting for clang", ie);
						}

						String output = new String(is.readAllBytes());

						if (!finished) {
							proc.destroyForcibly();
							throw new RunnerException("clang timed out and was killed. Output:\n" + output);
						}

						int exit = proc.exitValue();
						if (exit != 0) {
							throw new RunnerException("clang failed with exit code " + exit + ". Output:\n" + output);
						}

					}

					// Execute the produced executable and return its exit code
					try {
						ProcessBuilder runPb = new ProcessBuilder(exePath.toString());
						runPb.redirectErrorStream(true);
						Process runProc = runPb.start();
						try (InputStream runIs = runProc.getInputStream()) {
							boolean finishedRun;
							try {
								finishedRun = runProc.waitFor(30, TimeUnit.SECONDS);
							} catch (InterruptedException ie) {
								Thread.currentThread().interrupt();
								runProc.destroyForcibly();
								throw new RunnerException("Interrupted while waiting for executable to finish", ie);
							}

							String runOutput = new String(runIs.readAllBytes());

							if (!finishedRun) {
								runProc.destroyForcibly();
								throw new RunnerException("Executable timed out and was killed");
							}

							int runExit = runProc.exitValue();

							return runExit;
						}
					} catch (IOException ioEx) {
						throw new RunnerException("Failed to execute produced executable: " + ioEx.getMessage(), ioEx);
					}
				} finally {
					// Cleanup temp directory and its contents
					try {
						deleteRecursively(dir);
					} catch (IOException ignored) {
						// If cleanup fails, don't mask the main error; nothing to do
					}
				}
			} catch (IOException ioEx) {
				throw new RunnerException("Failed to write temporary file or run clang: " + ioEx.getMessage(), ioEx);
			}
		} catch (CompileException e) {
			throw new RunnerException("Compile failed: " + e.getMessage(), e);
		}
	}

	private static void deleteRecursively(Path path) throws IOException {
		if (Files.notExists(path))
			return;
		if (Files.isDirectory(path)) {
			try (var entries = Files.list(path)) {
				entries.forEach(p -> {
					try {
						deleteRecursively(p);
					} catch (IOException ignored) {
						// ignore
					}
				});
			}
		}
		Files.deleteIfExists(path);
	}

	// Small main wrapper so the class remains runnable during testing.
	public static void main(String[] args) {
		try {
			int status = run(args.length > 0 ? args[0] : "");
			System.exit(status);
		} catch (RunnerException re) {
			// Per requirement: do not print stack traces, only the message
			System.out.println(re.getMessage());
			System.exit(1);
		}
	}
}
