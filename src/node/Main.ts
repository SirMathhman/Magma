
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
export class Main {
	constructor() {}

	private static void readAndWriteFile(final Path sourcePath, final Path targetPath) throws IOException {
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
	}

	private static String processLine(final String line) {
		String trimmed = line.strip();
		// Handle class declarations
		if (trimmed.startsWith("public class") || trimmed.startsWith("public final class"))
			return line.replace("public final class", "export class").replace("public class", "export class");
		
		// Handle constructors - identify methods with the same name as the class and no return type
		if (trimmed.matches("private\\s+Main\\s*\\(.*\\).*")) {
			return line.replace("private Main", "constructor");
		}
		
		return line;
	}

	public static void main(final String[] args) {
		try {
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

			Main.readAndWriteFile(sourcePath, targetPath);

			System.out.println();
			System.out.println("=== End of file contents ===");
			System.out.println("File successfully written to " + targetPath);

		} catch (final IOException e) {
			System.err.println("Error reading/writing the file: " + e.getMessage());
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}