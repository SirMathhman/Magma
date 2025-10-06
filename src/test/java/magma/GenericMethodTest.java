package magma;

import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import static magma.compile.Lang.CRoot;
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

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			System.out.println("✅ Lexing SUCCESS");

			Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, lexOk.value());
			if (deserializeResult instanceof Ok<JRoot, CompileError> deserOk) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + deserOk.value().children().size());
				deserOk.value().children().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName());
					if (child instanceof Lang.RecordNode record) {
						System.out.println("    ✅ Found Record: " + record.name());
						System.out.println("    Record children: " + record.children().size());
					}
				});

				Result<CRoot, CompileError> transformResult = Compiler.transform(deserOk.value());
				if (transformResult instanceof Ok<CRoot, CompileError> transformOk) {
					System.out.println("✅ Transform SUCCESS");
					System.out.println("CRoot children count: " + transformOk.value().children().size());
				} else {
					System.out.println("❌ Transform FAILED: " + transformResult);
				}
			} else if (deserializeResult instanceof Err<JRoot, CompileError> err) {
				System.out.println("❌ Deserialization FAILED: " + err.error());
			}
		} else if (lexResult instanceof Err<?, ?> err) {
			System.out.println("❌ Lexing FAILED: " + err.error());
		}
	}
}