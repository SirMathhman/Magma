package magma.compiler;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import magma.model.Location;
import magma.util.Tuple;
import magma.util.Result;

import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {

	@Test
	void compileEmptySourcesReturnsOk() {
		Compiler compiler = new Compiler();
		Result<Map<Location, Tuple<String, String>>, CompileError> res = compiler.compile(new HashMap<>());
		assertTrue(res.isOk(), "Expected compile to return Ok for empty input");
		Map<Location, Tuple<String, String>> out = res.getOrElse(new HashMap<>());
		assertNotNull(out);
		assertEquals(0, out.size());
	}

	@Test
	void emptyJavaSourceProducesDoNothingC() {
		Compiler compiler = new Compiler();
		Map<Location, String> sources = new HashMap<>();
		Location loc = new Location(new ArrayList<>(), "EmptyClass");
		sources.put(loc, "");

		Result<Map<Location, Tuple<String, String>>, CompileError> res = compiler.compile(sources);
		assertTrue(res.isOk(), "Expected compile to return Ok");

		Map<Location, Tuple<String, String>> out = res.getOrElse(new HashMap<>());
		assertEquals(1, out.size());

		Tuple<String, String> files = out.get(loc);
		assertNotNull(files);
		String c = files.left();
		assertTrue(c.contains("int main"), "C output should contain int main");
		assertTrue(c.contains("return 0"), "C output should return 0");
	}
}
