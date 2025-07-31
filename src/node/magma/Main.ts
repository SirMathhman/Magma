package magma;


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
export class Main {
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
readAndWriteFile(sourcePath: Path, targetPath: Path) {
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
			return Optional.empty();
		} catch (final IOException e) {
			// Return an error result with the exception
			return Optional.of(e);
		}
	}

processLine(line: string) {
		final String stripmed = line.strip();
		// Handle class declarations
		if (stripmed.startsWith("public class") || stripmed.startsWith("public final class"))
			return line.replace("public final class", "export class").replace("public class", "export class");

		// Handle constructors - identify methods with the same name as the class and no return type
		if (Main.isConstructorDeclaration(stripmed)) return line.replace("private magma.Main", "constructor");

		// Process method declarations
		return Main.processMethodDeclaration(line, stripmed).orElse(line);
	}

isConstructorDeclaration(input: string) {
		if (!input.startsWith("private") || !input.contains("magma.Main(")) return false;
		return Main.RETURN_TYPES.stream().noneMatch(input::contains);
	}


isMethodDeclaration(line: string) {
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

hasAccessModifier(line: string) {
		return line.startsWith("public ") || line.startsWith("private ") || line.startsWith("protected ");
	}

removeModifiers(line: string) {
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

handleParam(param: string) {
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

convertParamsToTypeScript(params: string) {
		if (params.isBlank()) return "";

		// Split parameters by comma
		final String[] paramList = params.split(",");
		final StringBuilder result = new StringBuilder();

		final var length = paramList.length;
		for (int i = 0; i < length; i++) Main.extracted(paramList, i, result, length);

		return result.toString();
	}

extracted(paramList: string[], i: number, result: StringBuilder, length: number) {
		final String processedParam = Main.handleParam(paramList[i]);
		if (processedParam.isEmpty()) return;

		result.append(processedParam);
		// Add comma if not the last parameter
		if (i < length - 1) result.append(", ");
	}

convertJavaTypeToTypeScript(javaType: string) {
		// For arrays
		if (javaType.endsWith("[]")) {
			final String baseType = javaType.substring(0, javaType.length() - 2);
			return Main.convertJavaTypeToTypeScript(baseType) + "[]";
		}
		// Return mapped type or original if not found
		return JavaType.getTypeScriptType(javaType);
	}

findMethodStart(line: string, methodName: string) {
		final int index = line.indexOf(methodName);
		if (-1 == index) return -1;

		// Verify this is actually the method name by checking what comes after it
		if (index + methodName.length() < line.length() && '(' == line.charAt(index + methodName.length())) return index;

		// Try to find the next occurrence
		return Main.findMethodStart(line.substring(index + 1), methodName);
	}

removeMethodModifiers(signature: string) {
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

extractMethodNameAndParams(methodSignature: string) {
		final int firstSpace = methodSignature.indexOf(' ');
		if (-1 == firstSpace) return Optional.empty();

		final String methodNameAndParams = methodSignature.substring(firstSpace + 1);
		final String methodName =
				methodNameAndParams.contains("(") ? methodNameAndParams.substring(0, methodNameAndParams.indexOf('('))
																					: methodNameAndParams;
		return Optional.of(methodName);
	}

createTypeScriptMethodSignature
																												final CharSequence methodName,
																												final int methodStart) {
		final String paramsSection = line.substring(methodStart + methodName.length());
		return methodName + Main.extractParamsSection(paramsSection).orElse("");
	}

extractParamsSection(paramsSection: string) {
		if (!paramsSection.contains("(") || !paramsSection.contains(")")) return Optional.empty();
		final int openParenIndex = paramsSection.indexOf('(');
		final int closeParenIndex = paramsSection.indexOf(')', openParenIndex);

		if (closeParenIndex <= openParenIndex) return Optional.empty();
		final String params = paramsSection.substring(openParenIndex + 1, closeParenIndex);
		final String convertedParams = Main.convertParamsToTypeScript(params);
		return Optional.of("(" + convertedParams + ")" + paramsSection.substring(closeParenIndex + 1));
	}

processMethodDeclaration(line: string, stripmed: string) {
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
	 * @param sourcePath the path to the Java source file
	 * @param directories the DirectoryPair containing source and target directories
	 * @return a Result containing the number of processed files if successful, or an Exception if an error occurred
	 */
processJavaFile(sourcePath: Path, directories: DirectoryPair) {
		// Calculate the relative path from the source directory to preserve package structure
		final Path relativePath = directories.sourceDir().relativize(sourcePath);

		// Convert .java extension to .ts for the output file
		final String fileName = relativePath.getFileName().toString();
		final String tsFileName = fileName.substring(0, fileName.length() - 5) + ".ts";

		// Create the target path with the same package structure as the source
		// If the file is in the root of the source directory, put it in the root of the target directory
		// Otherwise, maintain the same subdirectory structure
		final Path targetPath = directories.targetDir().resolve(
				null == relativePath.getParent() ? Paths.get(tsFileName) : relativePath.getParent().resolve(tsFileName));

		// Process the file and convert it to TypeScript
		return Main.readAndWriteFile(sourcePath, targetPath);
	}

	/**
	 * Recursively processes all Java files in the source directory and converts them to TypeScript files in the target directory.
	 * Preserves the package structure by maintaining the same directory hierarchy in the target directory.
	 * Uses the Result interface for error handling.
	 *
	 * @param directories the DirectoryPair containing source and target directories
	 * @return a Result containing the number of processed files if successful, or an Exception if an error occurred
	 */
IOException> processDirectory(directories: DirectoryPair) {
		try (final Stream<Path> paths = Files.walk(directories.sourceDir())) {
			// Find all Java files in the source directory and its subdirectories
			final List<Path> javaFiles =
					paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();

			Result<Integer, IOException> result = new Ok<>(0);

			// Process each Java file
			for (final Path sourcePath : javaFiles)
				result = Main.processFileAndUpdateResult(directories, sourcePath, result);

			// Return the number of successfully processed files
			return result;
		} catch (final IOException e) {
			// If an error occurred during directory walking, return it
			return new Err<>(e);
		}
	}

	/**
	 * Processes a single Java file and updates the result.
	 *
	 * @param directories the DirectoryPair containing source and target directories
	 * @param sourcePath the path to the Java source file
	 * @param result the current result
	 * @return an updated result
	 */
IOException> processFileAndUpdateResult
																																				 final Path sourcePath,
																																				 final Result<Integer, IOException> result) {
		final Optional<IOException> fileResult = Main.processJavaFile(sourcePath, directories);
		if (fileResult.isPresent()) return new Err<>(fileResult.get());
		return new Ok<>(result.getValue() + 1);
	}

main(args: string[]) {
		// Get the absolute path to the current working directory
		final Path currentDir = Paths.get("").toAbsolutePath();
		// Check if we're in the project root or in a subdirectory
		final Path projectRoot = currentDir.endsWith("java") ? currentDir.getParent().getParent() : currentDir;

		final Path sourceDir = projectRoot.resolve(Paths.get("src", "java"));
		final Path targetDir = projectRoot.resolve(Paths.get("src", "node"));
		
		// Create a DirectoryPair to encapsulate source and target directories
		final DirectoryPair directories = new DirectoryPair(sourceDir, targetDir);

		System.out.println("=== Processing Java files from " + sourceDir + " to " + targetDir + " ===");
		System.out.println();

		final Result<Integer, IOException> result = Main.processDirectory(directories);

		// Use pattern matching with instanceof for the Result type
		switch (result) {
			case final Err<Integer, IOException> err -> {
				final Exception e = err.getError();
				System.err.println("Error processing files: " + e.getMessage());
				//noinspection CallToPrintStackTrace
				e.printStackTrace();
			}
			case final Ok<Integer, IOException> ok -> {
				System.out.println();
				System.out.println("=== Processing complete ===");
				System.out.println("Successfully processed " + ok.getValue() + " files");
			}
		}
	}
}