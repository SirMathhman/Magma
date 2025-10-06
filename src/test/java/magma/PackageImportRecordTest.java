package magma;

import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import static magma.compile.Lang.JRoot;

public class PackageImportRecordTest {

	@Test
	public void testRecordWithPackageAndImport() {
		String input = """
				package magma.option;

				import java.util.function.Function;

				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Record with Package and Import ===");

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			System.out.println("✅ Lexing SUCCESS");

			Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, lexOk.value());
			if (deserializeResult instanceof Ok<JRoot, CompileError> deserOk) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + deserOk.value().children().size());
				deserOk.value().children().stream().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName());
					if (child instanceof Lang.RecordNode record) {
						System.out.println("    ✅ Found Record: " + record.name());
					}
				});
			} else if (deserializeResult instanceof Err<JRoot, CompileError> err) {
				System.out.println("❌ Deserialization FAILED: " + err.error());
			}
		} else if (lexResult instanceof Err<?, ?> err) {
			System.out.println("❌ Lexing FAILED: " + err.error());
		}
	}

	@Test
	public void testRecordWithoutPackageAndImport() {
		String input = """
				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Record without Package and Import ===");

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			System.out.println("✅ Lexing SUCCESS");

			Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, lexOk.value());
			if (deserializeResult instanceof Ok<JRoot, CompileError> deserOk) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + deserOk.value().children().size());
				deserOk.value().children().stream().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName());
					if (child instanceof Lang.RecordNode record) {
						System.out.println("    ✅ Found Record: " + record.name());
					}
				});
			} else if (deserializeResult instanceof Err<JRoot, CompileError> err) {
				System.out.println("❌ Deserialization FAILED: " + err.error());
			}
		} else if (lexResult instanceof Err<?, ?> err) {
			System.out.println("❌ Lexing FAILED: " + err.error());
		}
	}
}