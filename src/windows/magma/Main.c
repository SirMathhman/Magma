#include "Main.h"
/*import magma.divide.MutableState;*/
/*import magma.divide.State;*/
/*import magma.divide.fold.Folder;*/
/*import magma.divide.fold.StatementFolder;*/
/*import magma.divide.fold.ValuesFolder;*/
/*import magma.list.ListLike;*/
/*import magma.type.CPrimitive;*/
/*import magma.type.CType;*/
/*import magma.type.Placeholder;*/
/*import magma.type.Pointer;*/
/*import magma.type.Struct;*/
/*import java.io.IOException;*/
/*import java.nio.file.Files;*/
/*import java.nio.file.Path;*/
/*import java.nio.file.Paths;*/
/*import java.util.ArrayList;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*import java.util.function.Function;*/
/*import java.util.stream.Collectors;*/
/*import java.util.stream.IntStream;*/
/**/struct Main {
	/*private static final String SEPARATOR *//*=*/ System.lineSeparator();/*

    private static void runWithSources(final Path rootDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources)
            Main.runWithSource(rootDirectory, source);
    }*//*

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
        final var output = Main.compileRoot(input);

        final var targetContent = "#include \"" + name + ".h\"" + Main.SEPARATOR + output;
        Files.writeString(targetParent.resolve(name + ".c"), targetContent);

        final var joined = String.join("_", namespace);
        final var withName = joined + "_" + name;
        final var headerContent = String.join(Main.SEPARATOR, "#ifndef " + withName, "#define " + withName, "#endif");

        Files.writeString(targetParent.resolve(name + ".h"), headerContent);
    }*/
	/*' == stripped.charAt(stripped.length() - 1)))
            *//*return*/ Optional.empty();/*

        final var withoutEnd = stripped.substring(0, stripped.length() - "*/};
/*private */struct Main new_Main() {
	struct Main this;
	return this;
}
/*public static *//*void*/ main(/*final *//*String[]*/ args) {
	/*final var rootDirectory */ = Paths.get(".", "src", "java");/*
        try (final var stream = Files.walk(rootDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(file -> file.toString()
                            .endsWith(".java"))
                    .toList();

            Main.runWithSources(rootDirectory, sources);
        }*//* catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }*/
}
/*private static */char* compileRoot(/*final *//*CharSequence*/ input) {
	/*return Main.compileStatements(input, Main::compileRootSegment)*/;
}
/*private static */char* compileStatements(/*final *//*CharSequence*/ input, /* final Function<String*/, /* String> mapper*/) {
	/*return Main.compileAll(input, new StatementFolder(), mapper, "")*/;
}
/*private static */char* compileAll(/*final *//*CharSequence*/ input, /*final *//*Folder*/ folder, /* final Function<String*/, /* String> mapper*/, /*final *//*CharSequence*/ delimiter) {
	/*return Main.divide(input, folder)
                .stream()
                .map(mapper)
                .collect(Collectors.joining(delimiter))*/;
}
/*private static */char* compileRootSegment(/*final */char* input) {
	/*final var stripped */ = input.strip();
	/*if (stripped.startsWith("package "))
            return ""*/;
	/*if (stripped.startsWith("import "))
            return Placeholder.generate(stripped) + Main.SEPARATOR*/;
	/*return Main.compileStructure(stripped)
                .orElseGet(() -> Placeholder.generate(input))*/;
}
/*private static */struct Optional_char_ptr compileStructure(/*final */char* stripped) {/*
        if (!(!stripped.isEmpty() && '*/
}
/*".length());*//*
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
        final var other = new StringBuilder();

        for (final var segment : segments) {
            final var compiled = Main.compileClassSegment(segment);
            output.append(compiled.left());
            other.append(compiled.right());
        }

        return Optional.of(Placeholder.generate(beforeKeyword) + "struct " + name + " {" + output + "};" + Main.SEPARATOR + other);
    }

    private static Tuple<String, String> compileClassSegment(final String input) {
        return Main.compileField(input)
                .or(() -> Main.compileMethod(input))
                .orElseGet(() -> new Tuple<>(Placeholder.generate(input), ""));
    }

    private static Optional<Tuple<String, String>> compileMethod(final String input) {
        final var strip = input.strip();
        if (strip.isEmpty() || '}' != strip.charAt(strip.length() - 1))
            return Optional.empty();

        final var withoutEnd = strip.substring(0, strip.length() - "}*//*".length());*//*
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
        final var outputParams = Main.compileAll(inputParams, new ValuesFolder(), Main::compileParameter, ", ");

        final var outputDefinition = Main.compileDefinition(beforeParams)
                .<Header>map(value -> value)
                .or(() -> Main.compileConstructor(beforeParams))
                .orElseGet((() -> new Placeholder(beforeContent)));

        final var compiled = Main.compileStatements(content, Main::compileFunctionSegment);
        final String outputContent;
        if (outputDefinition instanceof final Constructor constructor)
            outputContent = Main.SEPARATOR + "\tstruct " + constructor.name() + " this;" + compiled + Main.SEPARATOR + "\treturn this;";
        else
            outputContent = compiled;

        return Optional.of(new Tuple<>("",
                outputDefinition.generate() + "(" + outputParams + ") {" + outputContent + Main.SEPARATOR + "}" + Main.SEPARATOR));
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

    private static String compileFunctionSegment(final String input) {
        final var strip = input.strip();
        if ("".contentEquals(strip))
            return "";

        if (!strip.isEmpty() && ';' == strip.charAt(strip.length() - 1)) {
            final var withoutEnd = strip.substring(0, strip.length() - ";".length());
            return Main.SEPARATOR + "\t" + Main.compileFunctionSegmentValue(withoutEnd) + ";";
        }

        return Placeholder.generate(input);
    }

    private static String compileFunctionSegmentValue(final String input) {
        final var valueSeparator = input.indexOf('=');
        if (0 <= valueSeparator) {
            final var destination = input.substring(0, valueSeparator);
            final var source = input.substring(valueSeparator + "=".length());
            return Main.compileValue(destination) + " = " + Main.compileValue(source);
        }

        return Placeholder.generate(input);
    }

    private static String compileValue(final String input) {
        final var separator = input.lastIndexOf('.');
        if (0 <= separator) {
            final var value = input.substring(0, separator);
            final var property = input.substring(separator + ".".length())
                    .strip();
            return Main.compileValue(value) + "." + property;
        }

        final var strip = input.strip();
        if (Main.isSymbol(strip))
            return strip;

        if (Main.isString(strip))
            return strip;

        if (Main.isNumber(strip))
            return strip;

        return Placeholder.generate(input);
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

        return Main.compileDefinition(input)
                .map(Definition::generate)
                .orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<Tuple<String, String>> compileField(final String input) {
        final var strip = input.strip();
        if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1))
            return Optional.empty();

        final var substring = strip.substring(0, strip.length() - ";".length());
        return Main.compileDefinition(substring)
                .map(Definition::generate)
                .map(content -> new Tuple<>(Main.SEPARATOR + "\t" + content + ";", ""));
    }

    private static Optional<Definition> compileDefinition(final String input) {
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

        return Optional.of(new Definition(beforeType, Main.compileType(inputType), name));
    }

    private static CType compileType(final String input) {
        final var strip = input.strip();

        if ("int".contentEquals(strip))
            return CPrimitive.Int;

        if ("String".contentEquals(strip))
            return new Pointer(CPrimitive.Char);

        return Main.compileGenericType(strip)
                .orElseGet(() -> new Placeholder(input));
    }

    private static Optional<CType> compileGenericType(final String strip) {
        if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1))
            return Optional.empty();

        final var withoutEnd = strip.substring(0, strip.length() - ">".length());
        final var argumentsStart = withoutEnd.indexOf('<');
        if (0 > argumentsStart)
            return Optional.empty();

        final var base = withoutEnd.substring(0, argumentsStart);
        final var arguments = withoutEnd.substring(argumentsStart + "<".length());
        return Optional.of(new Struct(base + "_" + Main.compileType(arguments)
                .generateSymbol()));
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
}*//*
*/