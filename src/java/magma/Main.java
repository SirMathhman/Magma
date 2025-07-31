package magma;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Java to TypeScript converter that processes all Java files in a directory and converts them to TypeScript.
 * <p>
 * How it works:
 * 1. The program recursively scans the source directory for Java files
 * 2. For each Java file, it creates a corresponding TypeScript file in the target directory
 * 3. It preserves the package structure by creating subdirectories in the target directory
 * 4. It converts Java syntax to TypeScript syntax
 * 5. It uses a Result interface with Ok and Err variants for error handling
 * <p>
 * To run this program:
 * 1. Compile: javac -d out\production\Magma src\java\magma\*.java
 * 2. Run: java -cp out\production\Magma magma.Main
 */
public final class Main {
	// No regex patterns needed

	private static final List<String> RETURN_TYPES =
			List.of(" void ", " int ", " String ", " boolean ", " double ", " float ", " long ", " char ", " byte ",
							" short ", " Object ");

	private Main() {}

	/**
	 * Reads a Java file, processes it to convert Java syntax to TypeScript, and writes the result to a TypeScript file.
	 * Uses the Result interface for error handling instead of exceptions.
	 *
	 * @param sourcePath the path to the Java source file
	 * @param targetPath the path where the TypeScript file will be written
	 * @return a Result containing the processed content if successful, or an Exception if an error occurred
	 */
	private static Result<String, Exception> readAndWriteFile(final Path sourcePath, final Path targetPath) {
		try {
			// Create parent directories if they don't exist
			Files.createDirectories(targetPath.getParent());

			// Read the source file
			String content = Files.readString(sourcePath, StandardCharsets.UTF_8);
			System.out.println(content);

			// Process the content: remove Java import statements and convert Java syntax to TypeScript
			content = content.lines()
											 .filter(line -> !line.strip().startsWith("import java."))
											 .map(Main::processLine)
											 .collect(Collectors.joining(System.lineSeparator()));

			// Write the processed content to the target file
			Files.writeString(targetPath, content, StandardCharsets.UTF_8);

			// Return a successful result with the processed content
			return Result.ok(content);
		} catch (final IOException e) {
			// Return an error result with the exception
			return Result.err(e);
		}
	}

	private static String processLine(final String line) {
		final String stripmed = line.strip();
		// Handle class declarations
		if (stripmed.startsWith("public class") || stripmed.startsWith("public final class"))
			return line.replace("public final class", "export class").replace("public class", "export class");

		// Handle constructors - identify methods with the same name as the class and no return type
		if (Main.isConstructorDeclaration(stripmed)) return line.replace("private magma.Main", "constructor");

		// Process method declarations
		final Optional<String> result = Main.processMethodDeclaration(line, stripmed);
		return result.isPresent() ? result.get() : line;
	}

	private static boolean isConstructorDeclaration(final String input) {
		if (!input.startsWith("private") || !input.contains("magma.Main(")) return false;
		return Main.RETURN_TYPES.stream().noneMatch(input::contains);
	}

	private static boolean isMethodDeclaration(final String line) {
		// Check if line starts with an access modifier
		if (!Main.hasAccessModifier(line)) return false;

		// Check if line contains parentheses (method parameters)
		if (!line.contains("(")) return false;

		// Check if line contains a return type and method name
		final var withoutModifier = Main.removeModifiers(line);

		// Find first space to check for return type and method name
		final int firstSpace = withoutModifier.indexOf(' ');
		return 0 <= firstSpace && withoutModifier.substring(firstSpace + 1).contains("(");
	}

	private static boolean hasAccessModifier(final String line) {
		return line.startsWith("public ") || line.startsWith("private ") || line.startsWith("protected ");
	}

	private static String removeModifiers(final String line) {
		String withoutModifier = line;
		if (line.startsWith("public ")) withoutModifier = line.substring("public ".length());
		else if (line.startsWith(
				"private ")) withoutModifier = line.substring("private ".length());
		else if (line.startsWith("protected ")) withoutModifier = line.substring("protected ".length());

		// Remove static and final if present
		if (withoutModifier.startsWith("static ")) withoutModifier = withoutModifier.substring("static ".length());
		if (withoutModifier.startsWith("final ")) withoutModifier = withoutModifier.substring("final ".length());
		return withoutModifier;
	}

	private static String handleParam(final String param) {
		if (param.isEmpty()) return "";

		String processedParam = param.strip();
		// Remove the 'final' keyword if present
		if (processedParam.startsWith("final ")) processedParam = processedParam.substring("final ".length()).strip();

		// Find the last space to separate type and name
		final int lastSpaceIndex = processedParam.lastIndexOf(' ');
		// If we can't parse it properly, keep it as is
		if (-1 == lastSpaceIndex) return processedParam;

		final String type = processedParam.substring(0, lastSpaceIndex);
		final String name = processedParam.substring(lastSpaceIndex + 1);

		// Convert Java types to TypeScript types
		final String tsType = Main.convertJavaTypeToTypeScript(type);

		// Return parameter in TypeScript format: name: type
		return name + ": " + tsType;
	}

	private static String convertParamsToTypeScript(final String params) {
		if (params.isBlank()) return "";

		// Split parameters by comma
		final String[] paramList = params.split(",");
		final StringBuilder result = new StringBuilder();

		final var length = paramList.length;
		for (int i = 0; i < length; i++) Main.extracted(paramList, i, result, length);

		return result.toString();
	}

	private static void extracted(final String[] paramList, final int i, final StringBuilder result, final int length) {
		final String processedParam = Main.handleParam(paramList[i]);
		if (processedParam.isEmpty()) return;

		result.append(processedParam);
		// Add comma if not the last parameter
		if (i < length - 1) result.append(", ");
	}

	private static String convertJavaTypeToTypeScript(final String javaType) {
		// For arrays
		if (javaType.endsWith("[]")) {
			final String baseType = javaType.substring(0, javaType.length() - 2);
			return Main.convertJavaTypeToTypeScript(baseType) + "[]";
		}
		// Return mapped type or original if not found
		return JavaType.getTypeScriptType(javaType);
	}

	private static int findMethodStart(final String line, final String methodName) {
		final int index = line.indexOf(methodName);
		if (-1 == index) return -1;

		// Verify this is actually the method name by checking what comes after it
		if (index + methodName.length() < line.length() && '(' == line.charAt(index + methodName.length())) return index;

		// Try to find the next occurrence
		return Main.findMethodStart(line.substring(index + 1), methodName);
	}

	private static String removeMethodModifiers(final String signature) {
		String result = signature;

		// Remove access modifiers (public, private, protected)
		if (result.startsWith("public ")) result = result.substring("public ".length());
		else if (result.startsWith(
				"private ")) result = result.substring("private ".length());
		else if (result.startsWith("protected ")) result = result.substring("protected ".length());

		// Remove static and final keywords
		if (result.startsWith("static ")) result = result.substring("static ".length());
		if (result.startsWith("final ")) result = result.substring("final ".length());

		return result;
	}

	private static Optional<String> extractMethodNameAndParams(final String methodSignature) {
		final int firstSpace = methodSignature.indexOf(' ');
		if (-1 == firstSpace) return Optional.empty();

		final String methodNameAndParams = methodSignature.substring(firstSpace + 1);
		final String methodName =
				methodNameAndParams.contains("(") ? methodNameAndParams.substring(0, methodNameAndParams.indexOf('('))
																					: methodNameAndParams;
		return Optional.of(methodName);
	}

	private static String createTypeScriptMethodSignature(final String line,
																												final String methodName,
																												final int methodStart) {
		String paramsSection = line.substring(methodStart + methodName.length());
		if (paramsSection.contains("(") && paramsSection.contains(")")) {
			final int openParenIndex = paramsSection.indexOf('(');
			final int closeParenIndex = paramsSection.indexOf(')', openParenIndex);

			if (closeParenIndex > openParenIndex) {
				final String params = paramsSection.substring(openParenIndex + 1, closeParenIndex);
				final String convertedParams = Main.convertParamsToTypeScript(params);
				paramsSection = "(" + convertedParams + ")" + paramsSection.substring(closeParenIndex + 1);
			}
		}
		return methodName + paramsSection;
	}

	private static Optional<String> processMethodDeclaration(final String line, final String stripmed) {
		if (!Main.isMethodDeclaration(stripmed)) return Optional.empty();

		final String methodSignature = Main.removeMethodModifiers(stripmed);
		final Optional<String> methodNameResult = Main.extractMethodNameAndParams(methodSignature);

		if (methodNameResult.isEmpty()) return Optional.empty();

		final String methodName = methodNameResult.get();
		final int methodStart = Main.findMethodStart(line, methodName);
		if (-1 == methodStart) return Optional.empty();

		final String typeScriptSignature = Main.createTypeScriptMethodSignature(line, methodName, methodStart);
		return Optional.of(typeScriptSignature);
	}

	/**
	 * Recursively processes all Java files in the source directory and converts them to TypeScript files in the target directory.
	 * Preserves the package structure by maintaining the same directory hierarchy in the target directory.
	 * Uses the Result interface for error handling.
	 *
	 * @param sourceDir the source directory containing Java files
	 * @param targetDir the target directory for TypeScript files
	 * @return a Result containing the number of processed files if successful, or an Exception if an error occurred
	 */
	private static Result<Void, Exception> processJavaFile(final Path sourcePath,
																												 final Path sourceDir,
																												 final Path targetDir) {
		// Calculate the relative path from the source directory to preserve package structure
		final Path relativePath = sourceDir.relativize(sourcePath);

		// Convert .java extension to .ts for the output file
		final String fileName = relativePath.getFileName().toString();
		final String tsFileName = fileName.substring(0, fileName.length() - 5) + ".ts";

		// Create the target path with the same package structure as the source
		// If the file is in the root of the source directory, put it in the root of the target directory
		// Otherwise, maintain the same subdirectory structure
		final Path targetPath = targetDir.resolve(
				null == relativePath.getParent() ? Paths.get(tsFileName) : relativePath.getParent().resolve(tsFileName));

		// Process the file and convert it to TypeScript
		final Result<String, Exception> result = Main.readAndWriteFile(sourcePath, targetPath);

		// If an error occurred during processing, propagate it up
		if (result.isErr()) return Result.err(result.getError());

		return Result.ok(null);
	}

	private static Result<Integer, Exception> processDirectory(final Path sourceDir, final Path targetDir) {
		try (final Stream<Path> paths = Files.walk(sourceDir)) {
			int processedFiles = 0;

			// Find all Java files in the source directory and its subdirectories
			final List<Path> javaFiles =
					paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();

			// Process each Java file
			for (final Path sourcePath : javaFiles) {
				final Result<Void, Exception> result = Main.processJavaFile(sourcePath, sourceDir, targetDir);
				if (result.isErr()) return Result.err(result.getError());
				processedFiles++;
			}

			// Return the number of successfully processed files
			return Result.ok(processedFiles);
		} catch (final IOException e) {
			// If an error occurred during directory walking, return it
			return Result.err(e);
		}
	}

	public static void main(final String[] args) {
		// Get the absolute path to the current working directory
		final Path currentDir = Paths.get("").toAbsolutePath();
		// Check if we're in the project root or in a subdirectory
		final Path projectRoot = currentDir.endsWith("java") ? currentDir.getParent().getParent() : currentDir;

		final Path sourceDir = projectRoot.resolve(Paths.get("src", "java"));
		final Path targetDir = projectRoot.resolve(Paths.get("src", "node"));

		System.out.println("=== Processing Java files from " + sourceDir + " to " + targetDir + " ===");
		System.out.println();

		final Result<Integer, Exception> result = Main.processDirectory(sourceDir, targetDir);

		if (result.isErr()) {
			final Exception e = result.getError();
			System.err.println("Error processing files: " + e.getMessage());
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		} else {
			System.out.println();
			System.out.println("=== Processing complete ===");
			System.out.println("Successfully processed " + result.getValue() + " files");
		}
	}
}