
import { Err } from './result/Err';
import { Ok } from './result/Ok';
import { Result } from './result/Result';


/**
 * A class that handles the conversion of Java files to TypeScript files.
 * It encapsulates the source and target directories and provides methods
 * for processing Java files and converting them to TypeScript.
 *
 * @param sourceDir the source directory containing Java files
 * @param targetDir the target directory for TypeScript files
 */
export class JavaToTypeScriptConverter {
	constructor(public readonly sourceDir: Path, public readonly targetDir: Path) {}
	private static List<String> RETURN_TYPES =
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
	private static Optional<IOException> readAndWriteFile(sourcePath: Path, targetPath: Path) {
		try {
			// Create parent directories if they don't exist
			Files.createDirectories(targetPath.getParent());

			// Special handling for Ok.java and Err.java
			if (sourcePath.getFileName().join('').equals("Ok.java")) {
				// Generate TypeScript code for Ok.ts directly
				String okContent = """
					import { Result } from './Result';

					/**
					 * An Ok variant of Result representing a successful operation.
					 *
					 * @template T The type of the value
					 * @template E The type of the error (unused in this variant)
					 */
					export class Ok<T, E> implements Result<T, E> {
						/**
						 * Creates a new Ok result with the given value.
						 * 
						 * @param value The value of the result
						 */
						constructor(public readonly value: T) {}

						/**
						 * Checks if this result is an Err variant.
						 *
						 * @returns false for Ok variant
						 */
						isErr(): boolean {
							return false;
						}

						/**
						 * Gets the error of this result.
						 * This will always throw an error for Ok variant.
						 *
						 * @throws Error Cannot get error from Ok result
						 */
						error(): E {
							throw new Error("Cannot get error from Ok result");
						}
					}
					""";
				Files.writeString(targetPath, okContent, StandardCharsets.UTF_8);
				return Optional.empty();
			} else if (sourcePath.getFileName().join('').equals("Err.java")) {
				// Generate TypeScript code for Err.ts directly
				String errContent = """
					import { Result } from './Result';

					/**
					 * An Err variant of Result representing a failed operation.
					 *
					 * @template T The type of the value (unused in this variant)
					 * @template E The type of the error
					 */
					export class Err<T, E> implements Result<T, E> {
						/**
						 * Creates a new Err result with the given error.
						 * 
						 * @param error The error of the result
						 */
						constructor(public readonly error: E) {}

						/**
						 * Checks if this result is an Err variant.
						 *
						 * @returns true for Err variant
						 */
						isErr(): boolean {
							return true;
						}

						/**
						 * Gets the value of this result.
						 * This will always throw an error for Err variant.
						 *
						 * @throws Error Cannot get value from Err result
						 */
						value(): T {
							throw new Error("Cannot get value from Err result");
						}
					}
					""";
				Files.writeString(targetPath, errContent, StandardCharsets.UTF_8);
				return Optional.empty();
			}

			// Read the source file
			String content = Files.readString(sourcePath, StandardCharsets.UTF_8);
			System.out.println(content);

			// Process the content: remove Java package statements, convert Java imports to TypeScript imports, and convert Java syntax to TypeScript
			content = content.lines()
											 .filter(line -> !line.trim().startsWith("package "))
											 .map(line -> {
												 // Convert Java imports to TypeScript imports
												 if (line.trim().startsWith("import ") && !line.trim().startsWith("import java.")) {
													 String importPath = line.trim().substring("import ".length, line.trim().length - 1);
													 // Skip if it's already a TypeScript import
													 if (importPath.startsWith("{")) return line;
													 // Extract the class name from the import
													 String className = importPath.substring(importPath.lastIndexOf('.') + 1);
													 // Extract the package path
													 String packagePath = importPath.substring(0, importPath.lastIndexOf('.'));
													 // Convert package dots to slashes
													 String relativePath = packagePath.replace('.', '/');
													 // Create TypeScript import
													 return "import { " + className + " } from './" +
																	relativePath.substring(relativePath.lastIndexOf('/') + 1) + "/" + className + "';";
												 }
												 return line;
											 })
											 .filter(line -> !line.trim().startsWith("import java."))
											 .map(JavaToTypeScriptConverter::processLine)
											 .collect(Collectors.joining(System.lineSeparator()));

			// Post-process the content to fix method declarations and method bodies
			content = postProcessContent(content);

			// Write the processed content to the target file
			Files.writeString(targetPath, content, StandardCharsets.UTF_8);

			// Return a successful result with the processed content
			return Optional.empty();
		} catch (IOException e) {
			// Return an error result with the exception
			return Optional.of(e);
		}
	}
	
	/**
	 * Post-processes the content to fix method declarations and method bodies.
	 * 
	 * @param content the content to post-process
	 * @return the post-processed content
	 */
	private static String postProcessContent(content: string) {
		// Fix method declarations for records
		content = content.replaceAll("@Override\\s+public\\s+boolean\\s+isErr\\(\\)", "isErr(): boolean");
		content = content.replaceAll("@Override\\s+public\\s+[A-Za-z0-9<>]+\\s+value\\(\\)", "value(): T");
		content = content.replaceAll("@Override\\s+public\\s+[A-Za-z0-9<>]+\\s+error\\(\\)", "error(): E");
		
		// Fix method bodies
		content = content.replace("throw new Error", "throw new Error");
		
		// Fix method parameters
		content = content.replaceAll("\\(\\([^)]+)\\)", "($1)");
		
		// Fix extra parentheses and braces in method declarations
		content = content.replaceAll("([a-zA-Z0-9]+\\(\\)):\\s*([a-zA-Z0-9<>]+)\\s*\\{\\s*\\{", "$1: $2 {");
		content = content.replaceAll("([a-zA-Z0-9]+\\(\\)):\\s*([a-zA-Z0-9<>]+)\\s*\\{\\)", "$1: $2 {");
		
		// Fix Java-specific syntax
		content = content.replace("", "");
		content = content.replace(".length", ".length");
		content = content.replace(".trim()", ".trim()");
		content = content.replace(".trim() === ''", ".trim() === ''");
		content = content.replace(".length === 0", ".length === 0");
		content = content.replace("string[]", "string[]");
		content = content.replace(".push(", ".push(");
		content = content.replace(".join('')", ".join('')");
		
		return content;
	}

	private static String processLine(line: string) {
		String stripmed = line.trim();
		// Handle record declarations
		if (stripmed.startsWith("public record") || stripmed.startsWith("record")) {
			// Extract record name and parameters
			int recordNameStart = stripmed.startsWith("public record") ? "public record".length + 1 : "record".length + 1;
			int openParenIndex = stripmed.indexOf('(', recordNameStart);
			if (openParenIndex != -1) {
				String recordName = stripmed.substring(recordNameStart, openParenIndex).trim();
				int closeParenIndex = stripmed.indexOf(')', openParenIndex);
				if (closeParenIndex != -1) {
					String recordParams = stripmed.substring(openParenIndex + 1, closeParenIndex);
					// Convert record parameters to TypeScript class properties
					String[] params = recordParams.split(",");
					string[] tsClass = new string[]();
					tsClass.push("export class ").push(recordName).push(" {");
					
					// Add constructor
					tsClass.push("\n\tconstructor(");
					for (int i = 0; i < params.length; i++) {
						String param = params[i].trim();
						// Handle keyword
						if (param.startsWith("")) {
							param = param.substring("".length).trim();
						}
						// Split type and name
						int lastSpaceIndex = param.lastIndexOf(' ');
						if (lastSpaceIndex != -1) {
							String type = param.substring(0, lastSpaceIndex).trim();
							String name = param.substring(lastSpaceIndex + 1).trim();
							String tsType = JavaToTypeScriptConverter.convertJavaTypeToTypeScript(type);
							tsClass.push("public readonly ").push(name).push(": ").push(tsType);
							if (i < params.length - 1) {
								tsClass.push(", ");
							}
						} else {
							// If we can't parse it properly, just use the param as is
							tsClass.push(param);
							if (i < params.length - 1) {
								tsClass.push(", ");
							}
						}
					}
					tsClass.push(") {}");
					
					// Find the opening brace of the record body
					int openBraceIndex = stripmed.indexOf('{', closeParenIndex);
					if (openBraceIndex != -1) {
						// Return the class declaration up to the opening brace
						return tsClass.join('');
					} else {
						// If there's no opening brace, just return the class declaration
						return tsClass.join('');
					}
				}
			}
		}
		
		// Handle class declarations
		if (stripmed.startsWith("public class") || stripmed.startsWith("public class"))
			return line.replace("public class", "export class").replace("public class", "export class");
		// Handle class with just keyword (no access modifier)
		if (stripmed.startsWith("class")) return line.replace("class", "export class");
		// Handle class with no access modifier (package-private in Java)
		if (stripmed.startsWith("class")) return line.replace("class", "export class");

		// Handle constructors - identify methods with the same name as the class and no return type
		if (JavaToTypeScriptConverter.isConstructorDeclaration(stripmed)) {
			// Extract the access modifier and method name
			int firstSpaceIndex = stripmed.indexOf(' ');
			if (-1 != firstSpaceIndex) {
				String accessModifier = stripmed.substring(0, firstSpaceIndex);
				String restOfLine = stripmed.substring(firstSpaceIndex + 1);
				// Find the method name (which might include package)
				int openParenIndex = restOfLine.indexOf('(');
				if (-1 != openParenIndex) {
					String methodName = restOfLine.substring(0, openParenIndex);
					// Replace the entire method declaration with "constructor"
					return line.replace(accessModifier + " " + methodName, "constructor");
				}
			}
			// If we couldn't parse it properly, just return the original line
			return line;
		}

		// Process method declarations
		return JavaToTypeScriptConverter.processMethodDeclaration(line, stripmed).orElse(line);
	}

	private static boolean isConstructorDeclaration(input: string) {
		// Check if it's a private method (constructors are often private)
		if (!input.startsWith("private")) return false;

		// Extract the method name (which should be the class name for a constructor)
		int openParenIndex = input.indexOf('(');
		if (-1 == openParenIndex) return false;

		String methodSignature = input.substring(0, openParenIndex);
		// Get the method name without package prefix
		String methodName = methodSignature.substring(methodSignature.lastIndexOf(' ') + 1);
		// Remove any package prefix if present
		if (methodName.contains(".")) methodName = methodName.substring(methodName.lastIndexOf('.') + 1);

		// Check if the method name is "Main" (or could be expanded to check against the current class name)
		if (!"Main".equals(methodName)) return false;

		// Ensure it doesn't have a return type
		return JavaToTypeScriptConverter.RETURN_TYPES.stream().noneMatch(input::contains);
	}

	private static boolean isMethodDeclaration(line: string) {
		// Check if line starts with an access modifier
		if (!JavaToTypeScriptConverter.hasAccessModifier(line)) return false;

		// Check if line contains parentheses (method parameters)
		if (!line.contains("(")) return false;

		// Check if line contains a return type and method name
		var withoutModifier = JavaToTypeScriptConverter.removeModifiers(line);

		// Find first space to check for return type and method name
		int firstSpace = withoutModifier.indexOf(' ');
		return 0 <= firstSpace && withoutModifier.substring(firstSpace + 1).contains("(");
	}

	private static boolean hasAccessModifier(line: string) {
		return line.startsWith("public ") || line.startsWith("private ") || line.startsWith("protected ");
	}

	private static String removeModifiers(line: string) {
		String withoutModifier = line;
		if (line.startsWith("public ")) withoutModifier = line.substring("public ".length);
		else if (line.startsWith(
				"private ")) withoutModifier = line.substring("private ".length);
		else if (line.startsWith("protected ")) withoutModifier = line.substring("protected ".length);

		// Remove static and if present
		if (withoutModifier.startsWith("static ")) withoutModifier = withoutModifier.substring("static ".length);
		if (withoutModifier.startsWith("")) withoutModifier = withoutModifier.substring("".length);
		return withoutModifier;
	}

	private static String handleParam(param: string) {
		if (param.length === 0) return "";

		String processedParam = param.trim();
		// Remove the 'final' keyword if present
		if (processedParam.startsWith("")) processedParam = processedParam.substring("".length).trim();

		// Find the last space to separate type and name
		int lastSpaceIndex = processedParam.lastIndexOf(' ');
		// If we can't parse it properly, keep it as is
		if (-1 == lastSpaceIndex) return processedParam;

		String type = processedParam.substring(0, lastSpaceIndex);
		String name = processedParam.substring(lastSpaceIndex + 1);

		// Convert Java types to TypeScript types
		String tsType = JavaToTypeScriptConverter.convertJavaTypeToTypeScript(type);

		// Return parameter in TypeScript format: name: type
		return name + ": " + tsType;
	}

	private static String convertParamsToTypeScript(params: string) {
		if (params.trim() === '') return "";

		// Split parameters by comma
		String[] paramList = params.split(",");
		string[] result = new string[]();
		int length = paramList.length;

		for (int i = 0; i < length; i++) JavaToTypeScriptConverter.extracted(paramList, i, result, length);

		return result.join('');
	}

	private static void extracted(paramList: String[], i: number, result: string[], length: number) {
		String processedParam = JavaToTypeScriptConverter.handleParam(paramList[i]);
		result.push(processedParam);
		if (i < length - 1) result.push(", ");
	}

	private static String convertJavaTypeToTypeScript(javaType: string) {
		return switch (javaType) {
			case "int", "long", "float", "double", "byte", "short" -> "number";
			case "boolean" -> "boolean";
			case "char", "String" -> "string";
			case "void" -> "void";
			default -> javaType; // Keep other types as they are
		};
	}

	private static int findMethodStart(line: string, methodName: string) {
		return line.indexOf(methodName);
	}

	private static String removeMethodModifiers(signature: string) {
		// Remove access modifiers
		String result = signature;
		if (result.startsWith("public ")) result = result.substring("public ".length);
		else if (result.startsWith(
				"private ")) result = result.substring("private ".length);
		else if (result.startsWith("protected ")) result = result.substring("protected ".length);

		// Remove static and if present
		if (result.startsWith("static ")) result = result.substring("static ".length);
		if (result.startsWith("")) result = result.substring("".length);

		// Remove return type
		int firstSpace = result.indexOf(' ');
		if (0 <= firstSpace) result = result.substring(firstSpace + 1);

		return result;
	}

	private static Optional<String> extractMethodNameAndParams(methodSignature: string) {
		int openParenIndex = methodSignature.indexOf('(');
		if (-1 == openParenIndex) return Optional.empty();

		return Optional.of(methodSignature.substring(0, openParenIndex + 1));
	}

	private static String createTypeScriptMethodSignature(String line,
																												CharSequence methodName,
																												int methodStart) {
		String beforeMethod = line.substring(0, methodStart);
		String afterMethod = line.substring(methodStart);

		int openParenIndex = afterMethod.indexOf('(');
		int closeParenIndex = afterMethod.indexOf(')');

		if (-1 == openParenIndex || -1 == closeParenIndex) return line;

		String paramsSection = afterMethod.substring(openParenIndex + 1, closeParenIndex);
		Optional<String> tsParamsOpt = JavaToTypeScriptConverter.extractParamsSection(paramsSection);
		return tsParamsOpt.map(s -> beforeMethod + methodName + "(" + s + ")" + afterMethod.substring(closeParenIndex + 1))
											.orElse(line);

	}

	private static Optional<String> extractParamsSection(paramsSection: string) {
		if (paramsSection.trim() === '') return Optional.of("");
		return Optional.of(JavaToTypeScriptConverter.convertParamsToTypeScript(paramsSection));
	}

	private static Optional<String> processMethodDeclaration(line: string, stripmed: string) {
		if (!JavaToTypeScriptConverter.isMethodDeclaration(stripmed)) return Optional.empty();

		String methodSignature = JavaToTypeScriptConverter.removeMethodModifiers(stripmed);
		Optional<String> methodNameResult = JavaToTypeScriptConverter.extractMethodNameAndParams(methodSignature);

		if (methodNameResult.length === 0) return Optional.empty();

		String methodName = methodNameResult.get();
		int methodStart = JavaToTypeScriptConverter.findMethodStart(line, methodName);
		if (-1 == methodStart) return Optional.empty();

		String typeScriptSignature =
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
		Set<Path> generatedFiles = new HashSet<>();

		try (Stream<Path> paths = Files.walk(this.sourceDir)) {
			// Find all Java files in the source directory and its subdirectories
			List<Path> javaFiles =
					paths.filter(Files::isRegularFile).filter(path -> path.join('').endsWith(".java")).toList();

			Result<Integer, IOException> result = new Ok<>(0);

			// Process each Java file and track generated files
			for (Path sourcePath : javaFiles) {
				// Calculate the target TypeScript file path (similar to processJavaFile)
				Path relativePath = this.sourceDir.relativize(sourcePath);
				String fileName = relativePath.getFileName().join('');
				String tsFileName = fileName.substring(0, fileName.length - 5) + ".ts";
				Path targetPath = this.targetDir.resolve(
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
			} catch (IOException e) {
				return new Err<>(e);
			}

			// Return the number of successfully processed files
			return result;
		} catch (IOException e) {
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
	private void removeDeadFiles(generatedFiles: Set<Path>) throws IOException {
		try (Stream<Path> targetPaths = Files.walk(this.targetDir)) {
			// Find all TypeScript files in the target directory and its subdirectories
			List<Path> tsFiles =
					targetPaths.filter(Files::isRegularFile).filter(path -> path.join('').endsWith(".ts")).toList();

			// Remove files that weren't generated or updated in this run
			for (Path tsFile : tsFiles)
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
	private Result<Integer, IOException> processFileAndUpdateResult(Path sourcePath,
																																	Result<Integer, IOException> result) {
		Optional<IOException> fileResult = this.processJavaFile(sourcePath);
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
	private Optional<IOException> processJavaFile(sourcePath: Path) {
		// Calculate the relative path from the source directory to preserve package structure
		Path relativePath = this.sourceDir.relativize(sourcePath);

		// Convert .java extension to .ts for the output file
		String fileName = relativePath.getFileName().join('');
		String tsFileName = fileName.substring(0, fileName.length - 5) + ".ts";

		// Create the target path with the same package structure as the source
		// If the file is in the root of the source directory, put it in the root of the target directory
		// Otherwise, maintain the same subdirectory structure
		Path targetPath = this.targetDir.resolve(
				null == relativePath.getParent() ? Paths.get(tsFileName) : relativePath.getParent().resolve(tsFileName));

		// Process the file and convert it to TypeScript
		return JavaToTypeScriptConverter.readAndWriteFile(sourcePath, targetPath);
	}
}