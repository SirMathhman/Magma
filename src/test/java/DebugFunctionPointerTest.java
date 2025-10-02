import magma.Main;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.option.Some;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DebugFunctionPointerTest {

	@Test
	public void testFunctionTypeTransformation() {
		String input = """
				package magma.test;

				import java.util.function.Function;

				public class TestClass {
				    public <R> void test(Function<String, R> mapper) {
				        // test
				    }
				}
				""";

		System.out.println("=== Testing Function<T, R> Transformation ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		assertInstanceOf(Ok<?, ?>.class, lexResult, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			System.out.println("\n=== Lexed Node ==="); System.out.println(value.format(0));

			Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, value);
			assertInstanceOf(Ok<?, ?>.class, deserializeResult, () -> "Deserialization failed: " + deserializeResult);

			if (deserializeResult instanceof Ok<JavaRoot, CompileError>(JavaRoot value)) {
				System.out.println("\n=== Deserialized JavaRoot ==="); value.children().forEach(child -> {
					System.out.println("Child: " + child.getClass().getSimpleName());
					if (child instanceof JClass jClass) jClass.children().forEach(structChild -> {
						System.out.println("  StructChild: " + structChild.getClass().getSimpleName());
						if (structChild instanceof Method method) {
							System.out.println("    Method: " + method.definition().name());
							if (method.params() instanceof Some<?>(Object paramsList)) ((List<?>) paramsList).forEach(param -> {
								if (param instanceof JavaDefinition def) {
									System.out.println("      Param: " + def.name() + " : " + def.type());
									System.out.println("      Param type class: " + def.type().getClass().getSimpleName());
									if (def.type() instanceof Generic(String base, List<JavaType> arguments)) {
										System.out.println("        Generic base: " + base);
										System.out.println("        Generic args: " + arguments);
									}
								}
							});
						}
					});
				});

				Result<CRoot, CompileError> transformResult = Main.transform(value);
				assertInstanceOf(Ok<?, ?>.class, transformResult, () -> "Transform failed: " + transformResult);

				if (transformResult instanceof Ok<CRoot, CompileError>(CRoot value)) {
					System.out.println("\n=== Transformed CRoot ==="); value.children().forEach(child -> {
						System.out.println("Child: " + child.getClass().getSimpleName());
						if (child instanceof Function func) {
							System.out.println("  Function: " + func.definition().name());
							func.params().forEach(param -> {
								if (param instanceof CDefinition def) {
									System.out.println("    Param (CDefinition): " + def.name() + " : " + def.type());
									System.out.println("    Param type class: " + def.type().getClass().getSimpleName());
								} else if (param instanceof CFunctionPointerDefinition(
										String name, CType returnType, java.util.List<CType> paramTypes
								)) {
									System.out.println("    Param (CFunctionPointerDefinition): " + name);
									System.out.println("      ✅ FunctionPointer! Return: " + returnType + ", Params: " + paramTypes);
								}
							});
						}
					});

					Result<Node, CompileError> serializeResult = Serialize.serialize(CRoot.class, value);
					assertInstanceOf(Ok<?, ?>.class, serializeResult, () -> "Serialization failed: " + serializeResult);

					if (serializeResult instanceof Ok<Node, CompileError>(Node value)) {
						System.out.println("\n=== Serialized Node ==="); System.out.println(value.format(0));

						Result<String, CompileError> generateResult = CRoot().generate(value);
						assertInstanceOf(Ok<?, ?>.class, generateResult, () -> "Generation failed: " + generateResult);

						if (generateResult instanceof Ok<String, CompileError>(String value)) {
							System.out.println("\n=== Generated C++ ==="); System.out.println(value);
							assertTrue(value.contains("(*mapper)"), "Generated C++ should contain function pointer syntax");
						}
					}
				}
			}
		}
	}
}
