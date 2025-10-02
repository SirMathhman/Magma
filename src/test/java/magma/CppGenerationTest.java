package magma;

import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.*;

public class CppGenerationTest {

	@Test
	public void testSimpleJavaClassLexing() {
		String input = """
				package magma.option;

				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Simple Java Class Lexing ===");
		System.out.println("Input: " + input);

		Result<Node, CompileError> lexResult = JRoot().lex(input);

		assertInstanceOf(Ok<?, ?>.class, lexResult, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			System.out.println("Lexed Node:"); System.out.println(value.format(0));
		}
	}

	@Test
	public void testJavaToObjectDeserialization() {
		String input = """
				package magma.option;

				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Java to Object Deserialization ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		assertInstanceOf(Ok<?, ?>.class, lexResult, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, value);

			assertInstanceOf(Ok<?, ?>.class, deserializeResult, () -> "Deserialization failed: " + deserializeResult);

			if (deserializeResult instanceof Ok<JavaRoot, CompileError>(JavaRoot value)) {
				System.out.println("Deserialized JavaRoot:"); System.out.println("Children count: " + value.children().size());

				value.children().forEach(child -> {
					System.out.println("Child type: " + child.getClass().getSimpleName());
					if (child instanceof JStructure jStruct) {
						System.out.println("  Structure name: " + jStruct.name());
						System.out.println("  Structure children: " + jStruct.children().size());
						jStruct.children().forEach(structChild -> {
							System.out.println("    Struct child: " + structChild.getClass().getSimpleName());
							if (structChild instanceof Method method) {
								System.out.println("      Method definition: " + method.definition());
								System.out.println("      Method body: " + method.body());
							}
						});
					}
				});
			}
		}
	}

	@Test
	public void testJavaToCppTransformation() {
		String input = """
				package magma.option;

				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Java to C++ Transformation ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		assertInstanceOf(Ok<?, ?>.class, lexResult, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, value);
			assertInstanceOf(Ok<?, ?>.class, deserializeResult, () -> "Deserialization failed: " + deserializeResult);

			if (deserializeResult instanceof Ok<JavaRoot, CompileError>(JavaRoot value)) {
				System.out.println("JavaRoot children count: " + value.children().size()); value.children().forEach(child -> {
					System.out.println("  JavaRoot child: " + child.getClass().getSimpleName());
					if (child instanceof Lang.Record record) {
						System.out.println("    Record name: " + record.name());
						System.out.println("    Record params: " + record.params());
						System.out.println("    Record children count: " + record.children().size());
						record.children().forEach(structChild -> {
							System.out.println("      Record child: " + structChild.getClass().getSimpleName());
						});
					}
				});

				Result<CRoot, CompileError> transformResult = Main.transform(value);
				assertInstanceOf(Ok<?, ?>.class, transformResult, () -> "Transform failed: " + transformResult);

				if (transformResult instanceof Ok<CRoot, CompileError>(CRoot value)) {
					System.out.println("Transformed CRoot:");
					System.out.println("C++ segments count: " + value.children().size());

					value.children().forEach(segment -> {
						System.out.println("C++ segment type: " + segment.getClass().getSimpleName());
						if (segment instanceof Structure struct) {
							System.out.println("  Structure name: " + struct.name());
							System.out.println("  Structure fields: " + struct.fields().size());
						} else if (segment instanceof Function func) {
							System.out.println("  Function name: " + func.definition().name());
							System.out.println("  Function params: " + func.params().size());
							System.out.println("  Function body: " + func.body());
						}
					});
				}
			}
		}
	}

	@Test
	public void testCppSerialization() {
		String input = """
				package magma.option;

				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing C++ Serialization ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		assertInstanceOf(Ok<?, ?>.class, lexResult, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, value);
			assertInstanceOf(Ok<?, ?>.class, deserializeResult, () -> "Deserialization failed: " + deserializeResult);

			if (deserializeResult instanceof Ok<JavaRoot, CompileError>(JavaRoot value)) {
				Result<CRoot, CompileError> transformResult = Main.transform(value);
				assertInstanceOf(Ok<?, ?>.class, transformResult, () -> "Transform failed: " + transformResult);

				if (transformResult instanceof Ok<CRoot, CompileError>(CRoot value)) {
					Result<Node, CompileError> serializeResult = Serialize.serialize(CRoot.class, value);

					assertInstanceOf(Ok<?, ?>.class, serializeResult, () -> "C++ Serialization failed: " + serializeResult);

					if (serializeResult instanceof Ok<Node, CompileError>(Node value)) {
						System.out.println("Serialized C++ Node:"); System.out.println(value.format(0));
					}
				}
			}
		}
	}

	@Test
	public void testFullCppGeneration() {
		String input = """
				package magma.option;

				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Full C++ Generation ===");

		Result<String, CompileError> compileResult = Main.compile(input);

		if (compileResult instanceof Ok<String, CompileError>(String value)) {
			System.out.println("Generated C++ code:"); System.out.println(value);
			assertFalse(value.isEmpty(), "Generated C++ code should not be empty");
		} else if (compileResult instanceof Err<String, CompileError>(CompileError error)) {
			System.err.println("Compilation failed: " + error); fail("Full compilation failed: " + error);
		}
	}
}