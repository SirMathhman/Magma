package magma.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		System.out.println("Magma compiler (minimal pipeline)");

		String inputPath = args.length > 0 ? args[0] : "sample.mg";
		String outputPath = args.length > 1 ? args[1] : "output.c";

		if (!Files.exists(Path.of(inputPath))) {
			// create a sample source file
			Files.writeString(Path.of(inputPath), "print \"Hello from generated C!\\n\";\n");
			System.out.println("Wrote sample source to " + inputPath);
		}

		String src = Files.readString(Path.of(inputPath));

		Lexer lexer = new Lexer(src);
		List<Token> tokens = lexer.tokenize();

		Parser parser = new Parser(tokens);
		magma.compiler.ast.Program program = parser.parse();

		SemanticAnalyzer.analyze(program);

		CodeGen.generateC(program, outputPath);
		System.out.println("Generated " + outputPath);
	}
}
