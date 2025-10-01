package magma;

import magma.compile.Serialize;
import magma.compile.error.ApplicationError;
import magma.compile.error.CompileError;
import magma.compile.error.ThrowableError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static magma.compile.Lang.*;

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
			List<Path> javaFiles =
					paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();

			System.out.println("Found " + javaFiles.size() + " Java files to compile");

			for (Path javaFile : javaFiles) {
				System.out.println("Compiling: " + javaFile);
				Option<ApplicationError> result = compileJavaFile(javaFile, javaSourceRoot, cOutputRoot);
				if (result instanceof Some<ApplicationError>(ApplicationError error)) {
					System.err.println("Failed to compile " + javaFile + ": " + error.display());
					return result; // Fail fast - return the error immediately
				} System.out.println("Successfully compiled: " + javaFile);
			}

			return Option.empty();
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}
	}

	private static Option<ApplicationError> compileJavaFile(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {
		// Calculate relative path from source root
		Path relativePath = javaSourceRoot.relativize(javaFile);

		// Change extension from .java to .c
		String fileName = relativePath.getFileName().toString();
		String cFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".c";
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

		if (readResult instanceof Ok<String, ThrowableError>(String input)) {
			Result<String, CompileError> compileResult = compile(input);
			if (compileResult instanceof Err<String, CompileError>(CompileError error))
				return Option.of(new ApplicationError(error));
			if (compileResult instanceof Ok<String, CompileError>(String compiled))
				return writeString(cFilePath, compiled).map(ThrowableError::new).map(ApplicationError::new);
		}

		return Option.empty();
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
			return new Ok<>(Files.readString(source));
		} catch (IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	}

	private static Result<String, CompileError> compile(String input) {
		return createJavaRootRule().lex(input)
															 .flatMap(node -> Serialize.deserialize(JavaRoot.class, node))
															 .flatMap(Main::transform)
															 .flatMap(cRoot -> Serialize.serialize(CRoot.class, cRoot))
															 .flatMap(createCRootRule()::generate);
	}

	private static Result<CRoot, CompileError> transform(JavaRoot node) {
		return new Ok<>(new CRoot(node.children().stream().flatMap(Main::flattenRootSegment).toList()));
	}

	private static Stream<CRootSegment> flattenRootSegment(JavaRootSegment segment) {
		return switch (segment) {
			case JClass aClass -> {
				final Structure structure = new Structure(aClass.name());
				yield Stream.concat(Stream.of(structure), aClass.children().stream().flatMap(Main::getSelf));
			}
			case Content content -> Stream.of(content);
			default -> Stream.empty();
		};
	}

	private static Stream<CRootSegment> getSelf(JavaClassMember self) {
		return switch (self) {
			case Content content -> Stream.of(content); case Method method -> Stream.of(transformMethod(method));
			case Whitespace _ -> Stream.empty();
		};
	}

	private static Function transformMethod(Method method) {
		final List<JavaDefinition> oldParams = switch (method.params()) {
			case None<List<JavaDefinition>> _ -> Collections.emptyList();
			case Some<List<JavaDefinition>> v -> v.value();
		};

		final List<CDefinition> newParams = oldParams.stream().map(Main::getDefinition).toList();
		return new Function(getDefinition(method.definition()), newParams, method.body());
	}

	private static CDefinition getDefinition(JavaDefinition definition) {
		return new CDefinition(definition.name(), getType(definition.type()));
	}

	private static CType getType(JavaType type) {
		return switch (type) {
			case Content content -> content; case Generic generic -> new Content(generic.base() + "_?");
			case Identifier identifier -> identifier;
		};
	}
}
