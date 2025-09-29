package magma.compiler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		System.out.println("Magma compiler (minimal pipeline)");

		String inputPath = args.length > 0 ? args[0] : "sample.mg";
		String outputPath = args.length > 1 ? args[1] : "output.c";

		try {
			if (!Files.exists(Path.of(inputPath))) {
				// create a sample source file
				Files.writeString(Path.of(inputPath), "print \"Hello from generated C!\\n\";\n");
				System.out.println("Wrote sample source to " + inputPath);
			}

			String src = Files.readString(Path.of(inputPath));

			Lexer lexer = new Lexer(src);
			List<Token> tokens = lexer.tokenize();

			Parser parser = new Parser(tokens);
			Result<magma.compiler.ast.Program, String> pr = parser.parse();
			if (pr instanceof Err<?, ?> perr) {
				System.err.println("Parse error: " + perr.asErrorOptional().get());
				return;
			}
			magma.compiler.ast.Program program = ((Ok<magma.compiler.ast.Program, String>) pr).asOptional().get();

			SemanticAnalyzer.analyze(program);

			Result<Unit, java.io.IOException> cg = CodeGen.generateC(program, outputPath);
			if (cg instanceof Err<?, ?> cerr) {
				System.err.println("Codegen error: " + cerr.asErrorOptional().get());
				return;
			}
			System.out.println("Generated " + outputPath);
		} catch (java.io.IOException ex) {
			System.err.println("I/O error: " + ex.getMessage());
			return;
		}
	}
}
