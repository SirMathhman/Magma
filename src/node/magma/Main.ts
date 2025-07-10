/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.ArrayList;*/
/*import java.util.Arrays;*/
/*import java.util.Collection;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Collectors;*/
/*public */class Main {
	private static readonly LINE_SEPARATOR : string = /* System.lineSeparator()*/;
	/*private Main() {}*/
	/*public static void main(final String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(sourceDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                                      .filter(path -> path.toString().endsWith(".java"))
                                      .collect(Collectors.toSet());

            Main.runWithSources(sourceDirectory, sources);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }*/
	/*private static void runWithSources(final Path sourceDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources) Main.runWithSource(sourceDirectory, source);
    }*/
	/*private static void runWithSource(final Path sourceDirectory, final Path source) throws IOException {
        final var relativeParent = sourceDirectory.relativize(source.getParent());

        final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);

        final var output = Main.compileStatements(input, Main::compileRootSegment);
        Files.writeString(target, output);
    }*/
	/*private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments) output.append(mapper.apply(segment));
        return output.toString();
    }*/
	/*private static String compileRootSegment(final String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ")) return "";
        return Main.compileClass(stripped).orElseGet(() -> Main.generatePlaceholder(stripped) + Main.LINE_SEPARATOR);
    }*/
	/*private static Optional<String> compileClass(final String stripped) {
        if (stripped.isEmpty() || '}' != stripped.charAt(stripped.length() - 1)) return Optional.empty();
        final var withoutEnd = stripped.substring(0, stripped.length() - "}".length());

        final var index = withoutEnd.indexOf('{');
        if (0 > index) return Optional.empty();
        final var beforeContent = withoutEnd.substring(0, index);
        final var content = withoutEnd.substring(index + "{".length());

        return Optional.of(Main.compileClassHeader(beforeContent) + " {" +
                           Main.compileStatements(content, Main::compileClassSegment) + "}");
    }*/
	/*private static String compileClassSegment(final String input) {
        return Main.LINE_SEPARATOR + "\t" + Main.compileClassSegmentValue(input.strip());
    }*/
	/*private static String compileClassSegmentValue(final String input) {
        if (!input.isEmpty() && ';' == input.charAt(input.length() - 1)) {
            final var slice = input.substring(0, input.length() - ";".length());
            return Main.compileClassStatementValue(slice) + ";";
        }
        return Main.generatePlaceholder(input);
    }*/
	/*private static String compileClassStatementValue(final String input) {
        final var index = input.indexOf('=');
        if (0 <= index) {
            final var definition = input.substring(0, index);
            final var value = input.substring(index + "=".length());
            return Main.compileDefinitionOrPlaceholder(definition) + " = " + Main.generatePlaceholder(value);
        }

        return Main.generatePlaceholder(input);
    }*/
	/*private static String compileDefinitionOrPlaceholder(final String input) {
        final var beforeType = Main.compileDefinition(input);
        return beforeType.orElseGet(() -> Main.generatePlaceholder(input));
    }*/
	/*private static Optional<String> compileDefinition(final String input) {
        final var strip = input.strip();
        final var nameSeparator = strip.lastIndexOf(' ');

        if (0 > nameSeparator) return Optional.empty();
        final var beforeName = strip.substring(0, nameSeparator).strip();
        final var name = strip.substring(nameSeparator + " ".length());

        final var typeSeparator = beforeName.lastIndexOf(' ');
        if (0 > typeSeparator) return Optional.empty();
        final var beforeType = beforeName.substring(0, typeSeparator);
        final var type = beforeName.substring(typeSeparator + " ".length());

        final var oldModifiers = Arrays.stream(beforeType.split(" "))
                                       .map(String::strip)
                                       .filter(value -> !value.isEmpty())
                                       .collect(Collectors.toSet());

        final Collection<String> newModifiers = new ArrayList<>();
        for (final var oldModifier : oldModifiers) {
            if ("private".contentEquals(oldModifier)) newModifiers.add("private");
            if ("static".contentEquals(oldModifier)) newModifiers.add("static");
            if ("final".contentEquals(oldModifier)) newModifiers.add("readonly");
        }

        final var joinedModifiers = newModifiers.stream().map(value -> value + " ").collect(Collectors.joining());
        return Optional.of(joinedModifiers + name + " : " + Main.compileType(type));
    }*/
	/*private static String compileType(final String input) {
        final var strip = input.strip();
        if ("String".contentEquals(strip)) return "string";

        return Main.generatePlaceholder(strip);
    }*/
	/*private static String compileClassHeader(final String input) {
        final var index = input.indexOf("class ");
        if (0 <= index) {
            final var beforeKeyword = input.substring(0, index);
            final var afterKeyword = input.substring(index + "class ".length()).strip();
            return Main.generatePlaceholder(beforeKeyword) + "class " + afterKeyword;
        }

        return Main.generatePlaceholder(input);
    }*/
	/*private static List<String> divide(final CharSequence input) {
        final var state = Main.foldEarly(new MutableDivideState(input), DivideState::pop,
                                         popped -> new Tuple<>(true, Main.foldDecorated(popped)));
        return state.right().advance().stream().toList();
    }*/
	/*private static Tuple<Boolean, DivideState> foldEarly(final DivideState initial,
                                                         final Function<DivideState, Optional<Tuple<DivideState, Character>>> mapper,
                                                         final Function<Tuple<DivideState, Character>, Tuple<Boolean, DivideState>> folder) {
        Tuple<Boolean, DivideState> tuple = new Tuple<>(true, initial);
        while (tuple.left()) {
            final var state = tuple.right();
            tuple = Main.foldEarlyElement(state, mapper, folder);
        }
        return tuple;
    }*/
	/*private static Tuple<Boolean, DivideState> foldEarlyElement(final DivideState state,
                                                                final Function<DivideState, Optional<Tuple<DivideState, Character>>> mapper,
                                                                final Function<Tuple<DivideState, Character>, Tuple<Boolean, DivideState>> folder) {
        final var maybePopped = mapper.apply(state);
        if (maybePopped.isEmpty()) return new Tuple<>(false, state);
        final var popped = maybePopped.get();
        return folder.apply(popped);
    }*/
	/*private static DivideState foldDecorated(final Tuple<DivideState, Character> popped) {
        final var state = popped.left();
        final var c = popped.right();
        return Main.foldSingleQuotes(state, c)
                   .or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> Main.foldStatement(state, c));
    }*/
	/*private static Optional<DivideState> foldDoubleQuotes(final DivideState state, final char c) {
        if ('\"' != c) return Optional.empty();
        return Optional.of(
                Main.foldEarly(state.append('\"'), DivideState::popAndAppendToTuple, Main::foldInDoubleQuotes).right());
    }*/
	/*private static Tuple<Boolean, DivideState> foldInDoubleQuotes(final Tuple<DivideState, Character> popped) {
        final var nextAppended = popped.left();
        final var next = popped.right();

        if ('\\' == next) return new Tuple<>(true, nextAppended.popAndAppendToOption().orElse(nextAppended));
        if ('\"' == next) return new Tuple<>(false, nextAppended);
        return new Tuple<>(true, nextAppended);
    }*/
	/*private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char c) {
        if ('\'' != c) return Optional.empty();
        return state.append(c)
                    .popAndAppendToTuple()
                    .flatMap(Main::foldEscape)
                    .flatMap(DivideState::popAndAppendToOption);
    }*/
	/*private static Optional<DivideState> foldEscape(final Tuple<DivideState, Character> tuple) {
        if ('\\' == tuple.right()) return tuple.left().popAndAppendToOption();
        return Optional.of(tuple.left());
    }*/
	/*private static DivideState foldStatement(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('}' == c && appended.isShallow()) return appended.advance().exit();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }*/
	/*private static String generatePlaceholder(final String input) {
        return "start" + input.replace("start", "start").replace("end", "end") + "end";
    }*/
	/**/}/**/
