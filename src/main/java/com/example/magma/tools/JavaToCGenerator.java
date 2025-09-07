package com.example.magma.tools;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

/**
 * Small utility that reads all .java files under a source root and generates
 * corresponding .h and .c stub files under a destination root while
 * preserving the relative folder structure.
 *
 * Usage:
 * java -cp target/classes com.example.magma.tools.JavaToCGenerator [srcDir]
 * [destDir]
 * Defaults: srcDir=src/main/java, destDir=src/main/windows
 */
public class JavaToCGenerator {
	public static void main(String[] args) throws IOException {
		Path src = Paths.get(args.length > 0 ? args[0] : "src/main/java");
		Path dst = Paths.get(args.length > 1 ? args[1] : "src/main/windows");

		if (!Files.exists(src) || !Files.isDirectory(src)) {
			System.err.println("Source directory does not exist or is not a directory: " + src);
			System.exit(1);
		}

		Files.createDirectories(dst);

		try (Stream<Path> stream = Files.walk(src)) {
			stream.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java"))
					.forEach(p -> {
						try {
							process(p, src, dst);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
		}

		System.out.println("Generation complete.");
	}

	private static void process(Path javaFile, Path srcRoot, Path dstRoot) throws IOException {
		Path rel = srcRoot.relativize(javaFile);
		Path relParent = rel.getParent();
		Path outDir = (relParent == null) ? dstRoot : dstRoot.resolve(relParent.toString());
		Files.createDirectories(outDir);

		String fileName = javaFile.getFileName().toString();
		// replaceFirst regex: literal dot is escaped as "\\." in Java string
		String base = fileName.replaceFirst("\\.java$", "");

		Path header = outDir.resolve(base + ".h");
		Path cfile = outDir.resolve(base + ".c");

		String guard = makeGuard(rel);

		String headerContent = "/* Generated from " + rel.toString().replace('\\', '/') + " */\n"
				+ "#ifndef " + guard + "\n"
				+ "#define " + guard + "\n\n"
				+ "/* TODO: translate Java class '" + base + "' to C declarations */\n\n"
				+ "#endif /* " + guard + " */\n";

		String cContent = "/* Generated from " + rel.toString().replace('\\', '/') + " */\n"
				+ "#include \"" + base + ".h\"\n\n"
				+ "/* TODO: implement translated functions for Java class '" + base + "' */\n";

		Files.writeString(header, headerContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		Files.writeString(cfile, cContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

		System.out.println("Wrote: " + dstRoot.relativize(header) + " and " + dstRoot.relativize(cfile));
	}

	private static String makeGuard(Path rel) {
		String s = rel.toString().replace(File.separatorChar, '_').replace('.', '_').replace('-', '_');
		s = s.replaceAll("[^A-Za-z0-9_]", "_");
		s = s.toUpperCase();
		if (!s.endsWith("_H")) {
			s = s + "_H";
		}
		return s;
	}
}
