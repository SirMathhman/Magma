import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaRootOnMainFileTest {

	@Test
	public void testJavaRootCanBeAppliedToMainJava() {
		assertTimeout(Duration.ofSeconds(5), () -> {
			// Read Main.java from the project
			Path mainJavaPath = Paths.get("src", "main", "java", "magma", "Main.java");
			assertTrue(Files.exists(mainJavaPath), "Main.java should exist at " + mainJavaPath);

			String mainJavaContent = Files.readString(mainJavaPath);

			// Apply JRoot() to the Main.java content
			Result<Node, CompileError> lexResult = Lang.JRoot().lex(mainJavaContent);

			// Assert that lexing succeeded
			assertTrue(lexResult instanceof Ok<?, ?>, () -> {
				if (lexResult instanceof Err<?, ?> err) {
					return "Lexing Main.java failed: " + err.error();
				}
				return "Lexing Main.java failed";
			});
		});
	}
}
