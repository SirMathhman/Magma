import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HelloWorld {
	/**
	 * Helper function that takes the content of the input file as a String and returns the output of the C program.
	 * 
	 * @param inputContent The content of the input file
	 * @return The output of the C program as a String
	 * @throws IOException If an I/O error occurs
	 * @throws InterruptedException If the process is interrupted
	 */
	public static String processCProgram(String inputContent) throws IOException, InterruptedException {
		// Use content root as base directory
		Path projectRoot = Paths.get(".");
		
		// Create src/windows directory at the same level as src/java
		File directory = new File(projectRoot.toString(), "src\\windows");
		directory.mkdirs();

		// Create C file with printf statement
		Path filePath = Paths.get(directory.toString(), "Main.c");
		String cMainFunction = "#include <stdio.h>\n\nint main() {\n    printf(\"Hello from C program! Current execution successful.\\n\");\n    return 0;\n}";
		Files.write(filePath, cMainFunction.getBytes());
		System.out.println("C file with int main created at " + filePath);

		// Build the C program
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
			
			// Run the compiled C program
			System.out.println("Running the C program...");
			Path exePath = Paths.get(directory.toString(), "Main.exe");
			ProcessBuilder runProcessBuilder = new ProcessBuilder(exePath.toString());
			runProcessBuilder.redirectErrorStream(true);
			Process runProcess = runProcessBuilder.start();
			
			// Read the output from the C program
			BufferedReader runReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
			String runLine;
			StringBuilder runOutput = new StringBuilder();
			while ((runLine = runReader.readLine()) != null) {
				runOutput.append(runLine).append("\n");
			}
			
			// Wait for the C program to complete
			int runExitCode = runProcess.waitFor();
			System.out.println("C program execution completed with exit code: " + runExitCode);
			System.out.println("C program output:");
			System.out.println(runOutput);
			
			return runOutput.toString();
		} else {
			System.out.println("Failed to build C program. Exit code: " + exitCode);
			if (output.length() > 0) {
				System.out.println("Compiler output: " + output);
			}
			return "Build failed with exit code: " + exitCode;
		}
	}

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
			
			// Convert lines to a single string (in this case it's empty)
			String inputContent = String.join("\n", lines);
			
			// Process the input content and get the C program output
			String cProgramOutput = processCProgram(inputContent);
			
			// Display the final output
			System.out.println("Final C program output:");
			System.out.println(cProgramOutput);
		} catch (IOException | InterruptedException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}