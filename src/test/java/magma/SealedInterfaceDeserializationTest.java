package magma;

import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.*;

public class SealedInterfaceDeserializationTest {

	@Test
	public void testRecordDeserializationDirectly() {
		String input = """
				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Direct Record Deserialization ===");

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		assertTrue(lexResult instanceof Ok<?, ?>, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			magma.compile.Node rootNode = lexOk.value();
			System.out
					.println("Root node children count: " + rootNode.findNodeList("children").map(list -> list.size()).orElse(0));

			// Find the record node specifically
			if (rootNode.findNodeList("children") instanceof Some<?> some) {
				@SuppressWarnings("unchecked")
				List<magma.compile.Node> children = (List<magma.compile.Node>) some.value();
				children.forEach(child -> {
					System.out.println("Child @type: " + child.maybeType);
					if (child.is("record")) {
						System.out.println("Found record node! Trying to deserialize as Record class directly...");

						Result<magma.compile.Lang.Record, CompileError> recordResult = Serialize
								.deserialize(magma.compile.Lang.Record.class, child);
						if (recordResult instanceof Ok<magma.compile.Lang.Record, CompileError> recordOk) {
							System.out.println("✅ Record deserialization SUCCESS");
							System.out.println("Record name: " + recordOk.value().name());
							System.out.println("Record children: " + recordOk.value().children().size());
						} else if (recordResult instanceof Err<magma.compile.Lang.Record, CompileError> recordErr) {
							System.out.println("❌ Record deserialization FAILED: " + recordErr.error());
						}

						System.out.println("Now trying to deserialize as JStructure interface...");
						Result<JStructure, CompileError> jStructResult = Serialize.deserialize(JStructure.class, child);
						if (jStructResult instanceof Ok<JStructure, CompileError> jStructOk) {
							System.out.println("✅ JStructure deserialization SUCCESS");
							System.out.println("JStructure actual type: " + jStructOk.value().getClass().getSimpleName());
							System.out.println("JStructure name: " + jStructOk.value().name());
						} else if (jStructResult instanceof Err<JStructure, CompileError> jStructErr) {
							System.out.println("❌ JStructure deserialization FAILED: " + jStructErr.error());
						}

						System.out.println("Finally trying to deserialize as JavaRootSegment interface...");
						Result<JavaRootSegment, CompileError> segmentResult = Serialize.deserialize(JavaRootSegment.class, child);
						if (segmentResult instanceof Ok<JavaRootSegment, CompileError> segmentOk) {
							System.out.println("✅ JavaRootSegment deserialization SUCCESS");
							System.out.println("Segment actual type: " + segmentOk.value().getClass().getSimpleName());
						} else if (segmentResult instanceof Err<JavaRootSegment, CompileError> segmentErr) {
							System.out.println("❌ JavaRootSegment deserialization FAILED: " + segmentErr.error());
						}
					}
				});
			}
		}
	}

	@Test
	public void testJavaRootWithRecordDeserialization() {
		String input = """
				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing JavaRoot with Record ===");

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		assertTrue(lexResult instanceof Ok<?, ?>, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			System.out.println("Trying to deserialize full JavaRoot...");
			Result<JavaRoot, CompileError> javaRootResult = Serialize.deserialize(JavaRoot.class, lexOk.value());

			if (javaRootResult instanceof Ok<JavaRoot, CompileError> javaRootOk) {
				System.out.println("✅ JavaRoot deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + javaRootOk.value().children().size());
				javaRootOk.value().children().forEach(child -> {
					System.out.println("  Child type: " + child.getClass().getSimpleName());
					if (child instanceof magma.compile.Lang.Record record) {
						System.out.println("    ✅ Found Record: " + record.name());
					}
				});
			} else if (javaRootResult instanceof Err<JavaRoot, CompileError> javaRootErr) {
				System.out.println("❌ JavaRoot deserialization FAILED: " + javaRootErr.error());
			}
		}
	}
}