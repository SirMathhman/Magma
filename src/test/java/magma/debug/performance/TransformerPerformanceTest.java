package magma.debug.performance;

import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TransformerPerformanceTest {
	@Test
	void test() throws IOException {
		final Path path =
				Paths.get("C:\\Users\\mathm\\IdeaProjects\\Magma\\src\\main\\java\\magma\\transform\\Transformer.java");
		final String input = Files.readString(path);
		assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
			switch (Lang.JRoot().lex(input)) {
				case Err<Node, CompileError> v -> fail(v.error().display());
				case Ok<Node, CompileError> v -> assertNotNull(v.value());
			}
		});
	}
}
