package magma;

import magma.compile.JavaSerializer;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class RealSomeJavaTest {

	@Test
	public void testRealSomeJavaFile() {
		String actualSomeJava = """
				package magma.option;

				import java.util.function.Function;

				public record Some<T>(T value) implements Option<T> {
				    @Override
				    public <R> Option<R> map(Function<T, R> mapper) {
				        return new Some<>(mapper.apply(value));
				    }

				    @Override
				    public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
				        return mapper.apply(value);
				    }

				    @Override
				    public T orElse(T other) {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Real Some.java ===");

		// Test each step of the pipeline
		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(actualSomeJava);
		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			System.out.println("✅ Lexing SUCCESS");

			Result<JavaRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JavaRoot.class, lexOk.value());
			if (deserializeResult instanceof Ok<JavaRoot, CompileError> deserOk) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + deserOk.value().children().size());
				deserOk.value().children().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName());
				});

				Result<CRoot, CompileError> transformResult = Main.transform(deserOk.value());
				if (transformResult instanceof Ok<CRoot, CompileError> transformOk) {
					System.out.println("✅ Transform SUCCESS");
					System.out.println("CRoot children count: " + transformOk.value().children().size());
					transformOk.value().children().forEach(child -> {
						System.out.println("  CRoot child: " + child.getClass().getSimpleName());
					});
				} else {
					System.out.println("❌ Transform FAILED: " + transformResult);
				}
			} else {
				System.out.println("❌ Deserialization FAILED: " + deserializeResult);
			}
		} else {
			System.out.println("❌ Lexing FAILED: " + lexResult);
		}

		Result<String, CompileError> compileResult = Main.compile(actualSomeJava);

		if (compileResult instanceof Ok<String, CompileError> ok) {
			System.out.println("✅ Compilation SUCCESS");
			System.out.println("Generated C++ code:");
			System.out.println(ok.value());
			assertFalse(ok.value().isEmpty(), "Generated C++ should not be empty");
		} else if (compileResult instanceof Err<String, CompileError> err) {
			System.err.println("❌ Compilation FAILED: " + err.error());
			fail("Compilation failed: " + err.error());
		}
	}
}