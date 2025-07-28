/*import magma.node.MapNode;*/
/*import magma.node.Node;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.Collections;*/
/*import java.util.List;*/
/*import java.util.stream.Collectors;*/
/*import java.util.stream.Stream;*/
/*public final*/class Main {/*private static final Path TARGET_DIRECTORY = Paths.get(".", "src", "node");

	private Main() {}

	public static void main(final String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");
		try (final Stream<Path> stream = Files.walk(sourceDirectory)) {
			final var sources = stream.filter(Files::isRegularFile)
																.filter(path -> path.toString().endsWith(".java"))
																.collect(Collectors.toSet());

			final var targets = Main.runWithSources(sources, sourceDirectory);
			Main.pruneTargets(targets);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static void pruneTargets(final Collection<Path> targets) throws IOException {
		try (final Stream<Path> stream1 = Files.walk(Main.TARGET_DIRECTORY)) {
			final var extra =
					stream1.filter(Files::isRegularFile).filter(target -> !targets.contains(target)).collect(Collectors.toSet());
			Main.deleteExtras(extra);
		}
	}

	private static void deleteExtras(final Iterable<Path> extras) throws IOException {
		for (final var path : extras) Files.deleteIfExists(path);
	}

	private static List<Path> runWithSources(final Iterable<Path> sources, final Path sourceDirectory)
			throws IOException {
		final List<Path> targets = new ArrayList<>();
		for (final var source : sources) targets.add(Main.runWithSource(source, sourceDirectory));
		return targets;
	}

	private static Path runWithSource(final Path source, final Path sourceDirectory) throws IOException {
		final var relativeParent = sourceDirectory.relativize(source.getParent());
		final var fileName = source.getFileName().toString();
		final var separator = fileName.lastIndexOf('.');
		final var name = fileName.substring(0, separator);

		final var targetParent = Main.TARGET_DIRECTORY.resolve(relativeParent);
		if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

		final var target = targetParent.resolve(name + ".ts");

		final var input = Files.readString(source);
		final var output = Main.compile(input);

		Files.writeString(target, output);
		return target;
	}

	private static String compile(final String input) {
		return Lang.createJavaRootRule().lex(input).map(Main::modify).flatMap(Lang.createTSRootRule()::generate).orElse("");
	}

	private static Node modify(final Node root) {
		final var newChildren = root.findNodeList("children")
																.orElse(Collections.emptyList())
																.stream()
																.filter(node -> !node.is("package"))
																.toList();

		return new MapNode().withNodeList("children", newChildren);
	}
*/}
