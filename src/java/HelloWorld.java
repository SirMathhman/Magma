import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HelloWorld {
	public static void main(String[] args) {
		System.out.println("Hello, World!");

		try {
			// Use content root as base directory
			Path projectRoot = Paths.get(".");

			// Create src/magma directory at the same level as src/java
			File magmaDirectory = new File(projectRoot.toString(), "src\\magma");
			magmaDirectory.mkdirs();

			// Create input file in src/magma
			Path inputFilePath = Paths.get(magmaDirectory.toString(), "Main.mg");
			if (!Files.exists(inputFilePath)) {
				// Create an empty input file if it doesn't exist
				Files.createFile(inputFilePath);
				System.out.println("Empty input file created at " + inputFilePath);
			}

			// Read the input file
			List<String> lines = Files.readAllLines(inputFilePath);

			// Check if the file is empty
			if (!lines.isEmpty()) {
				// File is not empty, throw an error
				throw new IOException("Input file is not empty. Cannot proceed.");
			}

			// File is empty, proceed with creating a C file with int main
			// Create src/windows directory at the same level as src/java
			File directory = new File(projectRoot.toString(), "src\\windows");
			directory.mkdirs();


			// Create C file with simple int main function
			Path filePath = Paths.get(directory.toString(), "Main.c");
			String cMainFunction = "int main() {\n    return 0;\n}";
			Files.write(filePath, cMainFunction.getBytes());
			System.out.println("C file with int main created at " + filePath);

			// Build the C program
			try {
				System.out.println("Building the C program...");
				ProcessBuilder processBuilder = new ProcessBuilder("clang", filePath.toString(), "-o",
																													 Paths.get(directory.toString(), "Main.exe").toString());
				processBuilder.redirectErrorStream(true);
				Process process = processBuilder.start();

				// Read the output from the process
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				StringBuilder output = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					output.append(line).append("\n");
				}

				// Wait for the process to complete
				int exitCode = process.waitFor();
				if (exitCode == 0) {
					System.out.println("C program built successfully.");
				} else {
					System.out.println("Failed to build C program. Exit code: " + exitCode);
					if (output.length() > 0) {
						System.out.println("Compiler output: " + output);
					}
				}
			} catch (Exception e) {
				System.err.println("Error building C program: " + e.getMessage());
			}
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}