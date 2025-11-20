package magma;

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
			String source = Files.readString(Paths.get(inputPath));
			String compiled = compile(source);
			Files.writeString(Paths.get(outputPath), compiled);
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}

	private static String compile(String source) {
		return "// Compiled from Magma source\n#include <stdio.h>\n\nint main(void) {\n    return 0;\n}\n";
	}
}
