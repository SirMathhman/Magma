/*import magma.divide.DivideState;*/
/*import magma.divide.MutableDivideState;*/
/*import magma.node.Assignment;*/
/*import magma.node.Constructor;*/
/*import magma.node.Definable;*/
/*import magma.node.Definition;*/
/*import magma.node.Header;*/
/*import magma.node.Placeholder;*/
/*import magma.result.Err;*/
/*import magma.result.Ok;*/
/*import magma.result.Result;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.Set;*/
/*import java.util.function.BiFunction;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Collectors;*/
/*import java.util.stream.IntStream;*/
/*public */class Main {
	private static readonly LINE_SEPARATOR : string = System.lineSeparator();
	private constructor() {
	}
	public static main(args : string[]) : void {
		const sourceDirectory = Paths.get(".", "src", "java");
		Main.walk(sourceDirectory).match(files => {
			const sources = files.stream().filter(/*Files::isRegularFile*/).filter(/*path -> path*/.toString().endsWith(".java")).collect(Collectors.toSet());
			/*return Main*/.runWithSources(sourceDirectory, sources);
		}, /* Optional::of*/).ifPresent(/*Throwable::printStackTrace*/);
	}
	/*private static Result<Set<Path>, IOException> walk(final Path sourceDirectory) {
        try (final var stream = Files.walk(sourceDirectory)) {
            return new Ok<>(stream.collect(Collectors.toSet()));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }*/
	private static runWithSources(sourceDirectory : Pathsources : /*Collection<Path>*/) : /*Optional<IOException>*/ {
		/*return sources*/.stream().map(/*source -> Main*/.runWithSource(sourceDirectory, source)).flatMap(/*Optional::stream*/).findFirst();
	}
	private static runWithSource(sourceDirectory : Pathsource : Path) : /*Optional<IOException>*/ {
		const relativeParent = sourceDirectory.relativize(source.getParent());
		const targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
		/*if (!Files.exists(targetParent)) return Main*/.createDirectories(targetParent);
		const fileName = source.getFileName().toString();
		const separator = fileName.lastIndexOf(/*'.'*/);
		const name = fileName.substring(/*0*/, separator);
		const target = targetParent.resolve(/*name + ".ts"*/);
		/*return Main*/.readString(source).match(input => {
			const output = Main.compileStatements(input, /* Main::compileRootSegment*/);
			/*return Main*/.writeString(target, output);
		}, /* Optional::of*/);
	}
	/*private static Result<String, IOException> readString(final Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }*/
	private static writeString(path : Pathcontent : CharSequence) : /*Optional<IOException>*/ {
		/*try {
            Files.writeString(path, content);
            return Optional.empty();
        }*/
		/*catch (final IOException e) {
            return Optional.of(e);
        }*/
	}
	private static createDirectories(directory : Path) : /*Optional<IOException>*/ {
		/*try {
            Files.createDirectories(directory);
            return Optional.empty();
        }*/
		/*catch (final IOException e) {
            return Optional.of(e);
        }*/
	}
	private static compileStatements(input : CharSequence/* final Function<String*//* String> mapper*/) : string {
		/*return Main*/.compileAll(input, mapper, /* Main::foldStatement*/, "");
	}
	private static compileAll(input : CharSequence/*
                                     final Function<String*//* String> mapper*//*
                                     final BiFunction<DivideState*//* Character*//* DivideState> folder*/delimiter : CharSequence) : string {
		/*return Main*/.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}
	private static compileRootSegment(input : string) : string {
		const stripped = input.strip();
		/*if (stripped.startsWith("package ")) return ""*/;
		/*return Main*/.compileClass(stripped).orElseGet(/*() -> Placeholder.wrap(stripped) + Main.LINE_SEPARATOR*/);
	}
	private static compileClass(stripped : string) : /*Optional<String>*/ {
		/*if (stripped.isEmpty() || '}' != stripped.charAt(stripped.length() - 1)) return Optional*/.empty();
		const withoutEnd = stripped.substring(/*0*/, /*stripped.length() - "}"*/.length());
		const index = withoutEnd.indexOf(/*'{'*/);
		/*if (0 > index) return Optional*/.empty();
		const beforeContent = withoutEnd.substring(/*0*/, index);
		const content = withoutEnd.substring(/*index + "{"*/.length());
		/*return Optional*/.of(/*Main.compileClassHeader(beforeContent) + " {" +
                           Main.compileStatements(content, Main::compileClassSegment) + "}"*/);
	}
	private static compileClassSegment(input : string) : string {
		/*return Main.LINE_SEPARATOR + "\t" + Main*/.compileClassSegmentValue(input.strip());
	}
	private static compileClassSegmentValue(input : string) : string {
		/*return Main*/.compileStatement(input, /* Main::compileFieldValue*/).or(/*() -> Main*/.compileMethod(input)).orElseGet(/*() -> Placeholder*/.wrap(input));
	}
	private static compileStatement(input : string/* final Function<String*//* String> mapper*/) : /*Optional<String>*/ {
		/*if (input.isEmpty() || ';' != input.charAt(input.length() - 1)) return Optional*/.empty();
		const slice = input.substring(/*0*/, /*input.length() - ";"*/.length());
		/*return Optional*/.of(/*mapper.apply(slice) + ";"*/);
	}
	private static compileMethod(input : string) : /*Optional<String>*/ {
		const i = input.indexOf(/*'('*/);
		/*if (0 > i) return Optional*/.empty();
		const headerString = input.substring(/*0*/, i).strip();
		const substring1 = input.substring(/*i + "("*/.length());
		const i1 = /*substring1*/.indexOf(/*')'*/);
		/*if (0 > i1) return Optional*/.empty();
		const params = /*substring1*/.substring(/*0*/, /* i1*/);
		const withBraces = /*substring1*/.substring(/*i1 + ")"*/.length()).strip();
		const maybeHeader = /*Main.parseDefinition(headerString)
                                    .map(definition -> Main.modifyDefinition(definition, Main::transformFieldModifier))
                                    .<Header>map*/(/*value -> value*/).or(/*() -> Main*/.compileConstructor(headerString));
		/*if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}' != withBraces.charAt(withBraces.length() - 1))
            return Optional*/.empty();
		const content = withBraces.substring(/*1*/, /* withBraces.length() - 1*/).strip();
		/*return maybeHeader*/.flatMap(header => {
			const outputParams = "(" + Main.compileParameters(params) + ")";
			/*return Optional*/.of(/*
                    header.generateWithAfterName(outputParams) + " {" + Main.compileFunctionSegments(content, 2) +
                    Main.createIndent(1) + "}"*/);
		});
	}
	private static compileFunctionSegments(content : stringdepth : int) : string {
		/*return Main*/.compileStatements(content, /*input -> Main*/.compileFunctionSegment(input, depth));
	}
	private static compileFunctionSegment(input : stringdepth : int) : string {
		const stripped = input.strip();
		/*if (stripped.isEmpty()) return ""*/;
		/*return Main.createIndent(depth) + Main*/.compileFunctionSegmentValue(stripped, depth);
	}
	private static compileFunctionSegmentValue(input : stringdepth : int) : string {
		/*return Main*/.compileStatement(input, /*input1 -> Main*/.compileFunctionStatementValue(/*input1*/, depth)).orElseGet(/*() -> Placeholder*/.wrap(input));
	}
	private static compileFunctionStatementValue(input : stringdepth : int) : string {
		/*return Main*/.parseAssignment(input, depth).map(/*Main::transformStatementAssignment*/).map(/*Assignment::generate*/).or(/*() -> Main*/.compileInvokable(input, depth)).orElseGet(/*() -> Placeholder*/.wrap(input));
	}
	private static transformStatementAssignment(assignment : Assignment) : Assignment {
		/*return assignment*/.mapDefinition(/*Main::transformStatementDefinable*/);
	}
	private static transformStatementDefinable(definable : Definable) : Definable {
		/*if (!(definable instanceof final Definition definition)) return definable*/;
		/*return definition*/.mapModifiers(/*Main::transformStatementModifiers*/).mapType(/*Main::removeVar*/);
	}
	private static removeVar(type : string) : /*Optional<String>*/ {
		/*if ("var".contentEquals(type)) return Optional*/.empty();
		/*else return Optional*/.of(type);
	}
	private static transformStatementModifiers(modifiers : /*Collection<String>*/) : /*List<String>*/ {
		const newModifiers = Main.transformModifiers(modifiers, /* Main::transformFunctionModifier*/);
		/*if (newModifiers.contains("const")) return newModifiers*/;
		newModifiers.add("let");
		/*return newModifiers*/;
	}
	private static transformFunctionModifier(modifier : CharSequence) : /*Optional<String>*/ {
		/*if ("final".contentEquals(modifier)) return Optional*/.of("const");
		/*return Optional*/.empty();
	}
	private static createIndent(depth : int) : string {
		/*return Main.LINE_SEPARATOR + "\t"*/.repeat(depth);
	}
	private static compileParameters(input : string) : string {
		const stripped = input.strip();
		/*if (stripped.isEmpty()) return ""*/;
		/*return Main*/.compileAll(input, /*input1 -> Main*/.transformDefinable(Main.parseDefinable(/*input1*/), /*
                                                                        Main::transformParameterModifier*/).generate(), /*
                               Main::foldValue*/, "");
	}
	private static transformParameterModifier(s : string) : /*Optional<String>*/ {
		/*return Optional*/.empty();
	}
	private static foldValue(state : DivideStatec : char) : DivideState {
		/*if (',' == c && state.isLevel()) return state*/.advance();
		const appended = state.append(c);
		/*if ('{' == c || '(' == c) return appended*/.enter();
		/*if ('}' == c || ')' == c) return appended*/.exit();
		/*return appended*/;
	}
	private static compileConstructor(header : string) : /*Optional<Constructor>*/ {
		const i2 = header.lastIndexOf(/*' '*/);
		/*if (0 > i2) return Optional*/.empty();
		const substring = header.substring(/*0*/, /* i2*/);
		/*return Main*/.lexModifiers(substring).map(/*Constructor::new*/);
	}
	private static compileFieldValue(input : string) : string {
		/*return Main*/.parseAssignment(input, /* 1*/).map(/*assignment -> Main*/.transformAssignment(assignment, /* Main::transformFieldModifier*/)).map(/*Assignment::generate*/).orElseGet(/*() -> Placeholder*/.wrap(input));
	}
	private static transformAssignment(assignment : Assignment/*
                                                  final Function<String*//* Optional<String>> mapper*/) : Assignment {
		/*return assignment*/.mapDefinition(/*definition -> Main*/.transformDefinable(definition, mapper));
	}
	private static parseAssignment(input : stringdepth : int) : /*Optional<Assignment>*/ {
		const index = input.indexOf(/*'='*/);
		/*if (0 > index) return Optional*/.empty();
		const definition = input.substring(/*0*/, index);
		const valueString = input.substring(/*index + "="*/.length());
		/*return Main*/.parseDefinition(definition).flatMap(/*definable -> Main*/.compileValue(valueString, depth).map(/*value -> new Assignment*/(definable, value)));
	}
	private static transformDefinable(definable : Definable/*
                                                final Function<String*//* Optional<String>> transformer*/) : Definable {
		/*if (definable instanceof final Definition definition) return Main*/.modifyDefinition(definition, transformer);
		/*return definable*/;
	}
	private static modifyDefinition(definition : Definition/*
                                               final Function<String*//* Optional<String>> transformer*/) : Definition {
		/*return definition*/.mapModifiers(/*modifiers -> Main*/.transformModifiers(modifiers, transformer));
	}
	private static transformModifiers(modifiers : /*Collection<String>*//*
                                                   final Function<String*//* Optional<String>> transformer*/) : /*List<String>*/ {
		/*return modifiers*/.stream().map(transformer).flatMap(/*Optional::stream*/).collect(Collectors.toList());
	}
	private static compileValueOrPlaceholder(input : stringdepth : int) : string {
		/*return Main*/.compileValue(input, depth).orElseGet(/*() -> Placeholder*/.wrap(input));
	}
	private static compileValue(input : stringdepth : int) : /*Optional<String>*/ {
		/*return Main*/.compileInvokable(input, depth).or(/*() -> Main*/.compileDataAccess(input, depth)).or(/*() -> Main*/.compileIdentifier(input)).or(/*() -> Main*/.compileString(input)).or(/*() -> Main*/.compileLambda(input, depth));
	}
	private static compileLambda(input : stringdepth : int) : /*Optional<String>*/ {
		const i = input.indexOf("->");
		/*if (0 <= i) {
            final var parameters = input.substring(0, i).strip();
            final var withBraces = input.substring(i + "->".length()).strip();
            if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
                final var substring = withBraces.substring(1, withBraces.length() - 1);
                return Optional.of(
                        parameters + " => {" + Main.compileFunctionSegments(substring, depth + 1) + Main.createIndent(depth) +
                        "}");
            }
        }*/
		/*return Optional*/.empty();
	}
	private static compileString(input : string) : /*Optional<String>*/ {
		const strip = input.strip();
		/*if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == input.charAt(input.length() - 1))
            return Optional*/.of(strip);
		/*else return Optional*/.empty();
	}
	private static compileIdentifier(input : string) : /*Optional<String>*/ {
		const strip = input.strip();
		/*if (Main.isSymbol(strip)) return Optional*/.of(strip);
		/*else return Optional*/.empty();
	}
	private static isSymbol(input : CharSequence) : boolean {
		/*return IntStream*/.range(/*0*/, input.length()).mapToObj(/*input::charAt*/).allMatch(/*Character::isLetter*/);
	}
	private static compileDataAccess(input : stringdepth : int) : /*Optional<String>*/ {
		const index = input.lastIndexOf(/*'.'*/);
		/*if (0 > index) return Optional*/.empty();
		const parent = input.substring(/*0*/, index);
		const property = input.substring(/*index + "."*/.length()).strip();
		/*if (!Main.isSymbol(property)) return Optional*/.empty();
		/*return Optional*/.of(/*Main.compileValueOrPlaceholder(parent, depth) + "." + property*/);
	}
	private static compileInvokable(input : stringdepth : int) : /*Optional<String>*/ {
		const strip = input.strip();
		/*if (!(!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1))) return Optional*/.empty();
		const slice = strip.substring(/*0*/, /*strip.length() - ")"*/.length());
		const divisions = Main.divide(slice, /* Main::foldInvocationStart*/);
		const argumentsString = divisions.removeLast();
		const withEnd = String.join("", divisions);
		/*if (withEnd.isEmpty() || '(' != withEnd.charAt(withEnd.length() - 1)) return Optional*/.empty();
		const withoutEnd = withEnd.substring(/*0*/, /*withEnd.length() - "("*/.length());
		/*return Optional*/.of(/*Main.compileValueOrPlaceholder(withoutEnd, depth) + "(" +
                           Main.compileValues(argumentsString, input1 -> Main.compileValueOrPlaceholder(input1, depth)) +
                           ")"*/);
	}
	private static foldInvocationStart(state : DivideStatec : char) : DivideState {
		const appended = state.append(c);
		/*if ('(' == c) return Main*/.foldInvocationParamStart(appended);
		/*if (')' == c) return appended*/.exit();
		/*return appended*/;
	}
	private static foldInvocationParamStart(appended : DivideState) : DivideState {
		const enter = appended.enter();
		/*if (enter.isShallow()) return enter*/.advance();
		/*return enter*/;
	}
	private static compileValues(input : CharSequence/* final Function<String*//* String> mapper*/) : string {
		/*return Main*/.compileAll(input, mapper, /* Main::foldValue*/, ", ");
	}
	private static parseDefinable(input : string) : Definable {
		const beforeType = Main.parseDefinition(input);
		/*return beforeType.<Definable>map*/(/*value -> value*/).orElseGet(/*() -> new Placeholder*/(input));
	}
	private static parseDefinition(input : string) : /*Optional<Definition>*/ {
		const strip = input.strip();
		const nameSeparator = strip.lastIndexOf(/*' '*/);
		/*if (0 > nameSeparator) return Optional*/.empty();
		const beforeName = strip.substring(/*0*/, nameSeparator).strip();
		const name = strip.substring(/*nameSeparator + " "*/.length());
		const typeSeparator = beforeName.lastIndexOf(/*' '*/);
		/*if (0 > typeSeparator) return Optional*/.empty();
		const beforeType = beforeName.substring(/*0*/, typeSeparator);
		const typeString = beforeName.substring(/*typeSeparator + " "*/.length());
		/*return Main*/.lexModifiers(beforeType).flatMap(modifiers => {
			const type = Main.compileType(typeString);
			/*return Optional*/.of(/*new Definition*/(modifiers, name, Optional.of(type)));
		});
	}
	private static lexModifiers(modifiers : string) : /*Optional<List<String>>*/ {
		/*Optional<List<String>> maybeList = Optional*/.of(/*new ArrayList<>*/());
		/*for (final String modifier : modifiers.split(" ")) maybeList = Main*/.lexModifier(modifier, maybeList);
		/*return maybeList*/;
	}
	private static lexModifier(modifier : stringmaybeList : /*Optional<List<String>>*/) : /*Optional<List<String>>*/ {
		const stripped : string = modifier.strip();
		/*if (!Main.isSymbol(stripped)) return Optional*/.empty();
		/*if (stripped.isEmpty()) return maybeList*/;
		/*return maybeList*/.map(list => {
			list.add(stripped);
			/*return list*/;
		});
	}
	private static transformFieldModifier(modifier : CharSequence) : /*Optional<String>*/ {
		/*if ("private".contentEquals(modifier)) return Optional*/.of("private");
		/*if ("public".contentEquals(modifier)) return Optional*/.of("public");
		/*if ("static".contentEquals(modifier)) return Optional*/.of("static");
		/*if ("final".contentEquals(modifier)) return Optional*/.of("readonly");
		/*return Optional*/.empty();
	}
	private static compileType(input : string) : string {
		const strip = input.strip();
		/*if ("String".contentEquals(strip)) return "string"*/;
		/*if ("void".contentEquals(strip)) return "void"*/;
		/*if (strip.endsWith("[]")) {
            final var slice = strip.substring(0, strip.length() - "[]".length());
            return Main.compileType(slice) + "[]";
        }*/
		/*return Main*/.compileIdentifier(input).orElseGet(/*() -> Placeholder*/.wrap(input));
	}
	private static compileClassHeader(input : string) : string {
		const index = input.indexOf("class ");
		/*if (0 <= index) {
            final var beforeKeyword = input.substring(0, index);
            final var afterKeyword = input.substring(index + "class ".length()).strip();
            return Placeholder.wrap(beforeKeyword) + "class " + afterKeyword;
        }*/
		/*return Placeholder*/.wrap(input);
	}
	private static divide(input : CharSequence/*
                                       final BiFunction<DivideState*//* Character*//* DivideState> folder*/) : /*List<String>*/ {
		const state = Main.foldEarly(/*new MutableDivideState*/(input), /* DivideState::pop*/, /*popped -> new Tuple<>*/(true, Main.foldDecorated(popped, folder)));
		/*return state*/.right().advance().stream().collect(Collectors.toCollection(/*ArrayList::new*/));
	}
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
	private static foldDecorated(/*final Tuple<DivideState*//* Character> popped*//*
                                             final BiFunction<DivideState*//* Character*//* DivideState> folder*/) : DivideState {
		const state = popped.left();
		const c = popped.right();
		/*return Main*/.foldSingleQuotes(state, c).or(/*() -> Main*/.foldDoubleQuotes(state, c)).orElseGet(/*() -> folder*/.apply(state, c));
	}
	private static foldDoubleQuotes(state : DivideStatec : char) : /*Optional<DivideState>*/ {
		/*if ('\"' != c) return Optional*/.empty();
		/*return Optional*/.of(Main.foldEarly(state.append(/*'\"'*/), /* DivideState::popAndAppendToTuple*/, /* Main::foldInDoubleQuotes*/).right());
	}
	/*private static Tuple<Boolean, DivideState> foldInDoubleQuotes(final Tuple<DivideState, Character> popped) {
        final var nextAppended = popped.left();
        final var next = popped.right();

        if ('\\' == next) return new Tuple<>(true, nextAppended.popAndAppendToOption().orElse(nextAppended));
        if ('\"' == next) return new Tuple<>(false, nextAppended);
        return new Tuple<>(true, nextAppended);
    }*/
	private static foldSingleQuotes(state : DivideStatec : char) : /*Optional<DivideState>*/ {
		/*if ('\'' != c) return Optional*/.empty();
		/*return state*/.append(c).popAndAppendToTuple().flatMap(/*Main::foldEscape*/).flatMap(/*DivideState::popAndAppendToOption*/);
	}
	private static foldEscape(/*final Tuple<DivideState*//* Character> tuple*/) : /*Optional<DivideState>*/ {
		/*if ('\\' == tuple.right()) return tuple*/.left().popAndAppendToOption();
		/*return Optional*/.of(tuple.left());
	}
	private static foldStatement(state : DivideStatec : char) : DivideState {
		const appended = state.append(c);
		/*if (';' == c && appended.isLevel()) return appended*/.advance();
		/*if ('}' == c && appended.isShallow()) return appended*/.advance().exit();
		/*if ('{' == c || '(' == c) return appended*/.enter();
		/*if ('}' == c || ')' == c) return appended*/.exit();
		/*return appended*/;
	}
	/**/}/**/
