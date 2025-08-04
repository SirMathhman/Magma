package magma;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class Main {

	/**
	 * Helper function that takes the content of the input file as a String and returns the output of the C program.
	 * This is an overloaded method that calls processCProgram with an empty list of arguments.
	 * Files are generated in a temporary directory.
	 *
	 * @param inputContent The content of the input file
	 * @return The output of the C program as a String
	 * @throws IOException          If an I/O error occurs
	 * @throws InterruptedException If the process is interrupted
	 * @throws CompileException     If there is an error during compilation
	 */
	public static String processCProgram(String inputContent) throws IOException, InterruptedException, CompileException {
		return processCProgram(inputContent, Collections.emptyList());
	}

	/**
	 * Helper function that takes the content of the input file as a String and a list of process arguments,
	 * and returns the output of the C program.
	 * Files are generated in a temporary directory.
	 *
	 * @param inputContent The content of the input file
	 * @param args         The arguments to pass to the C program
	 * @return The output of the C program as a String
	 * @throws IOException          If an I/O error occurs
	 * @throws InterruptedException If the process is interrupted
	 * @throws CompileException     If there is an error during compilation
	 */
	public static String processCProgram(String inputContent, List<String> args)
			throws IOException, InterruptedException, CompileException {
		// Create a temporary directory for the files
		Path tempDirectory = Files.createTempDirectory("magma_");
		try {
			return processCProgram(inputContent, args, tempDirectory);
		} finally {
			// Clean up the temporary directory and its contents
			deleteDirectory(tempDirectory.toFile());
		}
	}

	/**
	 * Helper function that takes the content of the input file as a String, a list of process arguments,
	 * and a directory path, and returns the output of the C program.
	 *
	 * @param inputContent The content of the input file
	 * @param args         The arguments to pass to the C program
	 * @param directory    The directory where files will be generated
	 * @return The output of the C program as a String
	 * @throws IOException          If an I/O error occurs
	 * @throws InterruptedException If the process is interrupted
	 * @throws CompileException     If there is an error during compilation
	 */
	public static String processCProgram(String inputContent, List<String> args, Path directory)
			throws IOException, InterruptedException, CompileException {
		// Generate unique file names to avoid collisions between tests
		String uniqueId = System.currentTimeMillis() + "_" + Math.abs(java.util.UUID.randomUUID().hashCode());
		String cFileName = "Main_" + uniqueId + ".c";
		String exeFileName = "Main_" + uniqueId + ".exe";

		// Create C file with printf statement
		Path filePath = directory.resolve(cFileName);
		String cMainFunction = Compiler.generateCSourceCode(inputContent);
		Files.write(filePath, cMainFunction.getBytes());
		System.out.println("C file with int main created at " + filePath);

		// Build the C program
		System.out.println("Building the C program...");
		ProcessBuilder processBuilder =
				new ProcessBuilder("clang", filePath.toString(), "-o", directory.resolve(exeFileName).toString());
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

			// Run the compiled C program with arguments
			System.out.println("Running the C program...");
			Path exePath = directory.resolve(exeFileName);

			// Create process builder with executable path and arguments
			ProcessBuilder runProcessBuilder = new ProcessBuilder();
			runProcessBuilder.command().add(exePath.toString());
			runProcessBuilder.command().addAll(args);

			runProcessBuilder.redirectErrorStream(true);
			Process runProcess = runProcessBuilder.start();

			// Read the output from the C program
			BufferedReader runReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
			String runLine;
			StringBuilder runOutput = new StringBuilder();
			while ((runLine = runReader.readLine()) != null) {
				runOutput.append(runLine);
				// Only add newline if there are more lines to come
				if (runReader.ready()) {
					runOutput.append("\n");
				}
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
				System.out.println("magma.Compiler output: " + output);
			}
			throw new IOException("Failed to build C program. Exit code: " + exitCode);
		}
	}

	/**
	 * Recursively deletes a directory and all its contents.
	 *
	 * @param directory The directory to delete
	 * @return true if the directory was successfully deleted, false otherwise
	 */
	private static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
		return directory.delete();
	}

	public static void main(String[] args) {
		System.out.println("Hello, World!");

		try {
			// Create a temporary directory for Magma files
			Path tempDirectory = Files.createTempDirectory("magma_source_");
			try {
				// Create input file in the temporary directory
				Path inputFilePath = tempDirectory.resolve("magma.Main.mg");
				Files.createFile(inputFilePath);
				System.out.println("Empty input file created at " + inputFilePath);

				// The file is empty by default, so we can proceed
				String inputContent = "";

				// Process the input content and get the C program output
				// This will use another temporary directory for C files and executables
				String cProgramOutput = processCProgram(inputContent);

				// Display the final output
				System.out.println("Final C program output:");
				System.out.println(cProgramOutput);
			} finally {
				// Clean up the temporary directory and its contents
				deleteDirectory(tempDirectory.toFile());
			}
		} catch (CompileException e) {
			System.err.println("Compilation error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException | InterruptedException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}