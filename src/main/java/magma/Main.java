package magma;

import magma.compile.error.ApplicationError;
import magma.compile.error.CompileError;
import magma.compile.error.ThrowableError;
import magma.list.ArrayList;
import magma.list.List;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) {
		if (run() instanceof Some<ApplicationError>(ApplicationError value)) System.err.println(value.display());
	}

	private static Option<ApplicationError> run() {
		final Path javaSourceRoot = Paths.get(".", "src", "main", "java");
		final Path cOutputRoot = Paths.get(".", "src", "main", "windows");

		// Ensure output directory exists
		try {
			Files.createDirectories(cOutputRoot);
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}

		return compileAllJavaFiles(javaSourceRoot, cOutputRoot);
	}

	private static Option<ApplicationError> compileAllJavaFiles(Path javaSourceRoot, Path cOutputRoot) {
		try (Stream<Path> paths = Files.walk(javaSourceRoot)) {
			List<Path> javaFiles = new ArrayList<Path>(paths.filter(Files::isRegularFile)
																											.filter(path -> path.toString().endsWith(".java"))
																											.toList());

			System.out.println("Found " + javaFiles.size() + " Java files to compile");
			return compileAll(javaSourceRoot, cOutputRoot, javaFiles);
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}
	}

	private static Option<ApplicationError> compileAll(Path javaSourceRoot, Path cOutputRoot, List<Path> javaFiles) {
		int i = 0;
		while (i < javaFiles.size()) {
			Path javaFile = javaFiles.get(i).orElse(null);
			System.out.println("Compiling: " + javaFile);
			Option<ApplicationError> result = compileJavaFile(javaFile, javaSourceRoot, cOutputRoot);
			if (result instanceof Some<ApplicationError>(ApplicationError error)) {
				System.err.println("Failed to compile " + javaFile + ": " + error.display());
				return result;
			}
			System.out.println("Successfully compiled: " + javaFile);
			i++;
		}

		return Option.empty();
	}

	private static Option<ApplicationError> compileJavaFile(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {
		// Calculate relative path from source root
		Path relativePath = javaSourceRoot.relativize(javaFile);

		// Change extension from .java to .c
		String fileName = relativePath.getFileName().toString();
		String cFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".cpp";
		Path cFilePath = cOutputRoot.resolve(relativePath.getParent()).resolve(cFileName);

		// Ensure output directory exists
		try {
			Files.createDirectories(cFilePath.getParent());
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}

		Result<String, ThrowableError> readResult = readString(javaFile);
		if (readResult instanceof Err<String, ThrowableError>(ThrowableError error))
			return Option.of(new ApplicationError(error));

		if (!(readResult instanceof Ok<String, ThrowableError>(String input))) return Option.empty();

		Result<String, CompileError> compileResult = Compiler.compile(input);
		if (compileResult instanceof Err<String, CompileError>(CompileError error))
			return Option.of(new ApplicationError(error));

		if (compileResult instanceof Ok<String, CompileError>(String compiled)) {
			final String message = formatMessage(javaFile);
			return writeString(cFilePath, message + compiled).map(ThrowableError::new).map(ApplicationError::new);
		}

		return Option.empty();
	}

	private static String formatMessage(Path javaFile) {
		final Path relative = Paths.get(".").relativize(javaFile);
		return "// Generated transpiled C++ from '" + relative +
					 "'. This file shouldn't be edited, and rather the compiler implementation should be changed." +
					 System.lineSeparator();
	}

	private static Option<IOException> writeString(Path path, String result) {
		try {
			Files.writeString(path, result);
			return Option.empty();
		} catch (IOException e) {
			return Option.of(e);
		}
	}

	private static Result<String, ThrowableError> readString(Path source) {
		try {
			return new Ok<String, ThrowableError>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, ThrowableError>(new ThrowableError(e));
		}
	}
}
