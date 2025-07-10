/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.ArrayList;*/
/*import java.util.Arrays;*/
/*import java.util.Collection;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.function.BiFunction;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Collectors;*/
/*import java.util.stream.IntStream;*/
/*public */class Main {
	private static readonly LINE_SEPARATOR : string = System.lineSeparator();
	private constructor() {
	}
	static main(readonly args : /*String[]*/) : void {
		/*final var sourceDirectory = Paths.get(".", "src", "java");*/
		/*try (final var stream = Files.walk(sourceDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                                      .filter(path -> path.toString().endsWith(".java"))
                                      .collect(Collectors.toSet());

            Main.runWithSources(sourceDirectory, sources);
        }*/
		/*catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }*/
	}
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
	private static compileStatements(readonly input : /*CharSequence*//* final Function<String*//* String> mapper*/) : string {
		/*return Main.compileAll(input, mapper, Main::foldStatement);*/
	}
	private static compileAll(readonly input : /*CharSequence*//*
                                     final Function<String*//* String> mapper*//*
                                     final BiFunction<DivideState*//* Character*//* DivideState> folder*/) : string {
		/*final var segments = Main.divide(input, folder);*/
		/*final var output = new StringBuilder();*/
		/*for (final var segment : segments) output.append(mapper.apply(segment));*/
		/*return output.toString();*/
	}
	private static compileRootSegment(readonly input : string) : string {
		/*final var stripped = input.strip();*/
		/*if (stripped.startsWith("package ")) return "";*/
		/*return Main.compileClass(stripped)
                   .orElseGet(() -> Placeholder.generatePlaceholder(stripped) + Main.LINE_SEPARATOR);*/
	}
	private static compileClass(readonly stripped : string) : /*Optional<String>*/ {
		/*if (stripped.isEmpty() || '}' != stripped.charAt(stripped.length() - 1)) return Optional.empty();*/
		/*final var withoutEnd = stripped.substring(0, stripped.length() - "}".length());*/
		/*final var index = withoutEnd.indexOf('{');*/
		/*if (0 > index) return Optional.empty();*/
		/*final var beforeContent = withoutEnd.substring(0, index);*/
		/*final var content = withoutEnd.substring(index + "{".length());*/
		/*return Optional.of(Main.compileClassHeader(beforeContent) + " {" +
                           Main.compileStatements(content, Main::compileClassSegment) + "}");*/
	}
	private static compileClassSegment(readonly input : string) : string {
		/*return Main.LINE_SEPARATOR + "\t" + Main.compileClassSegmentValue(input.strip());*/
	}
	private static compileClassSegmentValue(readonly input : string) : string {
		/*return Main.compileField(input)
                   .or(() -> Main.compileMethod(input))
                   .orElseGet(() -> Placeholder.generatePlaceholder(input));*/
	}
	private static compileField(readonly input : string) : /*Optional<String>*/ {
		/*if (input.isEmpty() || ';' != input.charAt(input.length() - 1)) return Optional.empty();*/
		/*final var slice = input.substring(0, input.length() - ";".length());*/
		/*return Optional.of(Main.compileFieldValue(slice) + ";");*/
	}
	private static compileMethod(readonly input : string) : /*Optional<String>*/ {
		/*final var i = input.indexOf('(');*/
		/*if (0 > i) return Optional.empty();*/
		/*final var headerString = input.substring(0, i).strip();*/
		/*final var substring1 = input.substring(i + "(".length());*/
		/*final var i1 = substring1.indexOf(')');*/
		/*if (0 > i1) return Optional.empty();*/
		/*final var params = substring1.substring(0, i1);*/
		/*final var withBraces = substring1.substring(i1 + ")".length()).strip();*/
		/*final var maybeHeader = Main.compileDefinition(headerString)
                                    .<Header>map(value -> value)
                                    .or(() -> Main.compileConstructor(headerString));*/
		/*if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}' != withBraces.charAt(withBraces.length() - 1))
            return Optional.empty();*/
		/*final var content = withBraces.substring(1, withBraces.length() - 1).strip();*/
		/*return maybeHeader.flatMap(header -> {
            final var outputParams = "(" + Main.compileParameters(params) + ")";
            return Optional.of(header.generateWithAfterName(outputParams) + " {" +
                               Main.compileStatements(content, Main::compileFunctionSegment) + Main.createIndent(1) +
                               "}");
        }*/
		/*);*/
	}
	private static compileFunctionSegment(readonly input : string) : string {
		/*final var stripped = input.strip();*/
		/*if (stripped.isEmpty()) return "";*/
		/*return Main.createIndent(2) + Placeholder.generatePlaceholder(stripped);*/
	}
	private static createIndent(readonly depth : /*int*/) : string {
		/*return Main.LINE_SEPARATOR + "\t".repeat(depth);*/
	}
	private static compileParameters(readonly input : string) : string {
		/*final var stripped = input.strip();*/
		/*if (stripped.isEmpty()) return "";*/
		/*return Main.compileAll(input, input1 -> Main.parseDefinitionOrPlaceholder(input1).generate(), Main::foldValue);*/
	}
	private static foldValue(readonly state : /*DivideState*/readonly c : /*char*/) : /*DivideState*/ {
		/*if (',' == c) return state.advance();*/
		/*return state.append(c);*/
	}
	private static compileConstructor(readonly header : string) : /*Optional<Constructor>*/ {
		/*final var i2 = header.lastIndexOf(' ');*/
		/*if (0 <= i2) {
            final var substring = header.substring(0, i2);
            final var newModifiers = Main.lexModifiers(substring);
            return Optional.of(new Constructor(newModifiers));
        }*/
		/*else return Optional.empty();*/
	}
	private static compileFieldValue(readonly input : string) : string {
		/*final var index = input.indexOf('=');*/
		/*if (0 <= index) {
            final var definition = input.substring(0, index);
            final var value = input.substring(index + "=".length());
            return Main.parseDefinitionOrPlaceholder(definition).generate() + " = " + Main.compileValue(value);
        }*/
		/*return Placeholder.generatePlaceholder(input);*/
	}
	private static compileValue(readonly input : string) : string {
		/*return Main.compileInvokable(input)
                   .or(() -> Main.compileDataAccess(input))
                   .or(() -> Main.compileSymbol(input))
                   .orElseGet(() -> Placeholder.generatePlaceholder(input));*/
	}
	private static compileSymbol(readonly input : string) : /*Optional<String>*/ {
		/*final var strip = input.strip();*/
		/*if (Main.isSymbol(strip)) return Optional.of(strip);*/
		/*else return Optional.empty();*/
	}
	private static isSymbol(readonly input : /*CharSequence*/) : /*boolean*/ {
		/*return IntStream.range(0, input.length()).mapToObj(input::charAt).allMatch(Character::isLetter);*/
	}
	private static compileDataAccess(readonly input : string) : /*Optional<String>*/ {
		/*final var i = input.lastIndexOf('.');*/
		/*if (0 > i) return Optional.empty();*/
		/*final var substring = input.substring(0, i);*/
		/*final var substring1 = input.substring(i + ".".length());*/
		/*return Optional.of(Main.compileValue(substring) + "." + substring1);*/
	}
	private static compileInvokable(readonly input : string) : /*Optional<String>*/ {
		/*final var strip = input.strip();*/
		/*if (!(!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1))) return Optional.empty();*/
		/*final var slice = strip.substring(0, strip.length() - ")".length());*/
		/*final var i = slice.indexOf('(');*/
		/*if (0 > i) return Optional.empty();*/
		/*final var substring = slice.substring(0, i);*/
		/*final var substring1 = slice.substring(i + "(".length());*/
		/*return Optional.of(Main.compileValue(substring) + "(" + Main.compileArguments(substring1) + ")");*/
	}
	private static compileArguments(readonly input : string) : string {
		/*final var strip = input.strip();*/
		/*if (strip.isEmpty()) return "";*/
		/*return Placeholder.generatePlaceholder(strip);*/
	}
	private static parseDefinitionOrPlaceholder(readonly input : string) : /*Definable*/ {
		/*final var beforeType = Main.compileDefinition(input);*/
		/*return beforeType.<Definable>map(value -> value).orElseGet(() -> new Placeholder(input));*/
	}
	private static compileDefinition(readonly input : string) : /*Optional<Definition>*/ {
		/*final var strip = input.strip();*/
		/*final var nameSeparator = strip.lastIndexOf(' ');*/
		/*if (0 > nameSeparator) return Optional.empty();*/
		/*final var beforeName = strip.substring(0, nameSeparator).strip();*/
		/*final var name = strip.substring(nameSeparator + " ".length());*/
		/*final var typeSeparator = beforeName.lastIndexOf(' ');*/
		/*if (0 > typeSeparator) return Optional.empty();*/
		/*final var beforeType = beforeName.substring(0, typeSeparator);*/
		/*final var typeString = beforeName.substring(typeSeparator + " ".length());*/
		/*final var newModifiers = Main.lexModifiers(beforeType);*/
		/*final var type = Main.compileType(typeString);*/
		/*return Optional.of(new Definition(newModifiers, name, type));*/
	}
	private static lexModifiers(readonly modifiers : string) : /*Collection<String>*/ {
		/*final var oldModifiers = Arrays.stream(modifiers.split(" "))
                                       .map(String::strip)
                                       .filter(value -> !value.isEmpty())
                                       .collect(Collectors.toSet());*/
		/*final Collection<String> newModifiers = new ArrayList<>();*/
		/*for (final var oldModifier : oldModifiers) Main.foldModifier(oldModifier).ifPresent(newModifiers::add);*/
		/*return newModifiers;*/
	}
	private static foldModifier(readonly modifier : /*CharSequence*/) : /*Optional<String>*/ {
		/*if ("private".contentEquals(modifier)) return Optional.of("private");*/
		/*if ("static".contentEquals(modifier)) return Optional.of("static");*/
		/*if ("final".contentEquals(modifier)) return Optional.of("readonly");*/
		/*return Optional.empty();*/
	}
	private static compileType(readonly input : string) : string {
		/*final var strip = input.strip();*/
		/*if ("String".contentEquals(strip)) return "string";*/
		/*if ("void".contentEquals(strip)) return "void";*/
		/*return Placeholder.generatePlaceholder(strip);*/
	}
	private static compileClassHeader(readonly input : string) : string {
		/*final var index = input.indexOf("class ");*/
		/*if (0 <= index) {
            final var beforeKeyword = input.substring(0, index);
            final var afterKeyword = input.substring(index + "class ".length()).strip();
            return Placeholder.generatePlaceholder(beforeKeyword) + "class " + afterKeyword;
        }*/
		/*return Placeholder.generatePlaceholder(input);*/
	}
	private static divide(readonly input : /*CharSequence*//*
                                       final BiFunction<DivideState*//* Character*//* DivideState> folder*/) : /*List<String>*/ {
		/*final var state = Main.foldEarly(new MutableDivideState(input), DivideState::pop,
                                         popped -> new Tuple<>(true, Main.foldDecorated(popped, folder)));*/
		/*return state.right().advance().stream().toList();*/
	}
	private static foldEarly(readonly initial : /*DivideState*//*
                                                         final Function<DivideState*//* Optional<Tuple<DivideState*//* Character>>> mapper*//*
                                                         final Function<Tuple<DivideState*//* Character>*//* Tuple<Boolean*//* DivideState>> folder*/) : /*DivideState>*/ {
		/*Tuple<Boolean, DivideState> tuple = new Tuple<>(true, initial);*/
		/*while (tuple.left()) {
            final var state = tuple.right();
            tuple = Main.foldEarlyElement(state, mapper, folder);
        }*/
		/*return tuple;*/
	}
	private static foldEarlyElement(readonly state : /*DivideState*//*
                                                                final Function<DivideState*//* Optional<Tuple<DivideState*//* Character>>> mapper*//*
                                                                final Function<Tuple<DivideState*//* Character>*//* Tuple<Boolean*//* DivideState>> folder*/) : /*DivideState>*/ {
		/*final var maybePopped = mapper.apply(state);*/
		/*if (maybePopped.isEmpty()) return new Tuple<>(false, state);*/
		/*final var popped = maybePopped.get();*/
		/*return folder.apply(popped);*/
	}
	private static foldDecorated(/*final Tuple<DivideState*//* Character> popped*//*
                                             final BiFunction<DivideState*//* Character*//* DivideState> folder*/) : /*DivideState*/ {
		/*final var state = popped.left();*/
		/*final var c = popped.right();*/
		/*return Main.foldSingleQuotes(state, c)
                   .or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> folder.apply(state, c));*/
	}
	private static foldDoubleQuotes(readonly state : /*DivideState*/readonly c : /*char*/) : /*Optional<DivideState>*/ {
		/*if ('\"' != c) return Optional.empty();*/
		/*return Optional.of(
                Main.foldEarly(state.append('\"'), DivideState::popAndAppendToTuple, Main::foldInDoubleQuotes).right());*/
	}
	private static foldInDoubleQuotes(/*final Tuple<DivideState*//* Character> popped*/) : /*DivideState>*/ {
		/*final var nextAppended = popped.left();*/
		/*final var next = popped.right();*/
		/*if ('\\' == next) return new Tuple<>(true, nextAppended.popAndAppendToOption().orElse(nextAppended));*/
		/*if ('\"' == next) return new Tuple<>(false, nextAppended);*/
		/*return new Tuple<>(true, nextAppended);*/
	}
	private static foldSingleQuotes(readonly state : /*DivideState*/readonly c : /*char*/) : /*Optional<DivideState>*/ {
		/*if ('\'' != c) return Optional.empty();*/
		/*return state.append(c)
                    .popAndAppendToTuple()
                    .flatMap(Main::foldEscape)
                    .flatMap(DivideState::popAndAppendToOption);*/
	}
	private static foldEscape(/*final Tuple<DivideState*//* Character> tuple*/) : /*Optional<DivideState>*/ {
		/*if ('\\' == tuple.right()) return tuple.left().popAndAppendToOption();*/
		/*return Optional.of(tuple.left());*/
	}
	private static foldStatement(readonly state : /*DivideState*/readonly c : /*char*/) : /*DivideState*/ {
		/*final var appended = state.append(c);*/
		/*if (';' == c && appended.isLevel()) return appended.advance();*/
		/*if ('}' == c && appended.isShallow()) return appended.advance().exit();*/
		/*if ('{' == c) return appended.enter();*/
		/*if ('}' == c) return appended.exit();*/
		/*return appended;*/
	}
	/**/}/**/
