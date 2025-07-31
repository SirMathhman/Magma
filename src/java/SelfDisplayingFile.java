import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

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
public final class SelfDisplayingFile {
	private SelfDisplayingFile() {}

	private static void readAndDisplayFile(final Path sourcePath) throws IOException {
		try (final BufferedReader reader = new BufferedReader(
				new FileReader(sourcePath.toFile(), StandardCharsets.UTF_8))) {
			SelfDisplayingFile.displayFileContents(reader);
		}
	}

	private static void displayFileContents(final BufferedReader reader) throws IOException {
		String line = reader.readLine();
		while (null != line) {
			System.out.println(line);
			line = reader.readLine();
		}
	}

	public static void main(final String[] args) {
		try {
			final Path sourcePath = Paths.get("src", "java", "SelfDisplayingFile.java");

			System.out.println("=== Contents of " + sourcePath + " ===");
			System.out.println();

			SelfDisplayingFile.readAndDisplayFile(sourcePath);

			System.out.println();
			System.out.println("=== End of file contents ===");

		} catch (final IOException e) {
			System.err.println("Error reading the source file: " + e.getMessage());
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}