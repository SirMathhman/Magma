package magma;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Application {
	public static int run(String input) {
		// fast-path: if there's no input, nothing to compile or run
		if (input == null || input.isEmpty()) {
			return 0;
		}
		try {
			final var output = Compiler.compile(input);
			final var temp = Files.createTempFile("main", ".c");
			Files.writeString(temp, output);

			// create a temporary executable in the temp directory and compile into it
			String os = System.getProperty("os.name").toLowerCase();
			final var exe = Files.createTempFile("main", os.contains("win") ? ".exe" : null);
			// on POSIX systems try to make it executable
			try {
				if (!os.contains("win")) {
					Files.setPosixFilePermissions(exe, PosixFilePermissions.fromString("rwxr-xr-x"));
				}
			} catch (Exception ignored) {
			}

			// compile the temporary file and capture stdout/stderr
			final var process = new ProcessBuilder("clang", "-o", exe.toString(), temp.toString())
					.start();

			final var stdoutCollector = new ByteArrayOutputStream();
			final var stderrCollector = new ByteArrayOutputStream();

			ExecutorService exec = Executors.newFixedThreadPool(2);
			Future<?> outFuture = exec.submit(() -> {
				try (InputStream is = process.getInputStream()) {
					is.transferTo(stdoutCollector);
				} catch (IOException ignored) {
				}
			});

			Future<?> errFuture = exec.submit(() -> {
				try (InputStream is = process.getErrorStream()) {
					is.transferTo(stderrCollector);
				} catch (IOException ignored) {
				}
			});

			int exitCode;
			try {
				exitCode = process.waitFor();
				outFuture.get();
				errFuture.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("Interrupted while waiting for clang", e);
			} catch (ExecutionException e) {
				throw new RuntimeException("Failed while reading clang output", e.getCause());
			} finally {
				exec.shutdownNow();
				try {
					exec.awaitTermination(1, TimeUnit.SECONDS);
				} catch (InterruptedException ignored) {
					Thread.currentThread().interrupt();
				}
			}

			String stdout = stdoutCollector.toString(StandardCharsets.UTF_8);
			String stderr = stderrCollector.toString(StandardCharsets.UTF_8);

			if (exitCode != 0) {
				StringBuilder msg = new StringBuilder();
				msg.append("clang failed with exit code ").append(exitCode);
				if (!stdout.isEmpty()) {
					msg.append("\n--- stdout ---\n").append(stdout);
				}
				if (!stderr.isEmpty()) {
					msg.append("\n--- stderr ---\n").append(stderr);
				}
				throw new RuntimeException(msg.toString());
			}

			// run the generated executable (the temp exe) and return its exit code
			final var runProcessBuilder = new ProcessBuilder(exe.toString())
					.inheritIO();
			final Process runProcess;
			try {
				runProcess = runProcessBuilder.start();
			} catch (IOException e) {
				throw new RuntimeException("Failed to start generated executable", e);
			}

			int programExit;
			try {
				programExit = runProcess.waitFor();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("Interrupted while waiting for executed program", e);
			}

			return programExit;
		} catch (IOException e) {
			throw new RuntimeException("I/O failure while running application", e);
		}
	}
}
