package magma;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
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
	// No regex patterns needed

	private static final List<String> RETURN_TYPES =
			List.of(" void ", " int ", " String ", " boolean ", " double ", " float ", " long ", " char ", " byte ",
							" short ", " Object ");

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
		final String stripmed = line.strip();
		// Handle class declarations
		if (stripmed.startsWith("public class") || stripmed.startsWith("public final class"))
			return line.replace("public final class", "export class").replace("public class", "export class");

		// Handle constructors - identify methods with the same name as the class and no return type
		if (Main.isConstructorDeclaration(stripmed)) return line.replace("private magma.Main", "constructor");
		return Main.processMethodDeclaration(line, stripmed).orElse(line);
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
		for (int i = 0; i < length; i++) {
			final String processedParam = Main.handleParam(paramList[i]);
			if (!processedParam.isEmpty()) {
				result.append(processedParam);
				// Add comma if not the last parameter
				if (i < length - 1) result.append(", ");
			}
		}

		return result.toString();
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

	private static Optional<String> processMethodDeclaration(final String line, final String stripmed) {
		// Handle method declarations
		if (!Main.isMethodDeclaration(stripmed)) return Optional.empty();

		// Extract method name and parameters
		String methodSignature = stripmed;

		// Remove access modifiers (public, private, protected)
		if (methodSignature.startsWith("public ")) methodSignature = methodSignature.substring("public ".length());
		else if (methodSignature.startsWith("private ")) methodSignature = methodSignature.substring("private ".length());
		else if (methodSignature.startsWith("protected "))
			methodSignature = methodSignature.substring("protected ".length());

		// Remove static and final keywords
		if (methodSignature.startsWith("static ")) methodSignature = methodSignature.substring("static ".length());
		if (methodSignature.startsWith("final ")) methodSignature = methodSignature.substring("final ".length());

		// Extract return type and method name
		final int firstSpace = methodSignature.indexOf(' ');
		if (-1 == firstSpace) return Optional.empty();

		final String methodNameAndParams = methodSignature.substring(firstSpace + 1);
		final String methodName =
				methodNameAndParams.contains("(") ? methodNameAndParams.substring(0, methodNameAndParams.indexOf('('))
																					: methodNameAndParams;

		// Create TypeScript method signature
		final int methodStart = Main.findMethodStart(line, methodName);
		if (-1 == methodStart) return Optional.empty();

		// Get the parameters part and convert it to TypeScript format
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

		return Optional.of(methodName + paramsSection);
	}

	public static void main(final String[] args) {
		// Get the absolute path to the current working directory
		final Path currentDir = Paths.get("").toAbsolutePath();
		// Check if we're in the project root or in a subdirectory
		final Path projectRoot = currentDir.endsWith("java") ? currentDir.getParent().getParent() : currentDir;

		final Path sourcePath = projectRoot.resolve(Paths.get("src", "java", "Main.java"));
		final Path targetPath = projectRoot.resolve(Paths.get("src", "node", "magma.Main.ts"));

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