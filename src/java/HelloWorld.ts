/**
 * A simple Hello World program
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class HelloWorld {
	private HelloWorld() {}

	/**
	 * Main method that prints "Hello, World!" to the console
	 * and writes its own source code to HelloWorld.ts
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(final String[] args) {
		System.out.println("Hello, World!");
		
		try {
			// Get the path to this source file
			Path sourcePath = Paths.get("HelloWorld.java");
			
			// Read the content of this file
			String sourceCode = Files.readString(sourcePath);
			
			// Write the content to HelloWorld.ts
			Path targetPath = Paths.get("HelloWorld.ts");
			Files.writeString(targetPath, sourceCode);
			
			System.out.println("Source code written to HelloWorld.ts");
		} catch (IOException e) {
			System.err.println("Error writing source code to file: " + e.getMessage());
		}
	}
}