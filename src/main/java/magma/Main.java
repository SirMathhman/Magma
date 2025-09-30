package magma;

import magma.compile.Serialize;
import magma.compile.error.ApplicationError;
import magma.compile.error.CompileError;
import magma.compile.error.ThrowableError;
import magma.option.None;
import magma.option.Optional;
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
		if (run() instanceof Some<ApplicationError>(
				ApplicationError value
		)) System.out.println(value.display());
	}

	private static Optional<ApplicationError> run() {
		final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		return switch (readString(source)) {
			case Err<String, ThrowableError>(ThrowableError error) -> Optional.of(new ApplicationError(error));
			case Ok<String, ThrowableError>(String input) -> {
				final Result<String, CompileError> result = compile(input);
				yield switch (result) {
					case Err<String, CompileError> v -> Optional.of(new ApplicationError(v.error()));
					case Ok<String, CompileError> v -> {
						final Path path = source.resolveSibling("main.c");
						yield writeString(path, v.value()).map(ThrowableError::new).map(ApplicationError::new);
					}
				};
			}
		};
	}

	private static Optional<IOException> writeString(Path path, String result) {
		try {
			Files.writeString(path, result);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
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
				yield Stream.concat(Stream.of(structure), aClass.children().stream().map(Main::getSelf));
			}
			case Content content -> Stream.of(content);
			default -> Stream.empty();
		};
	}

	private static CRootSegment getSelf(JavaClassMember self) {
		return switch (self) {
			case Content content -> content; case Method method -> transformMethod(method);
		};
	}

	private static Function transformMethod(Method method) {
		final List<JavaDefinition> oldParams = switch (method.params()) {
			case None<List<JavaDefinition>> v -> Collections.emptyList(); case Some<List<JavaDefinition>> v -> v.value();
		};

		final List<CDefinition> newParams = oldParams.stream().map(_ -> new CDefinition()).toList();
		return new Function(getDefinition(method), newParams, method.body());
	}

	private static CDefinition getDefinition(Method method) {
		return new CDefinition();
	}
}
