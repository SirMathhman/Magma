package magma;

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
import magma.util.Result;

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
	public static void main(String[] args) {
		Path src = Paths.get(args.length > 0 ? args[0] : "src/main/java");
		Path dst = Paths.get(args.length > 1 ? args[1] : "src/main/windows");

		if (!Files.exists(src) || !Files.isDirectory(src)) {
			System.err.println("Source directory does not exist or is not a directory: " + src);
			System.exit(1);
		}

		Result<Path, String> mk = createDirs(dst);
		if (mk.isErr()) {
			System.err.println("Failed to create destination directory: " + mk.getErrOrElse("absent"));
			System.exit(1);
		}

		Result<Map<Location, String>, String> collected = collectSources(src);
		if (collected.isErr()) {
			System.err.println("Failed to collect sources: " + collected.getErrOrElse("absent"));
			System.exit(1);
		}

		Map<Location, String> sources = collected.getOrElse(new HashMap<>());

		// Delegate to Compiler
		Compiler compiler = new Compiler();
		Map<Location, Tuple<String, String>> compiled = compiler.compile(sources);

		Result<Path, String> wrote = writeOutputs(compiled, dst);
		if (wrote.isErr()) {
			System.err.println("Failed to write outputs: " + wrote.getErrOrElse("absent"));
			System.exit(1);
		}

		System.out.println("Generation complete.");
	}

	private static Result<Path, String> createDirs(Path dst) {
		try {
			Files.createDirectories(dst);
			return new Result.Ok<>(dst);
		} catch (java.io.IOException ex) {
			return new Result.Err<>(ex.toString());
		}
	}

	private static Result<Map<Location, String>, String> collectSources(Path src) {
		Map<Location, String> sources = new HashMap<>();
		List<Path> javaFiles = new ArrayList<>();

		// First collect matching paths (this may still throw IOException)
		try (Stream<Path> stream = Files.walk(src)) {
			stream.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java")).forEach(javaFiles::add);
		} catch (java.io.IOException ex) {
			return new Result.Err<>(ex.toString());
		}

		// Now read files sequentially so IO failures can be handled and returned as
		// Result.Err
		for (Path p : javaFiles) {
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
			} catch (java.io.IOException ex) {
				return new Result.Err<>(ex.toString());
			}
		}

		return new Result.Ok<>(sources);
	}

	private static Result<Path, String> writeOutputs(Map<Location, Tuple<String, String>> compiled, Path dst) {
		try {
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

			return new Result.Ok<>(dst);
		} catch (java.io.IOException ex) {
			return new Result.Err<>(ex.toString());
		}
	}
}
