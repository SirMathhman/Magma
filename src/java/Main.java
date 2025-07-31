import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A simple file that displays its own contents.
 * When executed, this program reads its own source code and prints it to the console.
 * <p>
 * How it works:
 * 1. The program uses Paths.get() to create a relative path to this source file
 * 2. It opens the file using a BufferedReader and FileReader
 * 3. It reads the file line by line and prints each line to the console
 * 4. The program handles potential exceptions with a try-catch block
 * <p>
 * To run this program:
 * 1. Compile: javac -d out\production\Magma src\java\SelfDisplayingFile.java
 * 2. Run: java -cp out\production\Magma SelfDisplayingFile
 */
public final class Main {
	private static final Pattern PATTERN = Pattern.compile("private\\s+Main\\s*\\(.*\\).*");

	private Main() {}

	private static Optional<Exception> readAndWriteFile(final Path sourcePath, final Path targetPath) {
		try {
			// Create parent directories if they don't exist
			Files.createDirectories(targetPath.getParent());

			String content = Files.readString(sourcePath, StandardCharsets.UTF_8);
			System.out.println(content);

			// Remove Java import statements from the output
			content = content.lines()
											 .filter(line -> !line.strip().startsWith("import java."))
											 .map(Main::processLine)
											 .collect(Collectors.joining(System.lineSeparator()));

			Files.writeString(targetPath, content, StandardCharsets.UTF_8);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

	private static String processLine(final String line) {
		final String trimmed = line.strip();
		// Handle class declarations
		if (trimmed.startsWith("public class") || trimmed.startsWith("public final class"))
			return line.replace("public final class", "export class").replace("public class", "export class");

		// Handle constructors - identify methods with the same name as the class and no return type
		if (Main.PATTERN.matcher(trimmed).matches()) return line.replace("private Main", "constructor");

		return line;
	}

	public static void main(final String[] args) {
		// Get the absolute path to the current working directory
		final Path currentDir = Paths.get("").toAbsolutePath();
		// Check if we're in the project root or in a subdirectory
		final Path projectRoot = currentDir.endsWith("java") ? currentDir.getParent().getParent() : currentDir;

		final Path sourcePath = projectRoot.resolve(Paths.get("src", "java", "Main.java"));
		final Path targetPath = projectRoot.resolve(Paths.get("src", "node", "Main.ts"));

		System.out.println("=== Contents of " + sourcePath + " ===");
		System.out.println();
		System.out.println("Writing contents to " + targetPath);
		System.out.println();

		final Optional<Exception> exception = Main.readAndWriteFile(sourcePath, targetPath);

		if (exception.isPresent()) {
			final Exception e = exception.get();
			System.err.println("Error reading/writing the file: " + e.getMessage());
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		} else {
			System.out.println();
			System.out.println("=== End of file contents ===");
			System.out.println("File successfully written to " + targetPath);
		}
	}
}