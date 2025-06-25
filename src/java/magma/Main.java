package magma;

import magma.divide.MutableState;
import magma.divide.State;
import magma.divide.fold.Folder;
import magma.divide.fold.StatementFolder;
import magma.divide.fold.ValuesFolder;
import magma.list.ListLike;
import magma.node.CHeader;
import magma.node.Caller;
import magma.node.ConstructionHeader;
import magma.node.Constructor;
import magma.node.DataAccess;
import magma.node.GenericType;
import magma.node.Invokable;
import magma.node.JavaClassSegment;
import magma.node.JavaDefinition;
import magma.node.JavaHeader;
import magma.node.JavaMethod;
import magma.node.JavaPrimitive;
import magma.node.JavaStringType;
import magma.node.JavaType;
import magma.node.NumberNode;
import magma.node.Placeholder;
import magma.node.StringNode;
import magma.node.Symbol;
import magma.node.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final var rootDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(rootDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(file -> file.toString()
                            .endsWith(".java"))
                    .toList();

            Main.runWithSources(rootDirectory, sources);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(final Path rootDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources)
            Main.runWithSource(rootDirectory, source);
    }

    private static void runWithSource(final Path rootDirectory, final Path source) throws IOException {
        final var fileName = source.getFileName()
                .toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var relativeParent = rootDirectory.relativize(source.getParent());
        final var namespace = Main.computeNamespace(relativeParent);

        final var targetParent = Paths.get(".", "src", "windows")
                .resolve(relativeParent);

        if (!Files.exists(targetParent))
            Files.createDirectories(targetParent);

        final var input = Files.readString(source);
        final var output = Main.compileRoot(input, namespace);

        final var targetContent = "#include \"" + name + ".h\"" + Strings.LINE_SEPARATOR + output;
        Files.writeString(targetParent.resolve(name + ".c"), targetContent);

        final var joined = String.join("_", namespace);
        final var withName = joined + "_" + name;
        final var headerContent = String.join(Strings.LINE_SEPARATOR,
                "#ifndef " + withName,
                "#define " + withName,
                "#endif");

        Files.writeString(targetParent.resolve(name + ".h"), headerContent);
    }

    private static String compileRoot(final CharSequence input, final List<String> namespace) {
        final var segments = Main.divideStatements(input);
        final var buffer = new StringBuilder();
        final var imports = new ArrayList<String>();
        for (final var segment : segments) {
            final var compiled = Main.compileRootSegment(segment, namespace, imports);
            buffer.append(compiled.left());
            compiled.right()
                    .ifPresent(imports::add);
        }

        return buffer.toString();
    }

    private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        return Main.compileAll(input, new StatementFolder(), mapper, "");
    }

    private static String compileAll(final CharSequence input, final Folder folder, final Function<String, String> mapper, final CharSequence delimiter) {
        return Main.divide(input, folder)
                .stream()
                .map(mapper)
                .collect(Collectors.joining(delimiter));
    }

    private static Tuple<String, Optional<String>> compileRootSegment(final String input, final List<String> namespace, final List<String> imports) {
        final var stripped = input.strip();
        if (stripped.startsWith("package "))
            return new Tuple<>("", Optional.empty());

        return Main.compileImport(namespace, input)
                .or(() -> Main.compileStructure(stripped, imports))
                .orElseGet(() -> new Tuple<>(Placeholder.generate(input), Optional.empty()));

    }

    private static Optional<Tuple<String, Optional<String>>> compileImport(final List<String> moduleNamespace, final String input) {
        final var stripped = input.strip();
        if (!stripped.startsWith("import "))
            return Optional.empty();

        final var withoutPrefix = stripped.substring("import ".length());
        if (withoutPrefix.isEmpty() || ';' != withoutPrefix.charAt(withoutPrefix.length() - 1))
            return Optional.empty();

        final var withoutSuffix = withoutPrefix.substring(0, withoutPrefix.length() - ";".length());
        final List<String> importNamespace = new ArrayList<>(Arrays.asList(withoutSuffix.split("\\.")));
        final var actualNamespace = Main.computeActualNamespace(moduleNamespace, importNamespace);

        final var generated = "#include \"" + String.join("/", actualNamespace) + ".h\"" + Strings.LINE_SEPARATOR;
        return Optional.of(new Tuple<>(generated, Optional.of(actualNamespace.getLast())));
    }

    private static List<String> computeActualNamespace(final List<String> moduleNamespace, final List<String> importNamespace) {
        Tuple<List<String>, Integer> current = new Tuple<>(new ArrayList<>(), 0);
        final var moduleNamespaceSize = moduleNamespace.size();
        final var importNamespaceSize = importNamespace.size();

        for (var index = 0; index < moduleNamespaceSize; index++)
            current = Main.foldNamespace(current, index, moduleNamespace, importNamespace);

        current.left()
                .addAll(importNamespace.subList(current.right(), importNamespaceSize));
        return current.left();
    }

    private static Tuple<List<String>, Integer> foldNamespace(final Tuple<List<String>, Integer> current, final int index, final List<String> moduleNamespace, final List<String> importNamespace) {
        final var moduleNamespaceSize = moduleNamespace.size();
        final var importNamespaceSize = importNamespace.size();
        if (importNamespaceSize >= moduleNamespaceSize) {
            final var namespaceSegment = moduleNamespace.get(index);
            final var divisionSegment = importNamespace.get(index);
            if (namespaceSegment.contentEquals(divisionSegment))
                return new Tuple<>(current.left(), current.right() + 1);
        }

        current.left()
                .add("..");
        return current;
    }

    private static Optional<Tuple<String, Optional<String>>> compileStructure(final String stripped, final List<String> imports) {
        if (!(!stripped.isEmpty() && '}' == stripped.charAt(stripped.length() - 1)))
            return Optional.empty();

        final var withoutEnd = stripped.substring(0, stripped.length() - "}".length());
        final var contentStart = withoutEnd.indexOf('{');
        if (0 > contentStart)
            return Optional.empty();

        final var beforeContent = withoutEnd.substring(0, contentStart);
        final var content = withoutEnd.substring(contentStart + "{".length());
        final var classIndex = beforeContent.indexOf("class ");
        if (0 > classIndex)
            return Optional.empty();

        final var beforeKeyword = beforeContent.substring(0, classIndex);
        final var afterKeyword = beforeContent.substring(classIndex + "class ".length())
                .strip();

        final var implementsIndex = afterKeyword.indexOf("implements ");
        final var name = 0 <= implementsIndex ? afterKeyword.substring(0, implementsIndex)
                .strip() : afterKeyword;

        final var segments = Main.divideStatements(content);

        final var output = new StringBuilder();
        final List<JavaMethod> methods = new ArrayList<>();

        for (final var segment : segments) {
            final var compiled = Main.compileClassSegment(segment, imports, methods);
            output.append(compiled.left());
            compiled.right()
                    .ifPresent(methods::add);
        }

        final var joined = methods.stream()
                .map(method -> method.toCFunction(name))
                .map(CFunction::generate)
                .collect(Collectors.joining());

        final var generated = Placeholder.generate(beforeKeyword) + "struct " + name + " {" + output + "};" + Strings.LINE_SEPARATOR + joined;
        return Optional.of(new Tuple<>(generated, Optional.empty()));
    }

    private static Tuple<String, Optional<JavaMethod>> compileClassSegment(final String input, final List<String> imports, final List<JavaMethod> methods) {
        final var node = Main.parseField(input)
                .<JavaClassSegment>map(field -> field)
                .or(() -> Main.parseMethod(input, imports, methods))
                .orElseGet(() -> new Placeholder(input));

        return switch (node) {
            case final JavaDefinition javaDefinition -> {
                yield new Tuple<>(Strings.LINE_SEPARATOR + "\t" + javaDefinition.toCDefinition()
                        .generate() + ";", Optional.empty());
            }
            case final JavaMethod method -> {
                yield new Tuple<>("", Optional.of(method));
            }
            case final Placeholder placeholder -> new Tuple<>(placeholder.generate(), Optional.empty());
        };
    }

    private static Optional<JavaMethod> parseMethod(final String input, final List<String> imports, final List<JavaMethod> methods) {
        final var strip = input.strip();
        if (strip.isEmpty() || '}' != strip.charAt(strip.length() - 1))
            return Optional.empty();

        final var withoutEnd = strip.substring(0, strip.length() - "}".length());
        final var contentStart = withoutEnd.indexOf('{');
        if (0 > contentStart)
            return Optional.empty();

        final var beforeContent = withoutEnd.substring(0, contentStart)
                .strip();
        final var content = withoutEnd.substring(contentStart + "{".length());
        if (beforeContent.isEmpty() || ')' != beforeContent.charAt(beforeContent.length() - 1))
            return Optional.empty();

        final var withoutParamEnd = beforeContent.substring(0, beforeContent.length() - ")".length());
        final var paramStart = withoutParamEnd.indexOf('(');
        if (0 > paramStart)
            return Optional.empty();

        final var beforeParams = withoutParamEnd.substring(0, paramStart);
        final var inputParams = withoutParamEnd.substring(paramStart + "(".length());
        final var compiledParams = Main.compileAll(inputParams, new ValuesFolder(), Main::compileParameter, ", ");

        final var oldHeader = Main.compileHeader(beforeParams);
        final var compiledOutput = Main.compileStatements(content,
                segment -> Main.compileFunctionSegment(segment, imports, methods));

        final var method = new JavaMethod(oldHeader, compiledParams, compiledOutput);
        return Optional.of(method);
    }

    private static JavaHeader compileHeader(final String beforeParams) {
        return Main.parseDefinition(beforeParams)
                .<JavaHeader>map(value -> value)
                .or(() -> Main.compileConstructor(beforeParams))
                .orElseGet((() -> new Placeholder(beforeParams)));
    }

    private static Optional<Constructor> compileConstructor(final String input) {
        final var nameSeparator = input.lastIndexOf(' ');
        if (0 > nameSeparator)
            return Optional.empty();

        final var beforeName = input.substring(0, nameSeparator)
                .strip();

        final var name = input.substring(nameSeparator + " ".length())
                .strip();

        return Optional.of(new Constructor(beforeName, name));
    }

    private static String compileFunctionSegment(final String input, final List<String> imports, final List<JavaMethod> methods) {
        final var strip = input.strip();
        if ("".contentEquals(strip))
            return "";

        if (!strip.isEmpty() && ';' == strip.charAt(strip.length() - 1)) {
            final var withoutEnd = strip.substring(0, strip.length() - ";".length());
            return Strings.LINE_SEPARATOR + "\t" + Main.compileFunctionSegmentValue(withoutEnd, imports, methods) + ";";
        }

        return Placeholder.generate(input);
    }

    private static String compileFunctionSegmentValue(final String input, final List<String> imports, final List<JavaMethod> methods) {
        final var strip = input.strip();
        if (strip.startsWith("return ")) {
            final var value = strip.substring("return ".length());
            return "return " + Main.parseValue(value, imports, methods)
                    .generate();
        }

        final var valueSeparator = input.indexOf('=');
        if (0 <= valueSeparator) {
            final var destination = input.substring(0, valueSeparator);
            final var source = input.substring(valueSeparator + "=".length());
            return Main.parseValue(destination, imports, methods)
                    .generate() + " = " + Main.parseValue(source, imports, methods)
                    .generate();
        }

        return Placeholder.generate(input);
    }

    private static Value parseValue(final String input, final List<String> imports, final List<JavaMethod> methods) {
        final var compiledInvokable = Main.compileInvokable(input, imports, methods);
        if (compiledInvokable.isPresent())
            return compiledInvokable.get();

        final var strip = input.strip();
        final var separator = input.lastIndexOf('.');
        if (0 <= separator) {
            final var childString = input.substring(0, separator);
            final var property = input.substring(separator + ".".length())
                    .strip();
            if (Main.isSymbol(property)) {
                final var child = Main.parseValue(childString, imports, methods);
                if (child instanceof Symbol(final var callerValue))
                    if (imports.contains(callerValue))
                        return new Symbol(property + "_" + callerValue);

                return new DataAccess(child, property);
            }
        }

        if (Main.isSymbol(strip))
            return new Symbol(strip);

        if (Main.isString(strip))
            return new StringNode(strip);

        if (Main.isNumber(strip))
            return new NumberNode(strip);

        return new Placeholder(input);
    }

    private static Optional<Value> compileInvokable(final String input, final List<String> imports, final List<JavaMethod> methods) {
        final var strip = input.strip();
        if (strip.isEmpty() || ')' != strip.charAt(strip.length() - 1))
            return Optional.empty();

        final var withoutEnd = strip.substring(0, strip.length() - ")".length());
        final var argumentsStart = withoutEnd.indexOf('(');
        if (0 > argumentsStart)
            return Optional.empty();

        final var callerString = withoutEnd.substring(0, argumentsStart);
        final var argumentsString = withoutEnd.substring(argumentsStart + "(".length());
        return Main.parseCaller(callerString, imports, methods)
                .map(caller -> {
                    final var argument = Main.parseValue(argumentsString, imports, methods);
                    return new Invokable(caller, argument);
                });
    }

    private static Optional<Caller> parseCaller(final String input, final List<String> imports, final List<JavaMethod> methods) {
        final var strip = input.strip();
        if (strip.startsWith("new ")) {
            final var type = strip.substring("new ".length());
            final var generatedType = Main.parseType(type)
                    .toCType();

            return Optional.of(new ConstructionHeader(generatedType));
        }

        return Optional.of(Main.parseValue(input, imports, methods));
    }

    private static boolean isNumber(final CharSequence input) {
        return Main.stream(input)
                .allMatch(Character::isDigit);
    }

    private static boolean isString(final CharSequence input) {
        return !input.isEmpty() && '\"' == input.charAt(0) && '\"' == input.charAt(input.length() - 1);
    }

    private static boolean isSymbol(final CharSequence input) {
        return Main.stream(input)
                .allMatch(Character::isLetter);
    }

    private static IntStream stream(final CharSequence input) {
        return IntStream.range(0, input.length())
                .map(input::charAt);
    }

    private static String compileParameter(final String input) {
        if (input.isEmpty())
            return "";

        return Main.parseDefinition(input)
                .map(JavaDefinition::toCDefinition)
                .map(CHeader::generate)
                .orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<JavaDefinition> parseField(final String input) {
        final var strip = input.strip();
        if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1))
            return Optional.empty();

        final var substring = strip.substring(0, strip.length() - ";".length());
        return Main.parseDefinition(substring);
    }

    private static Optional<JavaDefinition> parseDefinition(final String input) {
        final var withoutEnd = input.strip();
        final var nameSeparator = withoutEnd.lastIndexOf(' ');
        if (0 > nameSeparator)
            return Optional.empty();

        final var beforeName = withoutEnd.substring(0, nameSeparator);
        final var name = withoutEnd.substring(nameSeparator + " ".length());

        final var typeSeparator = beforeName.lastIndexOf(' ');
        if (0 > typeSeparator)
            return Optional.empty();

        final var beforeType = beforeName.substring(0, typeSeparator);
        final var inputType = beforeName.substring(typeSeparator + " ".length());

        return Optional.of(new JavaDefinition(beforeType, Main.parseType(inputType), name));
    }

    private static JavaType parseType(final String input) {
        final var strip = input.strip();

        if ("int".contentEquals(strip))
            return JavaPrimitive.Int;

        if ("String".contentEquals(strip))
            return new JavaStringType();

        return Main.parseGenericType(strip)
                .or(() -> Main.parseSymbolType(strip))
                .orElseGet(() -> new Placeholder(input));
    }

    private static Optional<JavaType> parseSymbolType(final String input) {
        if (Main.isSymbol(input))
            return Optional.of(new Symbol(input));
        else
            return Optional.empty();
    }

    private static Optional<JavaType> parseGenericType(final String strip) {
        if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1))
            return Optional.empty();

        final var withoutEnd = strip.substring(0, strip.length() - ">".length());
        final var argumentsStart = withoutEnd.indexOf('<');
        if (0 > argumentsStart)
            return Optional.empty();

        final var base = withoutEnd.substring(0, argumentsStart);
        final var arguments = withoutEnd.substring(argumentsStart + "<".length());
        return Optional.of(new GenericType(base, Main.parseType(arguments)));
    }

    private static ListLike<String> divideStatements(final CharSequence input) {
        return Main.divide(input, new StatementFolder());
    }

    private static ListLike<String> divide(final CharSequence input, final Folder folder) {
        final State mutableState = MutableState.empty();
        final var length = input.length();
        var current = mutableState;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = folder.fold(current, c);
        }

        return current.advance()
                .unwrap();
    }

    private static List<String> computeNamespace(final Path relativeParent) {
        final List<String> namespace = new ArrayList<>();
        final var nameCount = relativeParent.getNameCount();
        for (var i = 0; i < nameCount; i++)
            namespace.add(relativeParent.getName(i)
                    .toString());
        return namespace;
    }
}
