package magma;

import magma.compile.JavaSerializer;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import magma.transform.Transformer;
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

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);

		assertTrue(lexResult instanceof Ok<?, ?>, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<magma.compile.Node, CompileError> ok) {
			System.out.println("Lexed Node:");
			System.out.println(ok.value().format(0));
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

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		assertTrue(lexResult instanceof Ok<?, ?>, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, lexOk.value());

			assertTrue(deserializeResult instanceof Ok<?, ?>, () -> "Deserialization failed: " + deserializeResult);

			if (deserializeResult instanceof Ok<JRoot, CompileError> deserOk) {
				System.out.println("Deserialized JavaRoot:");
				System.out.println("Children count: " + deserOk.value().children().size());

				deserOk.value().children().forEach(child -> {
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

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		assertTrue(lexResult instanceof Ok<?, ?>, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, lexOk.value());
			assertTrue(deserializeResult instanceof Ok<?, ?>, () -> "Deserialization failed: " + deserializeResult);

			if (deserializeResult instanceof Ok<JRoot, CompileError> deserOk) {
				System.out.println("JavaRoot children count: " + deserOk.value().children().size());
				deserOk.value().children().forEach(child -> {
					System.out.println("  JavaRoot child: " + child.getClass().getSimpleName());
					if (child instanceof RecordNode record) {
						System.out.println("    Record name: " + record.name());
						System.out.println("    Record params: " + record.params());
						System.out.println("    Record children count: " + record.children().size());
						record.children().forEach(structChild -> {
							System.out.println("      Record child: " + structChild.getClass().getSimpleName());
						});
					}
				});

				Result<CRoot, CompileError> transformResult = Transformer.transform(deserOk.value());
				assertTrue(transformResult instanceof Ok<?, ?>, () -> "Transform failed: " + transformResult);

				if (transformResult instanceof Ok<CRoot, CompileError> transformOk) {
					System.out.println("Transformed CRoot:");
					System.out.println("C++ segments count: " + transformOk.value().children().size());

					transformOk.value().children().forEach(segment -> {
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

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		assertTrue(lexResult instanceof Ok<?, ?>, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, lexOk.value());
			assertTrue(deserializeResult instanceof Ok<?, ?>, () -> "Deserialization failed: " + deserializeResult);

			if (deserializeResult instanceof Ok<JRoot, CompileError> deserOk) {
				Result<CRoot, CompileError> transformResult = Transformer.transform(deserOk.value());
				assertTrue(transformResult instanceof Ok<?, ?>, () -> "Transform failed: " + transformResult);

				if (transformResult instanceof Ok<CRoot, CompileError> transformOk) {
					Result<magma.compile.Node, CompileError> serializeResult = JavaSerializer.serialize(CRoot.class,
																																															transformOk.value());

					assertTrue(serializeResult instanceof Ok<?, ?>, () -> "C++ Serialization failed: " + serializeResult);

					if (serializeResult instanceof Ok<magma.compile.Node, CompileError> serOk) {
						System.out.println("Serialized C++ Node:");
						System.out.println(serOk.value().format(0));
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

		Result<String, CompileError> compileResult = Compiler.compile(input);

		if (compileResult instanceof Ok<String, CompileError> ok) {
			System.out.println("Generated C++ code:");
			System.out.println(ok.value());
			assertFalse(ok.value().isEmpty(), "Generated C++ code should not be empty");
		} else if (compileResult instanceof Err<String, CompileError> err) {
			System.err.println("Compilation failed: " + err.error());
			fail("Full compilation failed: " + err.error());
		}
	}
}