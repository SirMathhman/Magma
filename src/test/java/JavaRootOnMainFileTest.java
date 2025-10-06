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

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test that ensures JRoot() can be applied to Main.java within a 5-second
 * timeout.
 * 
 * <p>
 * This is a regression test for lexer performance. Currently, this test FAILS
 * due to
 * exponential complexity in the lexer when parsing complex files like Main.java
 * (338 lines).
 * The test uses assertTimeoutPreemptively to enforce a strict 5-second timeout.
 * 
 * <p>
 * See docs/JROOT_PERFORMANCE_ISSUE.md for full details on the performance
 * bottleneck.
 * 
 * <p>
 * <strong>Expected behavior (current):</strong> Test times out after 5 seconds.
 * <p>
 * <strong>Desired behavior (future):</strong> Test passes when lexer
 * performance is optimized.
 */
public class JavaRootOnMainFileTest {

	/**
	 * Tests that JRoot() can lex Main.java within 5 seconds.
	 * This is a VERY long process due to current lexer performance issues.
	 * 
	 * @see <a href="../../../docs/JROOT_PERFORMANCE_ISSUE.md">Performance Issue
	 *      Documentation</a>
	 */
	@Test
	public void testJavaRootCanBeAppliedToMainJava() {
		// Use assertTimeoutPreemptively to forcefully terminate if it exceeds 5 seconds
		assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
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
