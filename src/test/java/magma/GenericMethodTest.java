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

			Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, value);
			if (deserializeResult instanceof Ok<JavaRoot, CompileError>(JavaRoot value)) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + value.children().size()); value.children().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName()); if (child instanceof Lang.Record record) {
						System.out.println("    ✅ Found Record: " + record.name());
						System.out.println("    Record children: " + record.children().size());
					}
				});

				Result<CRoot, CompileError> transformResult = Main.transform(value);
				if (transformResult instanceof Ok<CRoot, CompileError>(CRoot value)) {
					System.out.println("✅ Transform SUCCESS");
					System.out.println("CRoot children count: " + value.children().size());
				} else System.out.println("❌ Transform FAILED: " + transformResult);
			} else if (deserializeResult instanceof Err<JavaRoot, CompileError>(CompileError error))
				System.out.println("❌ Deserialization FAILED: " + error);
		} else if (lexResult instanceof Err<?, ?>(? error)) System.out.println("❌ Lexing FAILED: " + error);
	}
}