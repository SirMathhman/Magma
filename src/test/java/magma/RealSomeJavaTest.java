package magma;

import magma.compile.Node;
import magma.compile.Serialize;
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
		Result<Node, CompileError> lexResult = JRoot().lex(actualSomeJava);
		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			System.out.println("✅ Lexing SUCCESS");

			Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, value);
			if (deserializeResult instanceof Ok<JavaRoot, CompileError>(JavaRoot value)) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + value.children().size()); value.children().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName());
				});

				Result<CRoot, CompileError> transformResult = Main.transform(value);
				if (transformResult instanceof Ok<CRoot, CompileError>(CRoot value)) {
					System.out.println("✅ Transform SUCCESS");
					System.out.println("CRoot children count: " + value.children().size()); value.children().forEach(child -> {
						System.out.println("  CRoot child: " + child.getClass().getSimpleName());
					});
				} else System.out.println("❌ Transform FAILED: " + transformResult);
			} else System.out.println("❌ Deserialization FAILED: " + deserializeResult);
		} else System.out.println("❌ Lexing FAILED: " + lexResult);

		Result<String, CompileError> compileResult = Main.compile(actualSomeJava);

		if (compileResult instanceof Ok<String, CompileError>(String value)) {
			System.out.println("✅ Compilation SUCCESS");
			System.out.println("Generated C++ code:"); System.out.println(value);
			assertFalse(value.isEmpty(), "Generated C++ should not be empty");
		} else if (compileResult instanceof Err<String, CompileError>(CompileError error)) {
			System.err.println("❌ Compilation FAILED: " + error); fail("Compilation failed: " + error);
		}
	}
}