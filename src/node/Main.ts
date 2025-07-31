
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
export class magma.Main {
	// No regex patterns needed

	constructor() {}

readAndWriteFile(sourcePath: Path, targetPath: Path) {
		try {
			// Create parent directories if they don't exist
			Files.createDirectories(targetPath.getParent());

			String content = Files.readString(sourcePath, StandardCharsets.UTF_8);
			System.out.println(content);

			// Remove Java import statements from the output
			content = content.lines()
											 .filter(line -> !line.strip().startsWith("import java."))
											 .map(magma.Main::processLine)
											 .collect(Collectors.joining(System.lineSeparator()));

			Files.writeString(targetPath, content, StandardCharsets.UTF_8);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

processLine(line: string) {
		final String trimmed = line.strip();
		// Handle class declarations
		if (trimmed.startsWith("public class") || trimmed.startsWith("public final class"))
			return line.replace("public final class", "export class").replace("public class", "export class");

		// Handle constructors - identify methods with the same name as the class and no return type
		if (magma.Main.isAPrivate(trimmed)) return line.replace("private magma.Main", "constructor");
		return magma.Main.getString(line, trimmed).orElse(line);
	}

isAPrivate(trimmed: string) {
		return trimmed.startsWith("private") && trimmed.contains("magma.Main(") && !trimmed.contains(" void ") &&
					 !trimmed.contains(" int ") && !trimmed.contains(" String ") && !trimmed.contains(" boolean ") &&
					 !trimmed.contains(" double ") && !trimmed.contains(" float ") && !trimmed.contains(" long ") &&
					 !trimmed.contains(" char ") && !trimmed.contains(" byte ") && !trimmed.contains(" short ") &&
					 !trimmed.contains(" Object ");
	}

isMethodDeclaration(line: string) {
		// Check if line starts with an access modifier
		if (!line.startsWith("public ") && !line.startsWith("private ") && !line.startsWith("protected ")) return false;

		// Check if line contains parentheses (method parameters)
		if (!line.contains("(")) return false;

		// Check if line contains a return type and method name
		final var withoutModifier = magma.Main.getString(line);

		// Split by space to check for return type and method name
		final String[] parts = withoutModifier.split(" ", 2);
		return 2 <= parts.length && parts[1].contains("(");
	}

getString(line: string) {
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
	
convertParamsToTypeScript(params: string) {
		if (params.trim().isEmpty()) return "";
		
		// Split parameters by comma
		String[] paramList = params.split(",");
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < paramList.length; i++) {
			String param = paramList[i].trim();
			
			// Skip empty parameters
			if (param.isEmpty()) continue;
			
			// Remove 'final' keyword if present
			if (param.startsWith("final ")) {
				param = param.substring("final ".length()).trim();
			}
			
			// Split the parameter into type and name
			String[] parts = param.split("\\s+", 2);
			if (parts.length == 2) {
				String type = parts[0];
				String name = parts[1];
				
				// Convert Java types to TypeScript types
				String tsType = convertJavaTypeToTypeScript(type);
				
				// Add parameter in TypeScript format: name: type
				result.append(name).append(": ").append(tsType);
			} else {
				// If we can't parse it properly, keep it as is
				result.append(param);
			}
			
			// Add comma if not the last parameter
			if (i < paramList.length - 1) {
				result.append(", ");
			}
		}
		
		return result.toString();
	}
	
convertJavaTypeToTypeScript(javaType: string) {
		// Convert common Java types to TypeScript types
		switch (javaType) {
			case "int":
			case "long":
			case "float":
			case "double":
			case "byte":
			case "short":
				return "number";
			case "boolean":
				return "boolean";
			case "char":
				return "string";
			case "String":
				return "string";
			case "Object":
				return "any";
			case "void":
				return "void";
			default:
				// For arrays
				if (javaType.endsWith("[]")) {
					String baseType = javaType.substring(0, javaType.length() - 2);
					return convertJavaTypeToTypeScript(baseType) + "[]";
				}
				// For other types, keep them as is
				return javaType;
		}
	}

findMethodStart(line: string, methodName: string) {
		final int index = line.indexOf(methodName);
		if (-1 == index) return -1;

		// Verify this is actually the method name by checking what comes after it
		if (index + methodName.length() < line.length() && '(' == line.charAt(index + methodName.length())) return index;

		// Try to find the next occurrence
		return magma.Main.findMethodStart(line.substring(index + 1), methodName);
	}

getString(line: string, trimmed: string) {
		// Handle method declarations
		if (!magma.Main.isMethodDeclaration(trimmed)) return Optional.empty();

		// Extract method name and parameters
		String methodSignature = trimmed;

		// Remove access modifiers (public, private, protected)
		if (methodSignature.startsWith("public ")) methodSignature = methodSignature.substring("public ".length());
		else if (methodSignature.startsWith("private ")) methodSignature = methodSignature.substring("private ".length());
		else if (methodSignature.startsWith("protected "))
			methodSignature = methodSignature.substring("protected ".length());

		// Remove static and final keywords
		if (methodSignature.startsWith("static ")) methodSignature = methodSignature.substring("static ".length());
		if (methodSignature.startsWith("final ")) methodSignature = methodSignature.substring("final ".length());

		// Extract return type and method name
		final String[] parts = methodSignature.split(" ", 2);
		if (2 > parts.length) return Optional.empty();

		final String methodNameAndParams = parts[1];
		final String methodName =
				methodNameAndParams.contains("(") ? methodNameAndParams.substring(0, methodNameAndParams.indexOf('('))
																					: methodNameAndParams;

		// Create TypeScript method signature
		final int methodStart = magma.Main.findMethodStart(line, methodName);
		if (-1 == methodStart) return Optional.empty();
		
		// Get the parameters part and convert it to TypeScript format
		String paramsSection = line.substring(methodStart + methodName.length());
		if (paramsSection.contains("(") && paramsSection.contains(")")) {
			int openParenIndex = paramsSection.indexOf('(');
			int closeParenIndex = paramsSection.indexOf(')', openParenIndex);
			
			if (closeParenIndex > openParenIndex) {
				String params = paramsSection.substring(openParenIndex + 1, closeParenIndex);
				String convertedParams = magma.Main.convertParamsToTypeScript(params);
				paramsSection = "(" + convertedParams + ")" + paramsSection.substring(closeParenIndex + 1);
			}
		}

		return Optional.of(methodName + paramsSection);
	}

main(args: string[]) {
		// Get the absolute path to the current working directory
		final Path currentDir = Paths.get("").toAbsolutePath();
		// Check if we're in the project root or in a subdirectory
		final Path projectRoot = currentDir.endsWith("java") ? currentDir.getParent().getParent() : currentDir;

		final Path sourcePath = projectRoot.resolve(Paths.get("src", "java", "magma.Main.java"));
		final Path targetPath = projectRoot.resolve(Paths.get("src", "node", "magma.Main.ts"));

		System.out.println("=== Contents of " + sourcePath + " ===");
		System.out.println();
		System.out.println("Writing contents to " + targetPath);
		System.out.println();

		final Optional<Exception> exception = magma.Main.readAndWriteFile(sourcePath, targetPath);

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