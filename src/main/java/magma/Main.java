package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import magma.compiler.Compiler;
import magma.model.Location;
import magma.util.Tuple;

/**
 * Main entrypoint that reads all .java files under a source root, delegates to
 * {@link Compiler} to produce C/H contents, and
 * writes the generated files under a destination root while preserving the
 * package-relative folder structure.
 *
 * Usage:
 * java -cp target/classes com.example.magma.Main [srcDir] [destDir]
 * Defaults: srcDir=src/main/java, destDir=src/main/windows
 */
public class Main {
	public static void main(String[] args) throws IOException {
		Path src = Paths.get(args.length > 0 ? args[0] : "src/main/java");
		Path dst = Paths.get(args.length > 1 ? args[1] : "src/main/windows");

		if (!Files.exists(src) || !Files.isDirectory(src)) {
			System.err.println("Source directory does not exist or is not a directory: " + src);
			System.exit(1);
		}

		Files.createDirectories(dst);

		Map<Location, String> sources = new HashMap<>();

		try (Stream<Path> stream = Files.walk(src)) {
			stream.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java"))
					.forEach(p -> {
						try {
							Path rel = src.relativize(p);
							Path relParent = rel.getParent();

							List<String> pkgParts = new ArrayList<>();
							Optional.ofNullable(relParent).ifPresent(rp -> {
								for (Path part : rp) {
									pkgParts.add(part.toString());
								}
							});

							String fileName = p.getFileName().toString();
							String base = fileName.replaceFirst("\\.java$", "");
							String content = Files.readString(p);

							Location loc = new Location(pkgParts, base);
							sources.put(loc, content);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					});
		}

		// Delegate to Compiler
		Compiler compiler = new Compiler();
		Map<Location, Tuple<String, String>> compiled = compiler.compile(sources);

		// Write outputs
		for (Map.Entry<Location, Tuple<String, String>> e : compiled.entrySet()) {
			Location loc = e.getKey();
			Tuple<String, String> out = e.getValue();

			Path outDir = dst;
			for (String part : loc.packageParts()) {
				outDir = outDir.resolve(part);
			}
			Files.createDirectories(outDir);

			String base = loc.className();
			Path cPath = outDir.resolve(base + ".c");
			Path hPath = outDir.resolve(base + ".h");

			Files.writeString(cPath, out.left(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			Files.writeString(hPath, out.right(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

			System.out.println("Wrote: " + dst.relativize(hPath) + " and " + dst.relativize(cPath));
		}

		System.out.println("Generation complete.");
	}
}
