package magma;

import magma.codegen.CCodeGenerator;
import magma.ast.Program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: magma <input-file> <output-file>");
			System.exit(1);
		}

		String inputPath = args[0];
		String outputPath = args[1];

		try {
			Program program = ModuleResolver.resolveMainModule(inputPath);
			CCodeGenerator generator = new CCodeGenerator();
			String compiled = generator.generate(program);
			Files.writeString(Paths.get(outputPath), compiled);
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}
}
