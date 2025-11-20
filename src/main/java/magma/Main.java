package magma;

import magma.ast.Node;
import magma.codegen.CCodeGenerator;
import magma.lexer.Lexer;
import magma.lexer.Token;
import magma.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: magma <input-file> <output-file>");
			System.exit(1);
		}

		String inputPath = args[0];
		String outputPath = args[1];

		try {
			String source = Files.readString(Paths.get(inputPath));
			String compiled = compile(source);
			Files.writeString(Paths.get(outputPath), compiled);
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}

	private static String compile(String source) {
		Lexer lexer = new Lexer(source);
		List<Token> tokens = lexer.tokenize();

		Parser parser = new Parser(tokens);
		Node ast = parser.parse();

		CCodeGenerator generator = new CCodeGenerator();
		String expression = generator.generate(ast);

		return "// Compiled from Magma source\n"
			+ "#include <stdio.h>\n\n"
			+ "int main(void) {\n"
			+ "    int result = " + expression + ";\n"
			+ "    return result;\n"
			+ "}\n";
	}
}
