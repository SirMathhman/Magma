package magma;

import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import org.junit.jupiter.api.Test;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.*;

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

		Result<magma.compile.Node, CompileError> lexResult = JavaRoot().lex(input);
		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			System.out.println("✅ Lexing SUCCESS");

			Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, lexOk.value());
			if (deserializeResult instanceof Ok<JavaRoot, CompileError> deserOk) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + deserOk.value().children().size());
				deserOk.value().children().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName());
					if (child instanceof magma.compile.Lang.Record record) {
						System.out.println("    ✅ Found Record: " + record.name());
					}
				});
			} else if (deserializeResult instanceof Err<JavaRoot, CompileError> err) {
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

		Result<magma.compile.Node, CompileError> lexResult = JavaRoot().lex(input);
		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			System.out.println("✅ Lexing SUCCESS");

			Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, lexOk.value());
			if (deserializeResult instanceof Ok<JavaRoot, CompileError> deserOk) {
				System.out.println("✅ Deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + deserOk.value().children().size());
				deserOk.value().children().forEach(child -> {
					System.out.println("  Child: " + child.getClass().getSimpleName());
					if (child instanceof magma.compile.Lang.Record record) {
						System.out.println("    ✅ Found Record: " + record.name());
					}
				});
			} else if (deserializeResult instanceof Err<JavaRoot, CompileError> err) {
				System.out.println("❌ Deserialization FAILED: " + err.error());
			}
		} else if (lexResult instanceof Err<?, ?> err) {
			System.out.println("❌ Lexing FAILED: " + err.error());
		}
	}
}