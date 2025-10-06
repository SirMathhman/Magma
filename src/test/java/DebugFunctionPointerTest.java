import magma.compile.CRules;
import magma.compile.JavaSerializer;
import magma.compile.error.CompileError;
import magma.option.Some;
import magma.result.Ok;
import magma.result.Result;
import magma.transform.Transformer;
import org.junit.jupiter.api.Test;

import static magma.compile.Lang.*;
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

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		assertTrue(lexResult instanceof Ok<?, ?>, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			System.out.println("\n=== Lexed Node ===");
			System.out.println(lexOk.value().format(0));

			Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, lexOk.value());
			assertTrue(deserializeResult instanceof Ok<?, ?>, () -> "Deserialization failed: " + deserializeResult);

			if (deserializeResult instanceof Ok<JRoot, CompileError> deserOk) {
				System.out.println("\n=== Deserialized JavaRoot ===");
				deserOk.value().children().stream().forEach(child -> {
					System.out.println("Child: " + child.getClass().getSimpleName());
					if (child instanceof JClass jClass) {
						jClass.children().stream().forEach(structChild -> {
							System.out.println("  StructChild: " + structChild.getClass().getSimpleName());
							if (structChild instanceof Method method) {
								System.out.println("    Method: " + method.definition().name());
								if (method.params() instanceof Some<?>(var paramsList)) {
									((java.util.List<?>) paramsList).forEach(param -> {
										if (param instanceof JDefinition def) {
											System.out.println("      Param: " + def.name() + " : " + def.type());
											System.out.println("      Param type class: " + def.type().getClass().getSimpleName());
											if (def.type() instanceof JGeneric gen) {
												System.out.println("        Generic base: " + gen.base());
												System.out.println("        Generic args: " + gen.typeArguments());
											}
										}
									});
								}
							}
						});
					}
				});

				Result<CRoot, CompileError> transformResult = Transformer.transform(deserOk.value());
				assertTrue(transformResult instanceof Ok<?, ?>, () -> "Transform failed: " + transformResult);

				if (transformResult instanceof Ok<CRoot, CompileError> transformOk) {
					System.out.println("\n=== Transformed CRoot ===");
					transformOk.value().children().stream().forEach(child -> {
						System.out.println("Child: " + child.getClass().getSimpleName());
						if (child instanceof Function func) {
							System.out.println("  Function: " + func.definition().name());
							func.params().stream().forEach(param -> {
								if (param instanceof CDefinition def) {
									System.out.println("    Param (CDefinition): " + def.name() + " : " + def.type());
									System.out.println("    Param type class: " + def.type().getClass().getSimpleName());
								} else if (param instanceof CFunctionPointerDefinition fpDef) {
									System.out.println("    Param (CFunctionPointerDefinition): " + fpDef.name());
									System.out.println(
											"      ✅ FunctionPointer! Return: " + fpDef.returnType() + ", Params: " + fpDef.paramTypes());
								}
							});
						}
					});

					Result<magma.compile.Node, CompileError> serializeResult = JavaSerializer.serialize(CRoot.class,
							transformOk.value());
					assertTrue(serializeResult instanceof Ok<?, ?>, () -> "Serialization failed: " + serializeResult);

					if (serializeResult instanceof Ok<magma.compile.Node, CompileError> serOk) {
						System.out.println("\n=== Serialized Node ===");
						System.out.println(serOk.value().format(0));

						Result<String, CompileError> generateResult = CRules.CRoot().generate(serOk.value());
						assertTrue(generateResult instanceof Ok<?, ?>, () -> "Generation failed: " + generateResult);

						if (generateResult instanceof Ok<String, CompileError> genOk) {
							System.out.println("\n=== Generated C++ ===");
							System.out.println(genOk.value());
							assertTrue(genOk.value().contains("(*mapper)"), "Generated C++ should contain function pointer syntax");
						}
					}
				}
			}
		}
	}
}
