/*package magma;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.Optional;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Collectors;*/
/*public */class Main {
	/*private static final*/ LINE_SEPARATOR : string = /*System.lineSeparator()*/;
	/*private Main*/(/**/) {/*
    */}
	/*public static*/ main : /*void*/(/*final String[] args*/) {/*
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
	/*private static*/ compileRoot : string(/*final CharSequence input*/) {/*
        return Main.compileStatements(input, Main::compileRootSegment);
    */}
	/*private static*/ compileStatements : string(/*final CharSequence input, final Function<String, String> mapper*/) {/*
        return Main.divide(input).stream().map(mapper).collect(Collectors.joining());
    */}
	/*private static*/ compileRootSegment : string(/*final String input*/) {/*
        return Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR;
    */}
	/*private static*/ compileRootSegmentValue : string(/*final String input*/) {/*
        if (!input.isEmpty() && '}' == input.charAt(input.length() - 1)) {
            final var withoutEnd = input.substring(0, input.length() - "}".length());
            final var contentStart = withoutEnd.indexOf('{');
            if (0 <= contentStart) {
                final var beforeContent = withoutEnd.substring(0, contentStart);
                final var content = withoutEnd.substring(contentStart + "{".length());
                return Main.compileStructureHeader(beforeContent) + " {" +
                       Main.compileStatements(content, Main::compileStructureSegment) + "}";
            }
        }

        return Main.generatePlaceholder(input);
    */}
	/*private static*/ compileStructureSegment : string(/*final String input*/) {/*
        final var strip = input.strip();
        return Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip);
    */}
	/*private static*/ compileStructureSegmentValue : string(/*final String input*/) {/*
        if (!input.isEmpty() && ';' == input.charAt(input.length() - 1)) {
            final var withoutEnd = input.substring(0, input.length() - ";".length());
            final var before = Main.compileStructureStatementValue(withoutEnd).map(result -> result + ";");
            if (before.isPresent())
                return before.get();
        }

        if (!input.isEmpty() && '}' == input.charAt(input.length() - 1)) {
            final var withoutEnd = input.substring(0, input.length() - "}".length());
            final var contentStart = withoutEnd.indexOf('{');
            if (0 <= contentStart) {
                final var before = withoutEnd.substring(0, contentStart).strip();
                final var after = withoutEnd.substring(contentStart + "{".length());
                if (before.endsWith(")")) {
                    final var withoutParamEnd = before.substring(0, before.length() - ")".length());
                    final var paramStart = withoutParamEnd.indexOf('(');
                    if (0 <= paramStart) {
                        final var definition = withoutParamEnd.substring(0, paramStart);
                        final var params = withoutParamEnd.substring(paramStart + "(".length());
                        return Main.compileDefinition(definition) + "(" + Main.generatePlaceholder(params) + ") {" +
                               Main.generatePlaceholder(after) + "}";
                    }
                }
            }
        }

        return Main.generatePlaceholder(input);
    */}
	/*private static*/ compileStructureStatementValue : /*Optional<String>*/(/*final String input*/) {/*
        final var separator = input.indexOf('=');
        if (0 <= separator) {
            final var before = input.substring(0, separator);
            final var after = input.substring(separator + "=".length());
            return Optional.of(Main.compileDefinition(before) + " = " + Main.compileValue(after));
        }
        return Optional.empty();
    */}
	/*private static*/ compileValue : string(/*final String input*/) {/*
        final var strip = input.strip();
        if (Main.isNumber(strip))
            return strip;

        if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1))
            return strip;

        return Main.generatePlaceholder(strip);
    */}
	/*private static*/ isNumber : /*boolean*/(/*final CharSequence input*/) {/*
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
            continue;
        }
        return true;
    */}
	/*private static*/ compileDefinition : string(/*final String input*/) {/*
        final var strip = input.strip();
        final var separator = strip.lastIndexOf(' ');
        if (0 <= separator) {
            final var beforeName = strip.substring(0, separator);
            final var name = strip.substring(separator + " ".length());
            final var typeSeparator = beforeName.lastIndexOf(' ');
            if (0 <= typeSeparator) {
                final var beforeType = beforeName.substring(0, typeSeparator);
                final var type = beforeName.substring(typeSeparator + " ".length());
                return Main.generatePlaceholder(beforeType) + " " + name + " : " + Main.compileType(type);
            }
        }

        return Main.generatePlaceholder(strip);
    */}
	/*private static*/ compileType : string(/*final String input*/) {/*
        final var strip = input.strip();
        if ("String".contentEquals(strip))
            return "string";
        if ("int".contentEquals(strip))
            return "number";
        return Main.generatePlaceholder(strip);
    */}
	/*private static*/ compileStructureHeader : string(/*final String input*/) {/*
        final var classIndex = input.indexOf("class ");
        if (0 <= classIndex) {
            final var beforeKeyword = input.substring(0, classIndex);
            final var afterKeyword = input.substring(classIndex + "class ".length()).strip();
            final var implementsIndex = afterKeyword.indexOf("implements ");
            if (0 <= implementsIndex) {
                final var beforeImplements = afterKeyword.substring(0, implementsIndex);
                final var afterImplements = afterKeyword.substring(implementsIndex + "implements ".length());
                return Main.generatePlaceholder(beforeKeyword) + "class " + beforeImplements +
                       Main.generatePlaceholder("implements " + afterImplements);
            } else
                return Main.generatePlaceholder(beforeKeyword) + "class " + afterKeyword;
        }

        return Main.generatePlaceholder(input);
    */}
	/*private static*/ divide : /*ListLike<String>*/(/*final CharSequence input*/) {/*
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
	/*private static*/ fold : /*State*/(/*final State state, final char c*/) {/*
        return Main.foldSingleQuotes(state, c).orElseGet(() -> Main.foldStatements(state, c));
    */}
	/*private static*/ foldSingleQuotes : /*Optional<State>*/(/*final State state, final char c*/) {/*
        if ('\'' != c)
            return Optional.empty();
        return state.append(c).popAndAppendToTuple().flatMap(
                            tuple -> '\\' == tuple.right() ? tuple.left().popAndAppendToOption() : Optional.of(tuple.left()))
                    .flatMap(State::popAndAppendToOption);
    */}
	/*private static*/ foldStatements : /*State*/(/*final State state, final char c*/) {/*
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
	/*private static*/ generatePlaceholder : string(/*final String input*/) {/*
        return "stat" + input.replace("stat", "stat").replace("end", "end") + "end";
    */}
	/**/}
/**/
