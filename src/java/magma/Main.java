package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
		final Path sourceDirectory = Paths.get(".", "src", "java");
		try (Stream<Path> stream = Files.walk(sourceDirectory)) {
			final Set<Path> sources = stream.filter(Files::isRegularFile)
																			.filter(path -> path.toString().endsWith(".java"))
																			.collect(Collectors.toSet());

			for (Path source : sources) {
				final String input = Files.readString(source);

				final Path relative = sourceDirectory.relativize(source);
				final Path parent = relative.getParent();
				final ArrayList<String> segments = new ArrayList<>();
				for (int i = 0; i < parent.getNameCount(); i++) {
					segments.add(parent.getName(i).toString());
				}

				Path targetDirectory = Paths.get(".", "src", "windows");
				for (String segment : segments) {
					targetDirectory = targetDirectory.resolve(segment);
				}

				Files.createDirectories(targetDirectory);

				final String fileName = relative.getFileName().toString();
				final int index = fileName.lastIndexOf(".");
				final String name = fileName.substring(0, index);
				final Path resolve = targetDirectory.resolve(name + ".c");
				Files.writeString(resolve, compile(input));
			}
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		return compileSegments(input, Main::compileRootSegment);
	}

	private static String compileSegments(String input, Function<String, String> mapper) {
		final ArrayList<String> segments = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer.setLength(0);
				continue;
			}
			if (c == '}' & depth == 1) {
				segments.add(buffer.toString());
				buffer.setLength(0);
				depth--;
				continue;
			}
			if (c == '{') depth++;
			if (c == '}') depth--;
		}
		segments.add(buffer.toString());

		return segments.stream().map(mapper).collect(Collectors.joining());
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}

	private static String compileRootSegment(String input) {
		final String stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";

		final int classIndex = stripped.indexOf("class ");
		if (classIndex >= 0) {
			final String modifiers = stripped.substring(0, classIndex);
			final String remainder = stripped.substring(classIndex + "class ".length());
			final int i = remainder.indexOf("{");
			if (i >= 0) {
				final String name = remainder.substring(0, i).strip();
				final String content = remainder.substring(i + "{".length());
				return wrap(modifiers) + "struct " + name + " {};" + System.lineSeparator() +
							 compileSegments(content, Main::compileClassSegment);
			}
		}

		return wrap(stripped);
	}

	private static String compileClassSegment(String input) {
		final int i = input.indexOf("(");
		if (i >= 0) {
			final String definition = input.substring(0, i);
			final String withParams = input.substring(i + "(".length());
			final int i1 = withParams.indexOf(")");
			if (i1 >= 0) {
				final String params = withParams.substring(0, i1);
				final String content = withParams.substring(i1 + ")".length()).strip();
				if (content.startsWith("{") && content.endsWith("}")) {
					final String slice = content.substring(1, content.length() - 1);
					final Optional<String> s = compileDefinition(definition);
					if (s.isPresent()) {
						return s.get() + "(" + compileDefinition(params).orElseGet(() -> wrap(params)) + "){" +
									 compileSegments(slice, Main::compileFunctionSegment) + "}" + System.lineSeparator();
					}
				}
			}
		}

		return wrap(input);
	}

	private static Optional<String> compileDefinition(String input) {
		final String stripped = input.strip();
		final int i = stripped.lastIndexOf(" ");
		if (i < 0) return Optional.empty();

		final String beforeName = stripped.substring(0, i).strip();
		final String name = stripped.substring(i + " ".length());

		final int i1 = beforeName.lastIndexOf(" ");
		if (i1 < 0) {
			return Optional.of(compileType(beforeName) + " " + name);
		}

		final String modifiers = beforeName.substring(0, i1);
		final String type = beforeName.substring(i1 + " ".length());

		return Optional.of(wrap(modifiers) + " " + compileType(type) + " " + name);
	}

	private static String compileType(String input) {
		final String stripped = input.strip();

		if (stripped.equals("void")) return "void";
		if (stripped.equals("String")) return "char*";

		if (stripped.endsWith("[]")) {
			final String slice = stripped.substring(0, stripped.length() - "[]".length());
			return compileType(slice) + "*";
		}

		return wrap(stripped);
	}

	private static String compileFunctionSegment(String input) {
		final String stripped = input.strip();
		return System.lineSeparator() + "\t" + compileFunctionSegmentValue(stripped);
	}

	private static String compileFunctionSegmentValue(String input) {
		if (input.endsWith(";")) {
			final String slice = input.substring(0, input.length() - ";".length());
			final int i = slice.indexOf("=");
			if (i >= 0) {
				final String definition = slice.substring(0, i);
				final String value = slice.substring(i + "=".length());
				final Optional<String> s = compileDefinition(definition);
				if (s.isPresent()) {
					return s.get() + " = " + compileExpression(value) + ";";
				}
			}
		}

		return wrap(input);
	}

	private static String compileExpression(String value) {
		final String stripped = value.strip();
		if (stripped.endsWith(")")) {
			final String slice = stripped.substring(0, stripped.length() - ")".length());
			final int i = slice.indexOf("(");
			if (i >= 0) {
				final String caller = slice.substring(0, i);
				final String arguments = slice.substring(i + "(".length());
				return wrap(caller) + "(" + wrap(arguments) + ")";
			}
		}

		return wrap(stripped);
	}
}
