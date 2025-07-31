package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;


/**
 * A class that handles the conversion of Java files to TypeScript files.
 * It encapsulates the source and target directories and provides methods
 * for processing Java files and converting them to TypeScript.
 *
 * @param sourceDir the source directory containing Java files
 * @param targetDir the target directory for TypeScript files
 */
record JavaToTypeScriptConverter(Path sourceDir, Path targetDir) {
	private static final List<String> RETURN_TYPES =
			List.of(" void ", " int ", " String ", " boolean ", " double ", " float ", " long ", " char ", " byte ",
							" short ", " Object ");

	/**
	 * Reads a Java file, processes it to convert Java syntax to TypeScript, and writes the result to a TypeScript file.
	 * Uses the Result interface for error handling instead of exceptions.
	 *
	 * @param sourcePath the path to the Java source file
	 * @param targetPath the path where the TypeScript file will be written
	 * @return a Result containing the processed content if successful, or an Exception if an error occurred
	 */
	private static Optional<IOException> readAndWriteFile((sourcePath: Path, targetPath: Path) {
		try {
			// Create parent directories if they don't exist
			Files.createDirectories(targetPath.getParent());

			// Read the source file
			String content = Files.readString(sourcePath, StandardCharsets.UTF_8);
			System.out.println(content);

			// Process the content: remove Java import statements and convert Java syntax to TypeScript
			content = content.lines()
											 .filter(line -> !line.strip().startsWith("import java."))
											 .map(JavaToTypeScriptConverter::processLine)
											 .collect(Collectors.joining(System.lineSeparator()));

			// Write the processed content to the target file
			Files.writeString(targetPath, content, StandardCharsets.UTF_8);

			// Return a successful result with the processed content
			return Optional.empty();
		} catch (final IOException e) {
			// Return an error result with the exception
			return Optional.of(e);
		}
	}

	private static String processLine((line: string) {
		final String stripmed = line.strip();
		// Handle class declarations
		if (stripmed.startsWith("public class") || stripmed.startsWith("public final class"))
			return line.replace("public final class", "export class").replace("public class", "export class");
		// Handle class with just final keyword (no access modifier)
		if (stripmed.startsWith("final class")) return line.replace("final class", "export class");
		// Handle class with no access modifier (package-private in Java)
		if (stripmed.startsWith("class")) return line.replace("class", "export class");

		// Handle constructors - identify methods with the same name as the class and no return type
		if (JavaToTypeScriptConverter.isConstructorDeclaration(stripmed))
			return line.replace("private magma.Main", "constructor");

		// Process method declarations
		return JavaToTypeScriptConverter.processMethodDeclaration(line, stripmed).orElse(line);
	}

	private static boolean isConstructorDeclaration((input: string) {
		if (!input.startsWith("private") || !input.contains("magma.Main(")) return false;
		return JavaToTypeScriptConverter.RETURN_TYPES.stream().noneMatch(input::contains);
	}

	private static boolean isMethodDeclaration((line: string) {
		// Check if line starts with an access modifier
		if (!JavaToTypeScriptConverter.hasAccessModifier(line)) return false;

		// Check if line contains parentheses (method parameters)
		if (!line.contains("(")) return false;

		// Check if line contains a return type and method name
		final var withoutModifier = JavaToTypeScriptConverter.removeModifiers(line);

		// Find first space to check for return type and method name
		final int firstSpace = withoutModifier.indexOf(' ');
		return 0 <= firstSpace && withoutModifier.substring(firstSpace + 1).contains("(");
	}

	private static boolean hasAccessModifier((line: string) {
		return line.startsWith("public ") || line.startsWith("private ") || line.startsWith("protected ");
	}

	private static String removeModifiers((line: string) {
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

	private static String handleParam((param: string) {
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
		final String tsType = JavaToTypeScriptConverter.convertJavaTypeToTypeScript(type);

		// Return parameter in TypeScript format: name: type
		return name + ": " + tsType;
	}

	private static String convertParamsToTypeScript((params: string) {
		if (params.isBlank()) return "";

		// Split parameters by comma
		final String[] paramList = params.split(",");
		final StringBuilder result = new StringBuilder();
		final int length = paramList.length;

		for (int i = 0; i < length; i++) JavaToTypeScriptConverter.extracted(paramList, i, result, length);

		return result.toString();
	}

	private static void extracted((paramList: String[], i: number, result: StringBuilder, length: number) {
		final String processedParam = JavaToTypeScriptConverter.handleParam(paramList[i]);
		result.append(processedParam);
		if (i < length - 1) result.append(", ");
	}

	private static String convertJavaTypeToTypeScript((javaType: string) {
		return switch (javaType) {
			case "int", "long", "float", "double", "byte", "short" -> "number";
			case "boolean" -> "boolean";
			case "char", "String" -> "string";
			case "void" -> "void";
			default -> javaType; // Keep other types as they are
		};
	}

	private static int findMethodStart((line: string, methodName: string) {
		return line.indexOf(methodName);
	}

	private static String removeMethodModifiers((signature: string) {
		// Remove access modifiers
		String result = signature;
		if (result.startsWith("public ")) result = result.substring("public ".length());
		else if (result.startsWith(
				"private ")) result = result.substring("private ".length());
		else if (result.startsWith("protected ")) result = result.substring("protected ".length());

		// Remove static and final if present
		if (result.startsWith("static ")) result = result.substring("static ".length());
		if (result.startsWith("final ")) result = result.substring("final ".length());

		// Remove return type
		final int firstSpace = result.indexOf(' ');
		if (0 <= firstSpace) result = result.substring(firstSpace + 1);

		return result;
	}

	private static Optional<String> extractMethodNameAndParams((methodSignature: string) {
		final int openParenIndex = methodSignature.indexOf('(');
		if (-1 == openParenIndex) return Optional.empty();

		return Optional.of(methodSignature.substring(0, openParenIndex + 1));
	}

	private static String createTypeScriptMethodSignature(final String line,
																												final CharSequence methodName,
																												final int methodStart) {
		final String beforeMethod = line.substring(0, methodStart);
		final String afterMethod = line.substring(methodStart);

		final int openParenIndex = afterMethod.indexOf('(');
		final int closeParenIndex = afterMethod.indexOf(')');

		if (-1 == openParenIndex || -1 == closeParenIndex) return line;

		final String paramsSection = afterMethod.substring(openParenIndex + 1, closeParenIndex);
		final Optional<String> tsParamsOpt = JavaToTypeScriptConverter.extractParamsSection(paramsSection);
		return tsParamsOpt.map(s -> beforeMethod + methodName + "(" + s + ")" + afterMethod.substring(closeParenIndex + 1))
											.orElse(line);

	}

	private static Optional<String> extractParamsSection((paramsSection: string) {
		if (paramsSection.isBlank()) return Optional.of("");
		return Optional.of(JavaToTypeScriptConverter.convertParamsToTypeScript(paramsSection));
	}

	private static Optional<String> processMethodDeclaration((line: string, stripmed: string) {
		if (!JavaToTypeScriptConverter.isMethodDeclaration(stripmed)) return Optional.empty();

		final String methodSignature = JavaToTypeScriptConverter.removeMethodModifiers(stripmed);
		final Optional<String> methodNameResult = JavaToTypeScriptConverter.extractMethodNameAndParams(methodSignature);

		if (methodNameResult.isEmpty()) return Optional.empty();

		final String methodName = methodNameResult.get();
		final int methodStart = JavaToTypeScriptConverter.findMethodStart(line, methodName);
		if (-1 == methodStart) return Optional.empty();

		final String typeScriptSignature =
				JavaToTypeScriptConverter.createTypeScriptMethodSignature(line, methodName, methodStart);
		return Optional.of(typeScriptSignature);
	}

	/**
	 * Recursively processes all Java files in the source directory and converts them to TypeScript files in the target directory.
	 * Preserves the package structure by maintaining the same directory hierarchy in the target directory.
	 * Removes files in the target directory that weren't generated or updated in the current run to keep it clean.
	 * Uses the Result interface for error handling.
	 *
	 * @return a Result containing the number of processed files if successful, or an Exception if an error occurred
	 */
	public Result<Integer, IOException> processDirectory(() {
		// Set to track all generated/updated TypeScript files
		final Set<Path> generatedFiles = new HashSet<>();

		try (final Stream<Path> paths = Files.walk(this.sourceDir)) {
			// Find all Java files in the source directory and its subdirectories
			final List<Path> javaFiles =
					paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();

			Result<Integer, IOException> result = new Ok<>(0);

			// Process each Java file and track generated files
			for (final Path sourcePath : javaFiles) {
				// Calculate the target TypeScript file path (similar to processJavaFile)
				final Path relativePath = this.sourceDir.relativize(sourcePath);
				final String fileName = relativePath.getFileName().toString();
				final String tsFileName = fileName.substring(0, fileName.length() - 5) + ".ts";
				final Path targetPath = this.targetDir.resolve(
						null == relativePath.getParent() ? Paths.get(tsFileName) : relativePath.getParent().resolve(tsFileName));

				// Process the Java file
				result = this.processFileAndUpdateResult(sourcePath, result);

				// If successful, add the target path to the set of generated files
				// If there was an error, return it
				if (result instanceof Ok) generatedFiles.add(targetPath);
				else return result;
			}

			// Remove "dead" files (files that weren't generated or updated in this run)
			try {
				this.removeDeadFiles(generatedFiles);
			} catch (final IOException e) {
				return new Err<>(e);
			}

			// Return the number of successfully processed files
			return result;
		} catch (final IOException e) {
			// If an error occurred during directory walking, return it
			return new Err<>(e);
		}
	}

	/**
	 * Removes files in the target directory that weren't generated or updated in the current run.
	 *
	 * @param generatedFiles the set of files that were generated or updated in the current run
	 * @throws IOException if an error occurs while walking the directory or deleting files
	 */
	private void removeDeadFiles((generatedFiles: Set<Path>) throws IOException {
		try (final Stream<Path> targetPaths = Files.walk(this.targetDir)) {
			// Find all TypeScript files in the target directory and its subdirectories
			final List<Path> tsFiles =
					targetPaths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".ts")).toList();

			// Remove files that weren't generated or updated in this run
			for (final Path tsFile : tsFiles)
				if (!generatedFiles.contains(tsFile)) {
					System.out.println("Removing dead file: " + tsFile);
					Files.delete(tsFile);
				}
		}
	}

	/**
	 * Processes a single Java file and updates the result.
	 *
	 * @param sourcePath the path to the Java source file
	 * @param result     the current result
	 * @return an updated result
	 */
	private Result<Integer, IOException> processFileAndUpdateResult(final Path sourcePath,
																																	final Result<Integer, IOException> result) {
		final Optional<IOException> fileResult = this.processJavaFile(sourcePath);
		if (fileResult.isPresent()) return new Err<>(fileResult.get());
		return new Ok<>(result.value() + 1);
	}

	/**
	 * Processes a single Java file and converts it to TypeScript.
	 * Preserves the package structure by maintaining the same directory hierarchy in the target directory.
	 *
	 * @param sourcePath the path to the Java source file
	 * @return an Optional containing an IOException if an error occurred, or empty if successful
	 */
	private Optional<IOException> processJavaFile((sourcePath: Path) {
		// Calculate the relative path from the source directory to preserve package structure
		final Path relativePath = this.sourceDir.relativize(sourcePath);

		// Convert .java extension to .ts for the output file
		final String fileName = relativePath.getFileName().toString();
		final String tsFileName = fileName.substring(0, fileName.length() - 5) + ".ts";

		// Create the target path with the same package structure as the source
		// If the file is in the root of the source directory, put it in the root of the target directory
		// Otherwise, maintain the same subdirectory structure
		final Path targetPath = this.targetDir.resolve(
				null == relativePath.getParent() ? Paths.get(tsFileName) : relativePath.getParent().resolve(tsFileName));

		// Process the file and convert it to TypeScript
		return JavaToTypeScriptConverter.readAndWriteFile(sourcePath, targetPath);
	}
}