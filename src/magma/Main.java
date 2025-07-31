package magma;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
 * 1. Compile: javac -d out\production\Magma src\magma\Main.java
 * 2. Run: java -cp out\production\Magma magma.Main
 */
public final class Main {
	private Main() {}

	private static void readAndWriteFile(final Path sourcePath, final Path targetPath) throws IOException {
		// Create parent directories if they don't exist
		Files.createDirectories(targetPath.getParent());

		try (final BufferedReader reader = new BufferedReader(new FileReader(sourcePath.toFile(), StandardCharsets.UTF_8));
				 final BufferedWriter writer = new BufferedWriter(
						 new FileWriter(targetPath.toFile(), StandardCharsets.UTF_8))) {

			Main.displayAndWriteFileContents(reader, writer);
		}
	}

	private static void displayAndWriteFileContents(final BufferedReader reader, final BufferedWriter writer)
			throws IOException {
		String line = reader.readLine();
		while (null != line) {
			System.out.println(line);
			writer.write(line);
			writer.newLine();
			line = reader.readLine();
		}
	}

	public static void main(final String[] args) {
		try {
			final Path sourcePath = Paths.get("src", "magma", "Main.java");
			final Path targetPath = Paths.get("src", "node", "Main.ts");

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