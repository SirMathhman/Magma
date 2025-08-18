package magma;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {
	public static int run(String input) throws IOException {
		final var output = Compiler.compile(input);
		final var temp = Files.createTempFile("main", ".c");
		Files.writeString(temp, output);

		// compile the temporary file and capture stdout/stderr
		final var process = new ProcessBuilder("clang", "-o", "main", temp.toString())
				.start();

		final var stdoutCollector = new ByteArrayOutputStream();
		final var stderrCollector = new ByteArrayOutputStream();

		Thread outThread = new Thread(() -> {
			try (InputStream is = process.getInputStream()) {
				is.transferTo(stdoutCollector);
			} catch (IOException ignored) {
			}
		});

		Thread errThread = new Thread(() -> {
			try (InputStream is = process.getErrorStream()) {
				is.transferTo(stderrCollector);
			} catch (IOException ignored) {
			}
		});

		outThread.start();
		errThread.start();

		int exitCode;
		try {
			exitCode = process.waitFor();
			outThread.join();
			errThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while waiting for clang", e);
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
			throw new IOException(msg.toString());
		}

		return 0;
	}
}
