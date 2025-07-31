package magma;


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
export class Main {
	// No regex patterns needed

	private static final List<String> RETURN_TYPES =
			List.of(" void ", " int ", " String ", " boolean ", " double ", " float ", " long ", " char ", " byte ",
							" short ", " Object ");

	private Main() {}

Exception> readAndWriteFile(sourcePath: Path, targetPath: Path) {
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
			return Result.ok(content);
		} catch (final IOException e) {
			return Result.err(e);
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
		Result<String, Void> result = Main.processMethodDeclaration(line, stripmed);
		return result.isOk() ? result.getValue() : line;
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

Void> processMethodDeclaration(line: string, stripmed: string) {
		// Handle method declarations
		if (!Main.isMethodDeclaration(stripmed)) return Result.err(null);

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
		if (-1 == firstSpace) return Result.err(null);

		final String methodNameAndParams = methodSignature.substring(firstSpace + 1);
		final String methodName =
				methodNameAndParams.contains("(") ? methodNameAndParams.substring(0, methodNameAndParams.indexOf('('))
																					: methodNameAndParams;

		// Create TypeScript method signature
		final int methodStart = Main.findMethodStart(line, methodName);
		if (-1 == methodStart) return Result.err(null);

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

		return Result.ok(methodName + paramsSection);
	}

	/**
	 * Processes all Java files in the source directory and converts them to TypeScript files in the target directory.
	 * Preserves the package structure.
	 *
	 * @param sourceDir the source directory containing Java files
	 * @param targetDir the target directory for TypeScript files
	 * @return a Result containing the number of processed files or an exception if an error occurred
	 */
Exception> processDirectory(sourceDir: Path, targetDir: Path) {
		try (Stream<Path> paths = Files.walk(sourceDir)) {
			int processedFiles = 0;
			
			// Filter for Java files only
			List<Path> javaFiles = paths
					.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".java"))
					.collect(Collectors.toList());
			
			for (Path sourcePath : javaFiles) {
				// Calculate the relative path from the source directory
				Path relativePath = sourceDir.relativize(sourcePath);
				
				// Convert .java extension to .ts
				String fileName = relativePath.getFileName().toString();
				String tsFileName = fileName.substring(0, fileName.length() - 5) + ".ts";
				
				// Create the target path with the same package structure
				Path targetPath = targetDir.resolve(relativePath.getParent() == null ? 
						Paths.get(tsFileName) : 
						relativePath.getParent().resolve(tsFileName));
				
				// Process the file
				Result<String, Exception> result = Main.readAndWriteFile(sourcePath, targetPath);
				if (result.isErr()) {
					return Result.err(result.getError());
				}
				
				processedFiles++;
			}
			
			return Result.ok(processedFiles);
		} catch (IOException e) {
			return Result.err(e);
		}
	}

main(args: string[]) {
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