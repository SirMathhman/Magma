package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class Main {
	private static final String SOURCE_DIR = "src/java";
	private static final String TARGET_DIR = "src/node";
	private static final String JAVA_EXT = ".java";
	private static final String TS_EXT = ".ts";

	private Main() {}

	private static void processFile(final Path sourceFile) throws IOException {
		final var content = Files.readString(sourceFile);
		final var relativePath = Paths.get(Main.SOURCE_DIR).relativize(sourceFile);
		final var targetPath = Paths.get(Main.TARGET_DIR).resolve(relativePath.toString().replace(Main.JAVA_EXT, Main.TS_EXT));
		Files.createDirectories(targetPath.getParent());
		Files.writeString(targetPath, content);
	}

	public static void main(final String[] args) {
		try (final Stream<Path> paths = Files.walk(Paths.get(Main.SOURCE_DIR))) {
			paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(Main.JAVA_EXT)).forEach(file -> {
				try {
					Main.processFile(file);
				} catch (final IOException e) {
					System.err.println("Error processing file " + file + ": " + e.getMessage());
				}
			});
		} catch (final IOException e) {
			System.err.println("Error walking directory: " + e.getMessage());
		}
	}
}
