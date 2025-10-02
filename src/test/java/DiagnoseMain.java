import magma.Main;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.*;

public class DiagnoseMain {

	@Test
	public void diagnoseMainCompilation() throws Exception {
		Path mainJavaPath = Paths.get("src", "main", "java", "magma", "Main.java");
		String mainSource = Files.readString(mainJavaPath);

		System.out.println("=== Diagnosing Main.java compilation ===");
		System.out.println("Source length: " + mainSource.length() + " characters");
		System.out.println("First 200 chars: " + mainSource.substring(0, Math.min(200, mainSource.length())));

		// Step 1: Lex
		Result<Node, CompileError> lexResult = JRoot().lex(mainSource); if (lexResult instanceof Err<?, ?>(? error)) {
			System.err.println("❌ LEXING FAILED: " + error); fail("Lexing failed: " + error);
		}

		assertInstanceOf(Ok<?, ?>.class, lexResult, "Lexing should succeed");
		Node lexedNode = ((Ok<Node, CompileError>) lexResult).value();
		System.out.println("\n✅ Lexing succeeded"); System.out.println("\nLexed node structure (first 5000 chars):");
		String formatted = lexedNode.format(0);
		System.out.println(formatted.substring(0, Math.min(5000, formatted.length()))); if (formatted.length() > 5000)
			System.out.println("... (truncated, total length: " + formatted.length() + ")");

		// Step 2: Deserialize to JavaRoot
		Result<JavaRoot, CompileError> deserializeResult = Serialize.deserialize(JavaRoot.class, lexedNode);
		if (deserializeResult instanceof Err<?, ?>(? error)) {
			System.err.println("❌ DESERIALIZATION FAILED: " + error); fail("Deserialization failed: " + error);
		}

		assertInstanceOf(Ok<?, ?>.class, deserializeResult, "Deserialization should succeed");
		JavaRoot javaRoot = ((Ok<JavaRoot, CompileError>) deserializeResult).value();
		System.out.println("✅ Deserialization succeeded");
		System.out.println("JavaRoot children count: " + javaRoot.children().size());

		// Check what children we got
		javaRoot.children().forEach(child -> {
			System.out.println("  Child type: " + child.getClass().getSimpleName()); if (child instanceof JClass jClass) {
				System.out.println("    Class name: " + jClass.name());
				System.out.println("    Class children count: " + jClass.children().size());
				jClass.children().forEach(structChild -> {
					System.out.println("      Structure child type: " + structChild.getClass().getSimpleName());
					if (structChild instanceof Method method)
						System.out.println("        Method name: " + method.definition().name());
				});
			}
		});

		// Step 3: Transform
		Result<CRoot, CompileError> transformResult = Main.transform(javaRoot);
		if (transformResult instanceof Err<?, ?>(? error)) {
			System.err.println("❌ TRANSFORMATION FAILED: " + error); fail("Transformation failed: " + error);
		}

		assertInstanceOf(Ok<?, ?>.class, transformResult, "Transformation should succeed");
		CRoot cRoot = ((Ok<CRoot, CompileError>) transformResult).value();
		System.out.println("\n✅ Transformation succeeded");
		System.out.println("CRoot children count: " + cRoot.children().size());

		cRoot.children().forEach(child -> {
			System.out.println("  CRoot child type: " + child.getClass().getSimpleName());
			if (child instanceof Structure struct) {
				System.out.println("    Structure name: " + struct.name());
				System.out.println("    Structure fields count: " + struct.fields().size());
			} else if (child instanceof Function func) System.out.println("    Function name: " + func.definition().name());
		});

		// Step 4: Serialize to C++
		Result<Node, CompileError> serializeResult = Serialize.serialize(CRoot.class, cRoot);
		if (serializeResult instanceof Err<?, ?>(? error)) {
			System.err.println("❌ SERIALIZATION FAILED: " + error); fail("Serialization failed: " + error);
		}

		assertInstanceOf(Ok<?, ?>.class, serializeResult, "Serialization should succeed");
		System.out.println("✅ Serialization succeeded");

		// Step 5: Generate C++ code
		Node serializedNode = ((Ok<Node, CompileError>) serializeResult).value();
		Result<String, CompileError> generateResult = CRoot().generate(serializedNode);
		if (generateResult instanceof Err<?, ?>(? error)) {
			System.err.println("❌ GENERATION FAILED: " + error); fail("Generation failed: " + error);
		}

		assertInstanceOf(Ok<?, ?>.class, generateResult, "Generation should succeed");
		String generated = ((Ok<String, CompileError>) generateResult).value();
		System.out.println("\n✅ Generation succeeded");
		System.out.println("Generated C++ length: " + generated.length() + " characters");
		System.out.println("Generated C++:"); System.out.println(generated);

		// The issue: Main.java has many methods, but generated C++ might only have
		// "struct Main{};"
		assertTrue(generated.length() > 20, "Generated C++ should be more than just 'struct Main{};'");
	}
}
