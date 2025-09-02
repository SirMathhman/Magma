package magma;

import magma.diagnostics.CompileError;
import magma.run.CExecutor;
import magma.run.Executor;
import magma.run.RunError;
import magma.run.Runner;
import magma.run.TSExecutor;
import magma.util.Err;
import magma.util.Ok;
import magma.util.Result;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {
	static final String PRELUDE = "extern fn readInt() : I32;";

	public static void assertAllValidWithPrelude(String source, String stdIn, String stdOut) {
		assertAllValid(PRELUDE + " " + source, stdIn, stdOut);
	}

	static void assertValid(Executor executor, String source, String stdIn, String stdOut) {
		Runner runner = new Runner(executor);
		Result<String, RunError> result = runner.run(source, stdIn);
		switch (result) {
			case Err(var error) -> {
				String msg = "Lang --- " + executor.getTargetLanguage() + ": " + error.toString();
				try {
					var gen = error.generatedOutput();
					if (gen != null && gen.isPresent()) {
						msg += "\nGenerated output:\n" + gen.get();
					}
				} catch (Exception e) {
					// ignore
				}
				org.junit.jupiter.api.Assertions.fail(msg);
			}
			case Ok(var value) -> {
				try {
					org.junit.jupiter.api.Assertions.assertEquals(stdOut, value,
																												"LANG " + executor.getTargetLanguage() + ": output mismatch");
				} catch (AssertionError ae) {
					// Compile the source with the compiler to get the generated units for debugging
					try {
						magma.parser.Location location = new magma.parser.Location(java.util.Collections.emptyList(), "");
						magma.ast.Unit unit = new magma.ast.Unit(location, ".mgs", source);
						java.util.Set<magma.ast.Unit> units = java.util.Collections.singleton(unit);
						magma.compiler.Compiler compiler = new magma.compiler.Compiler(executor.getTargetLanguage());
						Result<java.util.Set<magma.ast.Unit>, magma.diagnostics.CompileError> compileResult =
								compiler.compile(units);
						StringBuilder gen = new StringBuilder();
						if (compileResult instanceof Ok) {
							java.util.Set<magma.ast.Unit> cu =
									((Ok<java.util.Set<magma.ast.Unit>, magma.diagnostics.CompileError>) compileResult).value();
							for (magma.ast.Unit u : cu) {
								gen.append("=== Generated: ").append(u.location().name()).append(u.extension()).append(" ===\n");
								gen.append(u.input()).append("\n");
							}
						} else if (compileResult instanceof Err(Object error)) {
							gen.append("Compiler failed to compile: ").append(error.toString());
						}
						String msg = "Lang --- " + executor.getTargetLanguage() + ": output mismatch\n" + ae.getMessage();
						msg += "\nGenerated output:\n" + gen;
						org.junit.jupiter.api.Assertions.fail(msg);
					} catch (Exception e) {
						throw ae;
					}
				}
			}
		}
	}

	public static void assertAllValid(String source, String stdIn, String stdOut) {
		assertValid(new TSExecutor(), source, stdIn, stdOut);
		assertValid(new CExecutor(), source, stdIn, stdOut);
	}

	public static void assertAllInvalidWithPrelude(String source) {
		assertAllInvalid(PRELUDE + " " + source);
	}

	public static void assertAllInvalid(String source) {
		assertInvalid(new TSExecutor(), source);
		assertInvalid(new CExecutor(), source);
	}

	private static void assertInvalid(Executor executor, String source) {
		Result<String, RunError> result = new Runner(executor).run(PRELUDE + " " + source, "");
		if (result instanceof Err(var error)) {
			var maybeCause = error.maybeCause();
			if (maybeCause.isPresent() && maybeCause.get() instanceof CompileError) {
			} else {
				String msg = "LANG --- " + executor.getTargetLanguage() + ": Expected a compilation error.";
				try {
					var gen = error.generatedOutput();
					if (gen != null && gen.isPresent()) {
						msg += "\nGenerated output:\n" + gen.get();
					}
				} catch (Exception e) {
					// ignore
				}
				fail(msg);
			}
		} else {
			fail("LANG --- " + executor.getTargetLanguage() + ": Expected an invalid case.");
		}
	}
}
