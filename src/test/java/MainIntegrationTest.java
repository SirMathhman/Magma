import magma.Main;
import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.option.Option;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MainIntegrationTest {

	@Test
	public void testSerializeDeserializeJavaRoot() {
		String input = "class Test { public int add(int a, int b) { return a + b; } }";

		Result<Node, CompileError> lex = Lang.JavaRoot().lex(input);
		assertTrue(lex instanceof Ok<?, ?>, () -> "Lexing failed: " + lex);

		Node node = ((Ok<Node, CompileError>) lex).value();

		Result<Lang.JavaRoot, CompileError> des = Serialize.deserialize(Lang.JavaRoot.class, node);
		assertTrue(des instanceof Ok<?, ?>, () -> "Deserialization failed: " + des);

		Lang.JavaRoot javaRoot = ((Ok<Lang.JavaRoot, CompileError>) des).value(); assertNotNull(javaRoot.children());
		assertFalse(javaRoot.children().isEmpty(), "JavaRoot should contain at least one child (the class)");
	}

	@Test
	public void testMainRunWritesFiles() throws Exception {
		// Create a tiny temporary java source tree with one class and run Main.run()
		Path temp = Files.createTempDirectory("magma-test-");
		Path srcMain = temp.resolve("src").resolve("main").resolve("java");
		Path windowsOut = temp.resolve("src").resolve("main").resolve("windows"); Files.createDirectories(srcMain);
		Files.createDirectories(windowsOut);

		Path pkg = srcMain.resolve("magma"); Files.createDirectories(pkg);

		// Use a non-empty method body because the grammar requires method bodies to contain content
		String sample = "package magma; public class Simple { public void m() { int x = 0; } }";
		Path sampleFile = pkg.resolve("Simple.java"); Files.writeString(sampleFile, sample);

		// Run compileAllJavaFiles via reflection since Main.run() uses current directory
		// We can't change Main.run() signature, so call the private method compileAllJavaFiles
		var compileMethod =
				Main.class.getDeclaredMethod("compileAllJavaFiles", java.nio.file.Path.class, java.nio.file.Path.class);
		compileMethod.setAccessible(true);

		Object result = compileMethod.invoke(null, srcMain, windowsOut); assertTrue(result instanceof Option<?>);
		// Expect no error (Option.empty)
		Option<?> opt = (Option<?>) result;
		assertTrue(opt.getClass().getSimpleName().equals("None"), () -> "Expected None but got: " + opt);

		// Output cpp should exist
		Path outFile = windowsOut.resolve("magma").resolve("Simple.cpp");
		assertTrue(Files.exists(outFile), () -> "Expected generated file at " + outFile);
	}
}
