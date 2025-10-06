package magma;

import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import magma.transform.Transformer;
import org.junit.jupiter.api.Test;

import static magma.compile.Lang.JRoot;

public class GenericMethodTest {

	@Test
	public void testRecordWithRealSomeMethodSignature() {
		String input = """
				package magma.option;

				import java.util.function.Function;

				public record Some<T>(T value) implements Option<T> {
				    @Override
				    public <R> Option<R> map(Function<T, R> mapper) {
				        return new Some<>(mapper.apply(value));
				    }
				}
				""";

		System.out.println("=== Testing Record with Real Some Method Signature ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			System.out.println("✅ Lexing SUCCESS");

			Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, value);
			if (deserializeResult instanceof Ok<JRoot, CompileError>(JRoot javaRoot)) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + javaRoot.children().size());
				javaRoot.children().stream().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName());
					if (child instanceof Lang.RecordNode record) {
						System.out.println("    ✅ Found Record: " + record.name());
						System.out.println("    Record children: " + record.children().size());
					}
				});

				Result<Lang.CRoot, CompileError> transformResult = Transformer.transform(javaRoot);
				if (transformResult instanceof Ok<Lang.CRoot, CompileError>(Lang.CRoot cppRoot)) {
					System.out.println("✅ Transform SUCCESS");
					System.out.println("CRoot children count: " + cppRoot.children().size());
				} else System.out.println("❌ Transform FAILED: " + transformResult);
		} else if (deserializeResult instanceof Err<JRoot, CompileError>(CompileError error))
			System.out.println("❌ Deserialization FAILED: " + error);
	} else if (lexResult instanceof Err<Node, CompileError>(CompileError error)) System.out.println("❌ Lexing FAILED: " + error);
	}
}