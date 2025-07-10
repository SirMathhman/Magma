/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
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
	public static main(args : string[]) : void {
		const sourceDirectory = Paths.get(/*".", "src", "java"*/);
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
	private static compileStatements(input : CharSequence/* final Function<String*//* String> mapper*/) : string {
		/*return Main.compileAll(input, mapper, Main::foldStatement)*/;
	}
	private static compileAll(input : CharSequence/*
                                     final Function<String*//* String> mapper*//*
                                     final BiFunction<DivideState*//* Character*//* DivideState> folder*/) : string {
		const segments = Main.divide(/*input, folder*/);
		const output = /*new StringBuilder*/();
		/*for (final var segment : segments) output.append(mapper.apply(segment))*/;
		/*return output.toString()*/;
	}
	private static compileRootSegment(input : string) : string {
		const stripped = input.strip();
		/*if (stripped.startsWith("package ")) return ""*/;
		/*return Main.compileClass(stripped).orElseGet(() -> Placeholder.wrap(stripped) + Main.LINE_SEPARATOR)*/;
	}
	private static compileClass(stripped : string) : /*Optional<String>*/ {
		let ! : /*'}'*/ = stripped.charAt(/*stripped.length() - 1)) return Optional.empty(*/);
		const withoutEnd = stripped.substring(/*0, stripped.length() - "}".length()*/);
		const index = withoutEnd.indexOf(/*'{'*/);
		/*if (0 > index) return Optional.empty()*/;
		const beforeContent = withoutEnd.substring(/*0, index*/);
		const content = withoutEnd.substring(/*index + "{".length()*/);
		/*return Optional.of(Main.compileClassHeader(beforeContent) + " {" +
                           Main.compileStatements(content, Main::compileClassSegment) + "}")*/;
	}
	private static compileClassSegment(input : string) : string {
		/*return Main.LINE_SEPARATOR + "\t" + Main.compileClassSegmentValue(input.strip())*/;
	}
	private static compileClassSegmentValue(input : string) : string {
		/*return Main.compileStatement(input, Main::compileFieldValue)
                   .or(() -> Main.compileMethod(input))
                   .orElseGet(() -> Placeholder.wrap(input))*/;
	}
	private static compileStatement(input : string/* final Function<String*//* String> mapper*/) : /*Optional<String>*/ {
		let ! : /*';'*/ = input.charAt(/*input.length() - 1)) return Optional.empty(*/);
		const slice = input.substring(/*0, input.length() - ";".length()*/);
		/*return Optional.of(mapper.apply(slice) + ";")*/;
	}
	private static compileMethod(input : string) : /*Optional<String>*/ {
		const i = input.indexOf(/*'('*/);
		/*if (0 > i) return Optional.empty()*/;
		const headerString = input.substring(/*0, i).strip(*/);
		const substring1 = input.substring(/*i + "(".length()*/);
		const i1 = /*substring1*/.indexOf(/*')'*/);
		/*if (0 > i1) return Optional.empty()*/;
		const params = /*substring1*/.substring(/*0, i1*/);
		const withBraces = /*substring1*/.substring(/*i1 + ")".length()).strip(*/);
		const maybeHeader = Main.compileDefinition(/*headerString)
                                    .map(definition -> Main.modifyDefinition(definition, Main::transformFieldModifier))
                                    .<Header>map(value -> value)
                                    .or(() -> Main.compileConstructor(headerString)*/);
		let ! : /*'{'*/ = withBraces.charAt(/*0) || '}' != withBraces.charAt(withBraces.length() - 1))
            return Optional.empty(*/);
		const content = withBraces.substring(/*1, withBraces.length() - 1).strip(*/);
		/*return maybeHeader.flatMap(header -> {
            final var outputParams = "(" + Main.compileParameters(params) + ")";
            return Optional.of(header.generateWithAfterName(outputParams) + " {" +
                               Main.compileStatements(content, Main::compileFunctionSegment) + Main.createIndent(1) +
                               "}");
        }*/
		/*)*/;
	}
	private static compileFunctionSegment(input : string) : string {
		const stripped = input.strip();
		/*if (stripped.isEmpty()) return ""*/;
		/*return Main.createIndent(2) + Main.compileFunctionSegmentValue(stripped)*/;
	}
	private static compileFunctionSegmentValue(input : string) : string {
		/*return Main.compileStatement(input, Main::compileFunctionStatementValue)
                   .orElseGet(() -> Placeholder.wrap(input))*/;
	}
	private static compileFunctionStatementValue(input : string) : string {
		/*return Main.parseAssignment(input)
                   .map(Main::transformStatementAssignment)
                   .map(Assignment::generate)
                   .orElseGet(() -> Placeholder.wrap(input))*/;
	}
	private static transformStatementAssignment(assignment : Assignment) : Assignment {
		/*return assignment.mapDefinition(Main::transformStatementDefinable)*/;
	}
	private static transformStatementDefinable(definable : Definable) : Definable {
		/*if (!(definable instanceof final Definition definition)) return definable*/;
		/*return definition.mapModifiers(Main::transformStatementModifiers).mapType(type -> {
            if ("var".contentEquals(type)) return Optional.empty();
            else return Optional.of(type);
        }*/
		/*)*/;
	}
	private static transformStatementModifiers(modifiers : /*Collection<String>*/) : /*List<String>*/ {
		const newModifiers = Main.transformModifiers(/*modifiers, Main::transformFunctionModifier*/);
		/*if (newModifiers.contains("const")) return newModifiers*/;
		/*newModifiers.add("let")*/;
		/*return newModifiers*/;
	}
	private static transformFunctionModifier(modifier : string) : /*Optional<String>*/ {
		/*if ("final".contentEquals(modifier)) return Optional.of("const")*/;
		/*return Optional.empty()*/;
	}
	private static createIndent(depth : int) : string {
		/*return Main.LINE_SEPARATOR + "\t".repeat(depth)*/;
	}
	private static compileParameters(input : string) : string {
		const stripped = input.strip();
		/*if (stripped.isEmpty()) return ""*/;
		/*return Main.compileAll(input, input1 -> Main.transformDefinable(Main.parseDefinable(input1),
                                                                        Main::transformParameterModifier).generate(),
                               Main::foldValue)*/;
	}
	private static transformParameterModifier(s : string) : /*Optional<String>*/ {
		/*return Optional.empty()*/;
	}
	private static foldValue(state : DivideStatec : char) : DivideState {
		/*if (',' */ = /*= c) return state*/.advance();
		/*return state.append(c)*/;
	}
	private static compileConstructor(header : string) : /*Optional<Constructor>*/ {
		const i2 = header.lastIndexOf(/*' '*/);
		/*if (0 <= i2) {
            final var substring = header.substring(0, i2);
            final var newModifiers = Main.lexModifiers(substring);
            return Optional.of(new Constructor(newModifiers));
        }*/
		/*else return Optional.empty()*/;
	}
	private static compileFieldValue(input : string) : string {
		/*return Main.parseAssignment(input)
                   .map(assignment -> Main.transformAssignment(assignment, Main::transformFieldModifier))
                   .map(Assignment::generate)
                   .orElseGet(() -> Placeholder.wrap(input))*/;
	}
	private static transformAssignment(assignment : Assignment/*
                                                  final Function<String*//* Optional<String>> mapper*/) : Assignment {
		/*return assignment.mapDefinition(definition -> Main.transformDefinable(definition, mapper))*/;
	}
	private static parseAssignment(input : string) : /*Optional<Assignment>*/ {
		const index = input.indexOf(/*'='*/);
		/*if (0 > index) return Optional.empty()*/;
		const definition = input.substring(/*0, index*/);
		const value = input.substring(/*index + "=".length()*/);
		const definable = Main.parseDefinable(/*definition*/);
		const s = Main.compileValue(/*value*/);
		/*return Optional.of(new Assignment(definable, s))*/;
	}
	private static transformDefinable(definable : Definable/*
                                                final Function<String*//* Optional<String>> transformer*/) : Definable {
		/*if (definable instanceof final Definition definition) return Main.modifyDefinition(definition, transformer)*/;
		/*return definable*/;
	}
	private static modifyDefinition(definition : Definition/*
                                               final Function<String*//* Optional<String>> transformer*/) : Definition {
		/*return definition.mapModifiers(modifiers -> Main.transformModifiers(modifiers, transformer))*/;
	}
	private static transformModifiers(modifiers : /*Collection<String>*//*
                                                   final Function<String*//* Optional<String>> transformer*/) : /*List<String>*/ {
		/*return modifiers.stream().map(transformer).flatMap(Optional::stream).collect(Collectors.toList())*/;
	}
	private static compileValue(input : string) : string {
		/*return Main.compileInvokable(input)
                   .or(() -> Main.compileDataAccess(input))
                   .or(() -> Main.compileIdentifier(input))
                   .orElseGet(() -> Placeholder.wrap(input))*/;
	}
	private static compileIdentifier(input : string) : /*Optional<String>*/ {
		const strip = input.strip();
		/*if (Main.isSymbol(strip)) return Optional.of(strip)*/;
		/*else return Optional.empty()*/;
	}
	private static isSymbol(input : CharSequence) : boolean {
		/*return IntStream.range(0, input.length()).mapToObj(input::charAt).allMatch(Character::isLetter)*/;
	}
	private static compileDataAccess(input : string) : /*Optional<String>*/ {
		const i = input.lastIndexOf(/*'.'*/);
		/*if (0 > i) return Optional.empty()*/;
		const substring = input.substring(/*0, i*/);
		const substring1 = input.substring(/*i + ".".length()*/);
		/*return Optional.of(Main.compileValue(substring) + "." + substring1)*/;
	}
	private static compileInvokable(input : string) : /*Optional<String>*/ {
		const strip = input.strip();
		let ')' : /*&&*/ = /*= strip*/.charAt(/*strip.length() - 1))) return Optional.empty(*/);
		const slice = strip.substring(/*0, strip.length() - ")".length()*/);
		const i = slice.indexOf(/*'('*/);
		/*if (0 > i) return Optional.empty()*/;
		const substring = slice.substring(/*0, i*/);
		const substring1 = slice.substring(/*i + "(".length()*/);
		/*return Optional.of(Main.compileValue(substring) + "(" + Main.compileArguments(substring1) + ")")*/;
	}
	private static compileArguments(input : string) : string {
		const strip = input.strip();
		/*if (strip.isEmpty()) return ""*/;
		/*return Placeholder.wrap(strip)*/;
	}
	private static parseDefinable(input : string) : Definable {
		const beforeType = Main.compileDefinition(/*input*/);
		/*return beforeType.<Definable>map(value -> value).orElseGet(() -> new Placeholder(input))*/;
	}
	private static compileDefinition(input : string) : /*Optional<Definition>*/ {
		const strip = input.strip();
		const nameSeparator = strip.lastIndexOf(/*' '*/);
		/*if (0 > nameSeparator) return Optional.empty()*/;
		const beforeName = strip.substring(/*0, nameSeparator).strip(*/);
		const name = strip.substring(/*nameSeparator + " ".length()*/);
		const typeSeparator = beforeName.lastIndexOf(/*' '*/);
		/*if (0 > typeSeparator) return Optional.empty()*/;
		const beforeType = beforeName.substring(/*0, typeSeparator*/);
		const typeString = beforeName.substring(/*typeSeparator + " ".length()*/);
		const newModifiers = Main.lexModifiers(/*beforeType*/);
		const type = Main.compileType(/*typeString*/);
		/*return Optional.of(new Definition(newModifiers, name, Optional.of(type)))*/;
	}
	private static lexModifiers(modifiers : string) : /*Collection<String>*/ {
		/*return Arrays.stream(modifiers.split(" ")).map(String::strip).filter(value -> !value.isEmpty()).toList()*/;
	}
	private static transformFieldModifier(modifier : CharSequence) : /*Optional<String>*/ {
		/*if ("private".contentEquals(modifier)) return Optional.of("private")*/;
		/*if ("public".contentEquals(modifier)) return Optional.of("public")*/;
		/*if ("static".contentEquals(modifier)) return Optional.of("static")*/;
		/*if ("final".contentEquals(modifier)) return Optional.of("readonly")*/;
		/*return Optional.empty()*/;
	}
	private static compileType(input : string) : string {
		const strip = input.strip();
		/*if ("String".contentEquals(strip)) return "string"*/;
		/*if ("void".contentEquals(strip)) return "void"*/;
		/*if (strip.endsWith("[]")) {
            final var slice = strip.substring(0, strip.length() - "[]".length());
            return Main.compileType(slice) + "[]";
        }*/
		/*return Main.compileIdentifier(input).orElseGet(() -> Placeholder.wrap(input))*/;
	}
	private static compileClassHeader(input : string) : string {
		const index = input.indexOf(/*"class "*/);
		/*if (0 <= index) {
            final var beforeKeyword = input.substring(0, index);
            final var afterKeyword = input.substring(index + "class ".length()).strip();
            return Placeholder.wrap(beforeKeyword) + "class " + afterKeyword;
        }*/
		/*return Placeholder.wrap(input)*/;
	}
	private static divide(input : CharSequence/*
                                       final BiFunction<DivideState*//* Character*//* DivideState> folder*/) : /*List<String>*/ {
		const state = Main.foldEarly(/*new MutableDivideState(input), DivideState::pop,
                                         popped -> new Tuple<>(true, Main.foldDecorated(popped, folder))*/);
		/*return state.right().advance().stream().toList()*/;
	}
	private static foldEarly(initial : DivideState/*
                                                         final Function<DivideState*//* Optional<Tuple<DivideState*//* Character>>> mapper*//*
                                                         final Function<Tuple<DivideState*//* Character>*//* Tuple<Boolean*//* DivideState>> folder*/) : /*DivideState>*/ {
		let tuple : /*DivideState>*/ = /*new Tuple<>*/(/*true, initial*/);
		/*while (tuple.left()) {
            final var state = tuple.right();
            tuple = Main.foldEarlyElement(state, mapper, folder);
        }*/
		/*return tuple*/;
	}
	private static foldEarlyElement(state : DivideState/*
                                                                final Function<DivideState*//* Optional<Tuple<DivideState*//* Character>>> mapper*//*
                                                                final Function<Tuple<DivideState*//* Character>*//* Tuple<Boolean*//* DivideState>> folder*/) : /*DivideState>*/ {
		const maybePopped = mapper.apply(/*state*/);
		/*if (maybePopped.isEmpty()) return new Tuple<>(false, state)*/;
		const popped = maybePopped.get();
		/*return folder.apply(popped)*/;
	}
	private static foldDecorated(/*final Tuple<DivideState*//* Character> popped*//*
                                             final BiFunction<DivideState*//* Character*//* DivideState> folder*/) : DivideState {
		const state = popped.left();
		const c = popped.right();
		/*return Main.foldSingleQuotes(state, c)
                   .or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> folder.apply(state, c))*/;
	}
	private static foldDoubleQuotes(state : DivideStatec : char) : /*Optional<DivideState>*/ {
		let ! : /*('\"'*/ = /*c) return Optional*/.empty();
		/*return Optional.of(
                Main.foldEarly(state.append('\"'), DivideState::popAndAppendToTuple, Main::foldInDoubleQuotes).right())*/;
	}
	private static foldInDoubleQuotes(/*final Tuple<DivideState*//* Character> popped*/) : /*DivideState>*/ {
		const nextAppended = popped.left();
		const next = popped.right();
		/*if ('\\' */ = /*= next) return new Tuple<>*/(/*true, nextAppended.popAndAppendToOption().orElse(nextAppended)*/);
		/*if ('\"' */ = /*= next) return new Tuple<>*/(/*false, nextAppended*/);
		/*return new Tuple<>(true, nextAppended)*/;
	}
	private static foldSingleQuotes(state : DivideStatec : char) : /*Optional<DivideState>*/ {
		let ! : /*('\''*/ = /*c) return Optional*/.empty();
		/*return state.append(c)
                    .popAndAppendToTuple()
                    .flatMap(Main::foldEscape)
                    .flatMap(DivideState::popAndAppendToOption)*/;
	}
	private static foldEscape(/*final Tuple<DivideState*//* Character> tuple*/) : /*Optional<DivideState>*/ {
		/*if ('\\' */ = /*= tuple*/.right(/*)) return tuple.left().popAndAppendToOption(*/);
		/*return Optional.of(tuple.left())*/;
	}
	private static foldStatement(state : DivideStatec : char) : DivideState {
		const appended = state.append(/*c*/);
		/*if (';' */ = /*= c && appended*/.isLevel(/*)) return appended.advance(*/);
		/*if ('}' */ = /*= c && appended*/.isShallow(/*)) return appended.advance().exit(*/);
		/*if ('{' */ = /*= c) return appended*/.enter();
		/*if ('}' */ = /*= c) return appended*/.exit();
		/*return appended*/;
	}
	/**/}/**/
