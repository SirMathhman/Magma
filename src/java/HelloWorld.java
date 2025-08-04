import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HelloWorld {
	public static void main(String[] args) {
		System.out.println("Hello, World!");

		try {
			// Get the project root directory (one level up from src)
			Path currentPath = Paths.get(System.getProperty("user.dir"));
			Path projectRoot = currentPath;
			if (currentPath.toString().endsWith("\\src\\java")) {
				projectRoot = currentPath.getParent().getParent();
			}

			// Create src/magma directory at the same level as src/java
			File magmaDirectory = new File(projectRoot.toString(), "src\\magma");
			magmaDirectory.mkdirs();

			// Create input file in src/magma
			Path inputFilePath = Paths.get(magmaDirectory.toString(), "input.txt");
			if (!Files.exists(inputFilePath)) {
				// Create an empty input file if it doesn't exist
				Files.createFile(inputFilePath);
				System.out.println("Empty input file created at " + inputFilePath);
			}

			// Read the input file
			List<String> lines = Files.readAllLines(inputFilePath);

			// Check if the file is empty
			if (lines.isEmpty()) {
				// File is empty, proceed with creating a C file with int main
				// Create src/windows directory at the same level as src/java
				File directory = new File(projectRoot.toString(), "src\\windows");
				directory.mkdirs();

				// Ensure we don't have nested src/java/src/windows
				File nestedDir = new File(currentPath.toString(), "src\\windows");
				if (nestedDir.exists()) {
					// Delete the nested directory and its contents
					File[] files = nestedDir.listFiles();
					if (files != null) {
						for (File file : files) {
							file.delete();
						}
					}
					nestedDir.delete();
				}

				// Create C file with simple int main function
				Path filePath = Paths.get(directory.toString(), "Main.c");
				String cMainFunction = "int main() {\n    return 0;\n}";
				Files.write(filePath, cMainFunction.getBytes());
				System.out.println("C file with int main created at " + filePath);
			} else {
				// File is not empty, throw an error
				throw new IOException("Input file is not empty. Cannot proceed.");
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}