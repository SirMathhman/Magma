package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Main {
	private interface List<T> {
		List<T> add(T element);

		Stream<T> stream();
	}

	private record JavaList<T>(java.util.List<T> elements) implements Main.List<T> {
		private JavaList() {
			this(new ArrayList<>());
		}

		@Override
		public List<T> add(final T element) {
			this.elements.add(element);
			return this;
		}

		@Override
		public Stream<T> stream() {
			return this.elements.stream();
		}
	}

	private static final class Lists {
		static <T> List<T> empty() {
			return new JavaList<>();
		}

		static <T> List<T> of(final T... values) {
			return new JavaList<>(new ArrayList<>(Arrays.asList(values)));
		}
	}

	private static class DivideState {
		private List<String> segments = Lists.empty();
		private String buffer = "";
		private int depth = 0;

		private DivideState advance() {
			this.segments = this.segments.add(this.buffer);
			this.buffer = "";
			return this;
		}

		private DivideState append(final char c) {
			this.buffer = this.buffer + c;
			return this;
		}

		private boolean isLevel() {
			return 0 == this.depth;
		}

		private DivideState exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private DivideState enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}

		final boolean isShallow() {
			return 1 == this.depth;
		}
	}

	private record ParseState(List<JavaStructure> javaStructures, List<CStructure> cStructures, List<String> functions,
														List<String> visited, List<String> typeArguments, List<String> typeParameters) {
		private ParseState() {
			this(Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty(), Lists.empty());
		}

		ParseState addCStructure(final CStructure generated) {
			return new ParseState(this.javaStructures, this.cStructures.add(generated), this.functions, this.visited,
														this.typeArguments, this.typeParameters);
		}

		ParseState addFunction(final String generated) {
			return new ParseState(this.javaStructures, this.cStructures, this.functions.add(generated), this.visited,
														this.typeArguments, this.typeParameters);
		}

		ParseState addJavaStructure(final JavaStructure javaStructure) {
			return new ParseState(this.javaStructures.add(javaStructure), this.cStructures, this.functions, this.visited,
														this.typeArguments, this.typeParameters);
		}

		ParseState addVisited(final String name) {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited.add(name),
														this.typeArguments, this.typeParameters);
		}

		ParseState withArgument(final String argument) {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited, Lists.of(argument),
														this.typeParameters);
		}

		ParseState withTypeParameters(final String values) {
			return new ParseState(this.javaStructures, this.cStructures, this.functions, this.visited, this.typeArguments,
														Lists.of(values));
		}
	}

	private record Tuple<Left, Right>(Left left, Right right) {}

	private record CStructure(String modifiers, String name, String content) {
		private String generate() {
			return Main.generatePlaceholder(this.modifiers()) + "struct " + this.name() + " {" + this.content() +
						 System.lineSeparator() + "};" + System.lineSeparator();
		}
	}

	private record JavaStructure(String type, String modifiers, String name, String typeParameters, String content) {

	}

	private Main() {}

	public static void main(final String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));

			final var targetParent = Paths.get(".", "src", "windows", "magma");
			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

			final var target = targetParent.resolve("Main.c");

			final var joined = Main.compile(input);
			Files.writeString(target, joined);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(final CharSequence input) {
		final var tuple = Main.compileStatements(new ParseState(), input, Main::compileRootSegment);
		final var newState = tuple.left;
		final var joined = newState.cStructures.stream().map(CStructure::generate).collect(Collectors.joining()) +
											 newState.functions.stream().collect(Collectors.joining());

		return joined + tuple.right;
	}

	private static Tuple<ParseState, String> compileStatements(final ParseState state,
																														 final CharSequence input,
																														 final BiFunction<ParseState, String, Tuple<ParseState, String>> mapper) {
		final var current = Main.divide(input).stream().reduce(new Tuple<>(state, ""), (tuple, s) -> {
			final var tuple0 = mapper.apply(tuple.left, s);
			final var append = tuple.right + tuple0.right;
			return new Tuple<>(tuple0.left, append);
		}, (_, next) -> next);

		return new Tuple<>(current.left, current.right);
	}

	private static Tuple<ParseState, String> compileRootSegment(final ParseState state, final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return new Tuple<>(state, "");

		final var tuple = Main.compileRootSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right);
	}

	private static Tuple<ParseState, String> compileRootSegmentValue(final ParseState state, final String input) {
		return Main.compileClass(state, input).orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}

	private static Optional<Tuple<ParseState, String>> compileClass(final ParseState state, final String input) {
		return Main.compileStructure("class", state, input)
							 .or(() -> Main.compileStructure("interface", state, input))
							 .or(() -> Main.compileStructure("record", state, input));
	}

	private static Optional<Tuple<ParseState, String>> compileStructure(final String type,
																																			final ParseState state,
																																			final String input) {
		final var classIndex = input.indexOf(type + " ");
		if (0 > classIndex) return Optional.empty();
		final var before = input.substring(0, classIndex);
		final var after = input.substring(classIndex + (type + " ").length());

		final var i = after.indexOf('{');
		if (0 > i) return Optional.empty();
		final var name = after.substring(0, i).strip();
		final var withEnd = after.substring(i + 1).strip();

		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var content = withEnd.substring(0, withEnd.length() - 1);

		final var parseState = Main.assembleStructure(state, before, name, content, type);
		return Optional.of(new Tuple<>(parseState, ""));
	}

	private static ParseState assembleStructure(final ParseState state,
																							final String modifiers,
																							final String beforeContent,
																							final String content,
																							final String type) {
		final var strip = beforeContent.strip();
		if (!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)) {
			final var withoutEnd = beforeContent.substring(0, strip.length() - 1);
			final var i = withoutEnd.indexOf('<');
			if (0 <= i) {
				final var name = withoutEnd.substring(0, i);
				final var typeParameters = withoutEnd.substring(i + 1);

				return state.addJavaStructure(new JavaStructure(type, modifiers, name, typeParameters, content));
			}
		}

		return Main.attachStructure(state, modifiers, beforeContent, content, type);
	}

	private static ParseState attachStructure(final ParseState state,
																						final String modifiers,
																						final String name,
																						final CharSequence content,
																						final CharSequence type) {
		if (state.visited.stream().anyMatch(name::contentEquals)) return state;
		final var withVisited = state.addVisited(name);

		final var tuple = Main.compileStatements(withVisited, content, Main::compileClassSegment);
		final var outputContent = tuple.right;

		final var generated = new CStructure(modifiers, name, Main.createBeforeContent(type) + outputContent);
		return tuple.left.addCStructure(generated).addFunction(Main.generateConstructor(name));
	}

	private static String createBeforeContent(final CharSequence type) {
		if ("interface".contentEquals(type)) return System.lineSeparator() + "\tvoid* impl;";
		return "";
	}

	private static String generateConstructor(final String name) {
		return "struct " + name + " " + name + "(){" + System.lineSeparator() + "\tstruct " + name + " this;" +
					 System.lineSeparator() + "\treturn this;" + System.lineSeparator() + "}";
	}

	private static Tuple<ParseState, String> compileClassSegment(final ParseState state, final String input) {
		final var strip = input.strip();
		if (strip.isEmpty()) return new Tuple<>(state, "");

		final var tuple = Main.compileClassSegmentValue(state, strip);
		return new Tuple<>(tuple.left, tuple.right);
	}

	private static Tuple<ParseState, String> compileClassSegmentValue(final ParseState state, final String input) {
		return Main.compileClass(state, input)
							 .or(() -> Main.compileField(state, input))
							 .or(() -> Main.compileMethod(state, input))
							 .orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(input)));
	}

	private static Optional<Tuple<ParseState, String>> compileMethod(final ParseState state, final String input) {
		final var index = input.indexOf('(');
		if (0 <= index) {
			final var definition = input.substring(0, index);
			final var withParams = input.substring(index + 1);
			final var tuple = Main.compileDefinition(state, definition);
			final var generated = tuple.right + "(" + Main.generatePlaceholder(withParams) + System.lineSeparator();
			return Optional.of(new Tuple<>(tuple.left.addFunction(generated), ""));
		}

		return Optional.empty();
	}

	private static Optional<Tuple<ParseState, String>> compileField(final ParseState state, final String input) {
		if (!input.isEmpty() && ';' == input.charAt(input.length() - 1)) {
			final var withoutEnd = input.substring(0, input.length() - ";".length());
			final var i = withoutEnd.indexOf('=');
			if (0 <= i) {
				final var substring = withoutEnd.substring(0, i);
				final var tuple = Main.compileDefinition(state, substring);
				return Optional.of(new Tuple<>(tuple.left, System.lineSeparator() + "\t" + tuple.right + ";"));
			}
		}

		return Optional.empty();
	}

	private static Tuple<ParseState, String> compileDefinition(final ParseState state, final String input) {
		final var strip = input.strip();
		final var i = strip.lastIndexOf(' ');
		if (0 <= i) {
			final var beforeName = strip.substring(0, i);
			final var name = strip.substring(i + 1);
			final var tuple = Main.compileDefinitionBeforeName(state, beforeName);
			return new Tuple<>(tuple.left, tuple.right + " " + name);
		}

		return new Tuple<>(state, Main.generatePlaceholder(strip));
	}

	private static Tuple<ParseState, String> compileDefinitionBeforeName(final ParseState state,
																																			 final String beforeName) {
		final var typeSeparator = beforeName.lastIndexOf(' ');
		if (0 > typeSeparator) return Main.compileType(state, beforeName);

		final var beforeType = beforeName.substring(0, typeSeparator).strip();
		final var type = beforeName.substring(typeSeparator + 1);

		if (!beforeType.isEmpty() && '>' == beforeType.charAt(beforeType.length() - 1)) {
			final var substring = beforeType.substring(0, beforeType.length() - ">".length());
			final var i = substring.indexOf('<');
			if (0 <= i) return new Tuple<>(state, "");
		}

		return Main.assembleDefinition(state, type, beforeType);
	}

	private static Tuple<ParseState, String> assembleDefinition(final ParseState state,
																															final String type,
																															final String beforeType) {
		final var tuple = Main.compileType(state, type);
		final var generated = Main.generatePlaceholder(beforeType) + " " + tuple.right;
		return new Tuple<>(tuple.left, generated);
	}

	private static Tuple<ParseState, String> compileType(final ParseState state, final String input) {
		final var strip = input.strip();
		if ("int".contentEquals(strip)) return new Tuple<>(state, "int");
		return Main.compileGenericType(state, strip)
							 .or(() -> Main.compileTypeParam(state, strip))
							 .orElseGet(() -> new Tuple<>(state, Main.generatePlaceholder(strip)));
	}

	private static Optional<Tuple<ParseState, String>> compileTypeParam(final ParseState state, final String input) {
		return state.typeArguments.stream().findFirst().map(first -> {
			return new Tuple<>(state, first);
		});
	}

	private static Optional<Tuple<ParseState, String>> compileGenericType(final ParseState state, final String strip) {
		if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1)) return Optional.empty();
		final var withoutEnd = strip.substring(0, strip.length() - ">".length());

		final var argumentStart = withoutEnd.indexOf('<');
		if (0 > argumentStart) return Optional.empty();
		final var name = withoutEnd.substring(0, argumentStart);
		final var argument = withoutEnd.substring(argumentStart + 1);

		final var tuple = Main.compileType(state, argument);
		final var left = tuple.left;
		final var right = tuple.right;

		final var maybeStructure =
				left.javaStructures.stream().filter(structure -> structure.name.contentEquals(name)).findFirst();

		if (maybeStructure.isEmpty()) return Optional.empty();
		final var javaStructure = maybeStructure.get();

		final var monomorphizedName = javaStructure.name + "_" + right;
		final var parseState = Main.attachStructure(left.withArgument(right), javaStructure.modifiers, monomorphizedName,
																								javaStructure.content, javaStructure.type);

		return Optional.of(new Tuple<>(parseState, "struct " + monomorphizedName));
	}

	private static List<String> divide(final CharSequence input) {
		final var length = input.length();
		var current = new DivideState();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return new JavaList<>(current.advance().stream().toList());
	}

	private static DivideState fold(final DivideState state, final char c) {
		final var current = state.append(c);
		if (';' == c && current.isLevel()) return current.advance();
		if ('}' == c && current.isShallow()) return current.advance().exit();
		if ('{' == c) return current.enter();
		if ('}' == c) return current.exit();
		return current;
	}

	private static String generatePlaceholder(final String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
