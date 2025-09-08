package magma;

import magma.simple.SimpleLexer;
import magma.simple.SimpleParser;
import magma.simple.SimpleTypeChecker;
import magma.simple.SimpleCodeEmitter;
import magma.ir.SimpleIrBuilder;
import magma.ir.IrNode;
import magma.ir.IrLiteral;
import magma.ir.IrBinary;

/**
 * A tiny CLI entrypoint for the stubbed Magma project.
 * Prints help and exits normally.
 * See docs/architecture.md for design.
 */
public final class CLI {
	/**
	 * Run the CLI with the given arguments and return an appropriate exit code.
	 * This is separated out so tests can call it without terminating the JVM.
	 */
	public static int run(String[] args) {
		if (args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
			System.out.println("magma-cli (stub)\nusage: magma [options] <file>");
			return 0;
		}

		// If the first argument is a numeric literal (program source inline),
		// compile a tiny C program that returns that number and run it. This
		// exercises the emitter+toolchain end-to-end for the simplest case.
		try {
			String raw = args[0];
			// Accept a trailing I32 suffix (e.g., "5I32") as a 32-bit integer literal.
			if (raw.endsWith("I32") || raw.endsWith("i32")) {
				raw = raw.substring(0, raw.length() - 3);
			}

			long val = Long.parseLong(raw);

			// Generate a temporary C source file.
			java.nio.file.Path tmpDir = java.nio.file.Files.createTempDirectory("magma-run-");
			java.nio.file.Path cFile = tmpDir.resolve("prog.c");
			java.nio.file.Path exeFile = tmpDir.resolve("prog");

			String cSource = "#include <stdlib.h>\nint main() { return (int)" + val + "; }\n";
			java.nio.file.Files.writeString(cFile, cSource);

			// Try to find clang (or cc) on PATH.
			String compiler = null;
			if (isExecutableOnPath("clang"))
				compiler = "clang";
			else if (isExecutableOnPath("cc"))
				compiler = "cc";

			if (compiler == null) {
				System.err.println("magma: no C compiler found (clang/cc). Falling back to literal exit.");
				return (int) (val & 0xFF);
			}

			// Compile: clang -o prog prog.c
			ProcessBuilder pbCompile = new ProcessBuilder(compiler, "-o", exeFile.toString(), cFile.toString());
			pbCompile.redirectErrorStream(true);
			pbCompile.redirectOutput(java.lang.ProcessBuilder.Redirect.INHERIT);
			Process pc = pbCompile.start();
			int compileCode = pc.waitFor();
			if (compileCode != 0) {
				System.err.println("magma: C compilation failed (code=" + compileCode + "), falling back to literal return");
				return (int) (val & 0xFF);
			}

			// Make executable on platforms that need it (Windows will still run .exe; here
			// exeFile has no .exe extension but clang on Windows typically produces
			// exeFile.exe)
			java.nio.file.Path exeToRun = exeFile;
			if (System.getProperty("os.name").toLowerCase().contains("win")) {
				java.nio.file.Path exeWin = tmpDir.resolve("prog.exe");
				if (java.nio.file.Files.exists(exeWin))
					exeToRun = exeWin;
			}

			// Run the compiled program and return its exit code.
			ProcessBuilder pbRun = new ProcessBuilder(exeToRun.toString());
			pbRun.directory(tmpDir.toFile());
			pbRun.redirectOutput(java.lang.ProcessBuilder.Redirect.INHERIT);
			pbRun.redirectError(java.lang.ProcessBuilder.Redirect.INHERIT);
			Process pr = pbRun.start();
			int rc = pr.waitFor();

			// Clean up temp files (best-effort).
			try {
				java.nio.file.Files.deleteIfExists(cFile);
				java.nio.file.Files.deleteIfExists(exeToRun);
				java.nio.file.Files.deleteIfExists(tmpDir);
			} catch (Exception ignore) {
			}

			return rc;
		} catch (NumberFormatException e) {
			// Not a plain integer; try the simple pipeline: lex -> parse -> typecheck -> ir -> emit
			String src = args[0];
			Lexer lexer = new SimpleLexer();
			Parser parser = new SimpleParser();
			TypeChecker tc = new SimpleTypeChecker();
			SimpleIrBuilder irb = new SimpleIrBuilder();
			CodeEmitter emitter = new SimpleCodeEmitter();

			Token[] toks = lexer.tokenize(src);
			AstNode ast = parser.parse(toks);
			if (ast == null) {
				System.out.println("magma: received " + args.length + " argument(s)");
				return 0;
			}
			if (!tc.check(ast)) {
				System.err.println("magma: type check failed");
				return 1;
			}
			IrNode ir = irb.build(ast);
			if (ir instanceof IrLiteral) {
				int v = ((IrLiteral) ir).value;
				emitter.emit(ir);
				return v & 0xFF;
			}
			if (ir instanceof IrBinary) {
				IrBinary bin = (IrBinary) ir;
				int sum = bin.left.value + bin.right.value;
				emitter.emit(ir);
				return sum & 0xFF;
			}
			// Fallback generic message
			System.out.println("magma: received " + args.length + " argument(s)");
			return 0;
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			return 1;
		} catch (Exception ex) {
			System.err.println("magma: unexpected error while running C emitter: " + ex.getMessage());
			return 1;
		}
	}

	public static void main(String[] args) {
		System.exit(run(args));
	}

	private static boolean isExecutableOnPath(String exe) {
		String path = System.getenv("PATH");
		if (path == null)
			return false;
		String[] parts = path.split(java.io.File.pathSeparator);
		for (String p : parts) {
			java.io.File f = new java.io.File(p,
					exe + (System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : ""));
			if (f.exists() && f.canExecute())
				return true;
		}
		return false;
	}
}
