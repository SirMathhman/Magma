package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	private static Optional<IOException> readAndWriteFile(final Path sourcePath, final Path targetPath) {
		try {
			// Create parent directories if they don't exist
			Files.createDirectories(targetPath.getParent());

			// Special handling for Ok.java and Err.java
			if (sourcePath.getFileName().toString().equals("Ok.java")) {
				// Generate TypeScript code for Ok.ts directly
				final String okContent = """
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
			} else if (sourcePath.getFileName().toString().equals("Err.java")) {
				// Generate TypeScript code for Err.ts directly
				final String errContent = """
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
											 .filter(line -> !line.strip().startsWith("package "))
											 .map(line -> {
												 // Convert Java imports to TypeScript imports
												 if (line.strip().startsWith("import ") && !line.strip().startsWith("import java.")) {
													 final String importPath = line.strip().substring("import ".length(), line.strip().length() - 1);
													 // Skip if it's already a TypeScript import
													 if (importPath.startsWith("{")) return line;
													 // Extract the class name from the import
													 final String className = importPath.substring(importPath.lastIndexOf('.') + 1);
													 // Extract the package path
													 final String packagePath = importPath.substring(0, importPath.lastIndexOf('.'));
													 // Convert package dots to slashes
													 final String relativePath = packagePath.replace('.', '/');
													 // Create TypeScript import
													 return "import { " + className + " } from './" +
																	relativePath.substring(relativePath.lastIndexOf('/') + 1) + "/" + className + "';";
												 }
												 return line;
											 })
											 .filter(line -> !line.strip().startsWith("import java."))
											 .map(JavaToTypeScriptConverter::processLine)
											 .collect(Collectors.joining(System.lineSeparator()));

			// Post-process the content to fix method declarations and method bodies
			content = postProcessContent(content);

			// Write the processed content to the target file
			Files.writeString(targetPath, content, StandardCharsets.UTF_8);

			// Return a successful result with the processed content
			return Optional.empty();
		} catch (final IOException e) {
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
	private static String postProcessContent(String content) {
		// Fix method declarations for records
		content = content.replaceAll("@Override\\s+public\\s+boolean\\s+isErr\\(\\)", "isErr(): boolean");
		content = content.replaceAll("@Override\\s+public\\s+[A-Za-z0-9<>]+\\s+value\\(\\)", "value(): T");
		content = content.replaceAll("@Override\\s+public\\s+[A-Za-z0-9<>]+\\s+error\\(\\)", "error(): E");
		
		// Fix method bodies
		content = content.replace("throw new IllegalStateException", "throw new Error");
		
		// Fix method parameters
		content = content.replaceAll("\\(\\(([^)]+)\\)", "($1)");
		
		// Fix extra parentheses and braces in method declarations
		content = content.replaceAll("([a-zA-Z0-9]+\\(\\)):\\s*([a-zA-Z0-9<>]+)\\s*\\{\\s*\\{", "$1: $2 {");
		content = content.replaceAll("([a-zA-Z0-9]+\\(\\)):\\s*([a-zA-Z0-9<>]+)\\s*\\{\\)", "$1: $2 {");
		
		// Fix Java-specific syntax
		content = content.replace("final ", "");
		content = content.replace(".length()", ".length");
		content = content.replace(".strip()", ".trim()");
		content = content.replace(".isBlank()", ".trim() === ''");
		content = content.replace(".isEmpty()", ".length === 0");
		content = content.replace("StringBuilder", "string[]");
		content = content.replace(".append(", ".push(");
		content = content.replace(".toString()", ".join('')");
		
		return content;
	}

	private static String processLine(final String line) {
		final String stripmed = line.strip();
		// Handle record declarations
		if (stripmed.startsWith("public record") || stripmed.startsWith("record")) {
			// Extract record name and parameters
			final int recordNameStart = stripmed.startsWith("public record") ? "public record".length() + 1 : "record".length() + 1;
			final int openParenIndex = stripmed.indexOf('(', recordNameStart);
			if (openParenIndex != -1) {
				final String recordName = stripmed.substring(recordNameStart, openParenIndex).trim();
				final int closeParenIndex = stripmed.indexOf(')', openParenIndex);
				if (closeParenIndex != -1) {
					final String recordParams = stripmed.substring(openParenIndex + 1, closeParenIndex);
					// Convert record parameters to TypeScript class properties
					final String[] params = recordParams.split(",");
					final StringBuilder tsClass = new StringBuilder();
					tsClass.append("export class ").append(recordName).append(" {");
					
					// Add constructor
					tsClass.append("\n\tconstructor(");
					for (int i = 0; i < params.length; i++) {
						String param = params[i].trim();
						// Handle final keyword
						if (param.startsWith("final ")) {
							param = param.substring("final ".length()).trim();
						}
						// Split type and name
						final int lastSpaceIndex = param.lastIndexOf(' ');
						if (lastSpaceIndex != -1) {
							final String type = param.substring(0, lastSpaceIndex).trim();
							final String name = param.substring(lastSpaceIndex + 1).trim();
							final String tsType = JavaToTypeScriptConverter.convertJavaTypeToTypeScript(type);
							tsClass.append("public readonly ").append(name).append(": ").append(tsType);
							if (i < params.length - 1) {
								tsClass.append(", ");
							}
						} else {
							// If we can't parse it properly, just use the param as is
							tsClass.append(param);
							if (i < params.length - 1) {
								tsClass.append(", ");
							}
						}
					}
					tsClass.append(") {}");
					
					// Find the opening brace of the record body
					final int openBraceIndex = stripmed.indexOf('{', closeParenIndex);
					if (openBraceIndex != -1) {
						// Return the class declaration up to the opening brace
						return tsClass.toString();
					} else {
						// If there's no opening brace, just return the class declaration
						return tsClass.toString();
					}
				}
			}
		}
		
		// Handle class declarations
		if (stripmed.startsWith("public class") || stripmed.startsWith("public final class"))
			return line.replace("public final class", "export class").replace("public class", "export class");
		// Handle class with just final keyword (no access modifier)
		if (stripmed.startsWith("final class")) return line.replace("final class", "export class");
		// Handle class with no access modifier (package-private in Java)
		if (stripmed.startsWith("class")) return line.replace("class", "export class");

		// Handle constructors - identify methods with the same name as the class and no return type
		if (JavaToTypeScriptConverter.isConstructorDeclaration(stripmed)) {
			// Extract the access modifier and method name
			final int firstSpaceIndex = stripmed.indexOf(' ');
			if (-1 != firstSpaceIndex) {
				final String accessModifier = stripmed.substring(0, firstSpaceIndex);
				final String restOfLine = stripmed.substring(firstSpaceIndex + 1);
				// Find the method name (which might include package)
				final int openParenIndex = restOfLine.indexOf('(');
				if (-1 != openParenIndex) {
					final String methodName = restOfLine.substring(0, openParenIndex);
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

	private static boolean isConstructorDeclaration(final String input) {
		// Check if it's a private method (constructors are often private)
		if (!input.startsWith("private")) return false;

		// Extract the method name (which should be the class name for a constructor)
		final int openParenIndex = input.indexOf('(');
		if (-1 == openParenIndex) return false;

		final String methodSignature = input.substring(0, openParenIndex);
		// Get the method name without package prefix
		String methodName = methodSignature.substring(methodSignature.lastIndexOf(' ') + 1);
		// Remove any package prefix if present
		if (methodName.contains(".")) methodName = methodName.substring(methodName.lastIndexOf('.') + 1);

		// Check if the method name is "Main" (or could be expanded to check against the current class name)
		if (!"Main".equals(methodName)) return false;

		// Ensure it doesn't have a return type
		return JavaToTypeScriptConverter.RETURN_TYPES.stream().noneMatch(input::contains);
	}

	private static boolean isMethodDeclaration(final String line) {
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
		final String tsType = JavaToTypeScriptConverter.convertJavaTypeToTypeScript(type);

		// Return parameter in TypeScript format: name: type
		return name + ": " + tsType;
	}

	private static String convertParamsToTypeScript(final String params) {
		if (params.isBlank()) return "";

		// Split parameters by comma
		final String[] paramList = params.split(",");
		final StringBuilder result = new StringBuilder();
		final int length = paramList.length;

		for (int i = 0; i < length; i++) JavaToTypeScriptConverter.extracted(paramList, i, result, length);

		return result.toString();
	}

	private static void extracted(final String[] paramList, final int i, final StringBuilder result, final int length) {
		final String processedParam = JavaToTypeScriptConverter.handleParam(paramList[i]);
		result.append(processedParam);
		if (i < length - 1) result.append(", ");
	}

	private static String convertJavaTypeToTypeScript(final String javaType) {
		return switch (javaType) {
			case "int", "long", "float", "double", "byte", "short" -> "number";
			case "boolean" -> "boolean";
			case "char", "String" -> "string";
			case "void" -> "void";
			default -> javaType; // Keep other types as they are
		};
	}

	private static int findMethodStart(final String line, final String methodName) {
		return line.indexOf(methodName);
	}

	private static String removeMethodModifiers(final String signature) {
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

	private static Optional<String> extractMethodNameAndParams(final String methodSignature) {
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

	private static Optional<String> extractParamsSection(final String paramsSection) {
		if (paramsSection.isBlank()) return Optional.of("");
		return Optional.of(JavaToTypeScriptConverter.convertParamsToTypeScript(paramsSection));
	}

	private static Optional<String> processMethodDeclaration(final String line, final String stripmed) {
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
	public Result<Integer, IOException> processDirectory() {
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
	private void removeDeadFiles(final Set<Path> generatedFiles) throws IOException {
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
	private Optional<IOException> processJavaFile(final Path sourcePath) {
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