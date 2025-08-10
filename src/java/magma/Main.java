package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");
		try (final var stream = Files.walk(sourceDirectory)) {
			runWithSources(stream, sourceDirectory);
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static void runWithSources(Stream<Path> stream, Path sourceDirectory) throws IOException {
		final var sources = stream.filter(Files::isRegularFile)
															.filter(path -> path.toString().endsWith(".java"))
															.collect(Collectors.toSet());

		for (var source : sources) runWithSource(source, sourceDirectory);
	}

	private static void runWithSource(Path source, Path sourceDirectory) throws IOException {
		final var relative = sourceDirectory.relativize(source);
		final var relativeParent = relative.getParent();
		final var fileName = relative.getFileName().toString();
		final var extensionSeparator = fileName.lastIndexOf(".");
		if (extensionSeparator < 0) return;

		final var name = fileName.substring(0, extensionSeparator);
		final var targetDirectory = Paths.get(".", "src", "node");
		final var targetParent = targetDirectory.resolve(relativeParent);
		if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

		final var target = targetParent.resolve(name + ".ts");
		final var input = Files.readString(source);

		final var output = divide(input).map(Main::compileRootSegment).collect(Collectors.joining());

		Files.writeString(target, output);
	}

	private static String compileRootSegment(String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ")) return "";
		return compileRootSegmentValue(strip) + System.lineSeparator();
	}

	private static String compileRootSegmentValue(String input) {
		return compileClass(input).orElseGet(() -> wrap(input));
	}

	private static Optional<String> compileClass(String input) {
		final var classIndex = input.indexOf("class ");
		if (classIndex < 0) return Optional.empty();
		final var modifiers = input.substring(0, classIndex);
		final var afterClass = input.substring(classIndex + "class ".length());
		final var modifiers1 = new MapNode().withString("modifiers", modifiers);

		final var contentStart = afterClass.indexOf("{");
		if (contentStart < 0) return Optional.empty();
		final var beforeContent = afterClass.substring(0, contentStart).strip();
		final var substring = afterClass.substring(contentStart + "{".length());
		final var other = new MapNode().withString("before-content", beforeContent);
		final var merge = modifiers1.merge(other);

		return new StripRule(new SuffixRule(new StringRule("content"), "}")).lex(substring).map(other1 -> {
			return generate(merge.merge(other1));
		});
	}

	private static String generate(MapNode mapNode) {
		return wrap(mapNode.find("modifiers").orElse("")) + "class " + mapNode.find("before-content").orElse("") + " {" +
					 wrap(mapNode.find("content").orElse("")) + "}";
	}

	private static Stream<String> divide(String input) {
		var current = new State();
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			current = fold(current, c);
		}
		return current.advance().stream();
	}

	private static State fold(State current, char c) {
		final var appended = current.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
