/*package magma;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.Arrays;*/
/*import java.util.Collections;*/
/*import java.util.Optional;*/
/*import java.util.function.Function;*/
/*import java.util.regex.Pattern;*/
/*import java.util.stream.Collectors;*/
/*public*/class Main {
	/*private static final*/ LINE_SEPARATOR : string = System.lineSeparator(/**/);
	/*private static final*/ PATTERN : /*Pattern*/ = Pattern.compile(/*"\\n"*/);
	constructor (/**/) {/*
    */}
	/*public static*/ main(/*final String[] args*/) : /*void*/ {/*
        final var root = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(root)) {
            final var sources = stream.filter(path -> path.toString().endsWith(".java")).toList();

            Main.runWithSources(sources, root);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    */}
	/*private static void runWithSources(final Iterable<Path> sources, final Path root) throws IOException {
        for (final var source : sources) {
            final var relative = root.relativize(source.getParent());
            final var input = Files.readString(source);
            final var output = Main.compileRoot(input);
            final var targetParent = Paths.get(".", "src", "node").resolve(relative);
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);

            final var fileName = source.getFileName().toString();
            final var separator = fileName.lastIndexOf('.');
            final var name = fileName.substring(0, separator);
            final var target = targetParent.resolve(name + ".ts");
            Files.writeString(target, output);
        }
    }*/
	/*private static*/ compileRoot(/*final CharSequence input*/) : string {/*
        return Main.compileStatements(input, Main::compileRootSegment);
    */}
	/*private static*/ compileStatements(/*final CharSequence input, final Function<String, String> mapper*/) : string {/*
        return Main.divide(input).stream().map(mapper).collect(Collectors.joining());
    */}
	/*private static*/ compileRootSegment(/*final String input*/) : string {/*
        return Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR;
    */}
	/*private static*/ compileRootSegmentValue(/*final String input*/) : string {/*
        return Main.compileStructure(input).orElseGet(() -> Placeholder.generate(input));
    */}
	/*private static*/ compileStructure(/*final String input*/) : Optional<string> {/*
        if (input.isEmpty() || '}' != input.charAt(input.length() - 1))
            return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - "*/}
	/*".length());*/
	/*final*/ contentStart : /*var*/ = withoutEnd.indexOf(/*'{'*/);
	/*if (0 > contentStart)
            return Optional.empty();*/
	/*final*/ beforeContent : /*var*/ = withoutEnd.substring(/*0, contentStart*/);
	/*final var content = withoutEnd.substring(contentStart + "{".length());
        final var definition = Main.parseStructureHeader(beforeContent);
        final String structName;
        if (definition instanceof final StructureHeader header) {
            if (header.annotations().contains("Actual"))
                return Optional.of("");

            structName = header.name();
        } else
            structName = "?";

        return Optional.of(definition.generate() + " {" +
                           Main.compileStatements(content, input1 -> Main.compileStructureSegment(input1, structName)) +
                           "}");
    }*/
	/*private static*/ compileStructureSegment(/*final String input, final String structName*/) : string {/*
        final var strip = input.strip();
        return Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip, structName);
    */}
	/*private static*/ compileStructureSegmentValue(/*final String input, final String structName*/) : string {/*
        return Main.compileField(input).or(() -> Main.compileMethod(input, structName))
                   .orElseGet(() -> Placeholder.generate(input));
    */}
	/*private static*/ compileField(/*final String input*/) : Optional<string> {/*
        if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
            return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - ";".length());
        return Main.compileStructureStatementValue(withoutEnd).map(result -> result + ";");
    */}
	/*private static*/ compileMethod(/*final String input, final CharSequence structName*/) : Optional<string> {/*
        if (input.isEmpty() || '}' != input.charAt(input.length() - 1))
            return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - "*/}
	/*".length());*/
	/*final*/ contentStart : /*var*/ = withoutEnd.indexOf(/*'{'*/);
	/*if (0 > contentStart)
            return Optional.empty();*/
	/*final*/ before : /*var*/ = withoutEnd.substring(/*0, contentStart).strip(*/);
	/*final var after = withoutEnd.substring(contentStart + "{".length());
        if (before.isEmpty() || ')' != before.charAt(before.length() - 1))
            return Optional.empty();

        final var withoutParamEnd = before.substring(0, before.length() - ")".length());
        final var paramStart = withoutParamEnd.indexOf('(');
        if (0 > paramStart)
            return Optional.empty();

        final var definition = withoutParamEnd.substring(0, paramStart);
        final var params = withoutParamEnd.substring(paramStart + "(".length());
        final var joinedParams = "(" + Placeholder.generate(params) + ")";
        return Optional.of(Main.parseMethodHeader(definition, structName).generateWithAfterName(joinedParams) + " {" +
                           Placeholder.generate(after) + "}");
    }*/
	/*private static*/ parseMethodHeader(/*final String input, final CharSequence structName*/) : /*MethodHeader*/ {/*
        return Main.parseConstructor(input, structName).orElseGet(() -> Main.parseDefinitionOrPlaceholder(input));
    */}
	/*private static*/ parseConstructor(/*final String input, final CharSequence structName*/) : Optional</*MethodHeader*/> {/*
        final var strip = input.strip();
        final var index = strip.lastIndexOf(' ');
        if (0 <= index) {
            final var name = strip.substring(index + " ".length()).strip();
            if (name.contentEquals(structName))
                return Optional.of(new Constructor());
        }

        return Optional.empty();
    */}
	/*private static*/ compileStructureStatementValue(/*final String input*/) : Optional<string> {/*
        final var separator = input.indexOf('=');
        if (0 <= separator) {
            final var before = input.substring(0, separator);
            final var after = input.substring(separator + "=".length());
            return Optional.of(Main.parseDefinitionOrPlaceholder(before).generate() + " = " + Main.compileValue(after));
        }
        return Optional.empty();
    */}
	/*private static*/ compileValue(/*final String input*/) : string {/*
        final var strip = input.strip();
        if (!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)) {
            final var substring = strip.substring(0, strip.length() - ")".length());
            final var i = substring.indexOf('(');
            if (0 <= i) {
                final var caller = substring.substring(0, i);
                final var argument = substring.substring(i + "(".length());
                return Main.compileValue(caller) + "(" + Placeholder.generate(argument) + ")";
            }
        }

        final var separator = input.lastIndexOf('.');
        if (0 <= separator) {
            final var substring = input.substring(0, separator);
            final var substring1 = input.substring(separator + ".".length()).strip();
            return Main.compileValue(substring) + "." + substring1;
        }

        if (Main.isNumber(strip))
            return strip;

        if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1))
            return strip;

        if (Main.isSymbol(strip))
            return strip;

        return Placeholder.generate(strip);
    */}
	/*private static*/ isSymbol(/*final CharSequence input*/) : /*boolean*/ {/*
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c))
                continue;
            return false;
        }
        return true;
    */}
	/*private static*/ isNumber(/*final CharSequence input*/) : /*boolean*/ {/*
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    */}
	/*private static*/ parseDefinitionOrPlaceholder(/*final String input*/) : /*Assignable*/ {/*
        final var strip = input.strip();
        return Main.parseDefinition(strip).<Assignable>map(value -> value).orElseGet(() -> new Placeholder(strip));
    */}
	/*private static*/ parseDefinition(/*final String strip*/) : Optional</*Definition*/> {/*
        final var separator = strip.lastIndexOf(' ');
        if (0 > separator)
            return Optional.empty();

        final var beforeName = strip.substring(0, separator);
        final var name = strip.substring(separator + " ".length());
        final var typeSeparator = beforeName.lastIndexOf(' ');
        if (0 > typeSeparator)
            return Optional.empty();

        final var beforeType = beforeName.substring(0, typeSeparator);
        final var type = beforeName.substring(typeSeparator + " ".length());
        return Optional.of(new Definition(beforeType, name, Main.compileType(type)));
    */}
	/*private static*/ compileType(/*final String input*/) : string {/*
        final var strip = input.strip();
        if ("String".contentEquals(strip))
            return "string";
        if ("int".contentEquals(strip))
            return "number";
        if (!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)) {
            final var withoutEnd = strip.substring(0, strip.length() - ">".length());
            final var start = withoutEnd.indexOf('<');
            if (0 <= start) {
                final var base = withoutEnd.substring(0, start);
                final var argument = withoutEnd.substring(start + "<".length());
                return base + "<" + Main.compileType(argument) + ">";
            }
        }
        return Placeholder.generate(strip);
    */}
	/*private static*/ parseStructureHeader(/*final String input*/) : /*StructureDefinition*/ {/*
        return Main.parseClassHeader(input, "class", "class").or(() -> Main.parseClassHeader(input, "record", "class"))
                   .or(() -> Main.parseClassHeader(input, "interface", "interface"))
                   .orElseGet(() -> new Placeholder(input));
    */}
	/*private static*/ parseClassHeader(/*final String input, final String keyword,
                                                                  final String type*/) : Optional</*StructureDefinition*/> {/*
        final var classIndex = input.indexOf(keyword + " ");
        if (0 > classIndex)
            return Optional.empty();

        final var beforeKeyword = input.substring(0, classIndex).strip();
        final var afterKeyword = input.substring(classIndex + (keyword + " ").length()).strip();
        final var implementsIndex = afterKeyword.indexOf("implements ");
        if (0 <= implementsIndex) {
            final var beforeImplements = afterKeyword.substring(0, implementsIndex).strip();
            final var afterImplements = afterKeyword.substring(implementsIndex + "implements ".length()).strip();
            return Optional.of(Main.complete(type, beforeKeyword, beforeImplements, Optional.of(afterImplements)));
        } else
            return Optional.of(Main.complete(type, beforeKeyword, afterKeyword, Optional.empty()));
    */}
	/*private static*/ complete(/*final String type, final String beforeKeyword,
                                            final String beforeImplements, final Optional<String> maybeImplements*/) : /*StructureHeader*/ {/*
        final var strip = beforeImplements.strip();
        if (!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)) {
            final var withoutEnd = strip.substring(0, strip.length() - ")".length());
            final var contentStart = withoutEnd.indexOf('(');
            if (0 <= contentStart) {
                final var strip1 = withoutEnd.substring(0, contentStart).strip();
                return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, strip1);
            }
        }

        return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, beforeImplements);
    */}
	/*private static*/ parseStructureHeaderByAnnotations(/*final String type, final String beforeKeyword,
                                                                     final Optional<String> maybeImplements,
                                                                     final String strip1*/) : /*StructureHeader*/ {/*
        final var index = beforeKeyword.lastIndexOf(System.lineSeparator());
        if (0 <= index) {
            final var annotations =
                    Arrays.stream(Main.PATTERN.split(beforeKeyword.substring(0, index).strip())).map(String::strip)
                          .filter(value -> !value.isEmpty()).map(value -> value.substring(1)).toList();

            final var substring1 = beforeKeyword.substring(index + System.lineSeparator().length());
            return new StructureHeader(type, annotations, substring1, strip1, maybeImplements);
        }

        return new StructureHeader(type, Collections.emptyList(), beforeKeyword, strip1, maybeImplements);
    */}
	/*private static*/ divide(/*final CharSequence input*/) : ListLike<string> {/*
        State current = new MutableState(input);
        while (true) {
            final var maybe = current.pop();
            if (maybe.isEmpty())
                break;

            final var tuple = maybe.get();
            current = tuple.left();
            current = Main.fold(current, tuple.right());
        }

        return current.advance().unwrap();
    */}
	/*private static*/ fold(/*final State state, final char c*/) : /*State*/ {/*
        return Main.foldSingleQuotes(state, c).orElseGet(() -> Main.foldStatements(state, c));
    */}
	/*private static*/ foldSingleQuotes(/*final State state, final char c*/) : Optional</*State*/> {/*
        if ('\'' != c)
            return Optional.empty();
        return state.append(c).popAndAppendToTuple().flatMap(
                            tuple -> '\\' == tuple.right() ? tuple.left().popAndAppendToOption() : Optional.of(tuple.left()))
                    .flatMap(State::popAndAppendToOption);
    */}
	/*private static*/ foldStatements(/*final State state, final char c*/) : /*State*/ {/*
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('}' == c && appended.isShallow())
            return appended.exit().advance();
        if ('{' == c)
            return appended.enter();
        if ('}' == c)
            return appended.exit();
        return appended;
    */}
	/**/}
/**/
