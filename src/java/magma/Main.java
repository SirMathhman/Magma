package magma;

import magma.error.ApplicationError;
import magma.error.CompileError;
import magma.error.ThrowableError;
import magma.java.result.JavaResults;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static final Path SOURCE_DIRECTORY = Paths.get(".", "src", "java");

    public static void main(String[] args) {
        collect()
                .mapErr(ThrowableError::new)
                .mapErr(ApplicationError::new)
                .match(Main::runWithSources, Some::new)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Option<ApplicationError> runWithSources(Set<Path> paths) {
        Set<Path> sources = paths.stream()
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .collect(Collectors.toSet());

        for (Path source : sources) {
            Option<ApplicationError> maybeError = runWithSource(source);
            if (maybeError.isPresent()) return maybeError;
        }

        return new None<>();
    }

    private static Option<ApplicationError> runWithSource(Path source) {
        Path relative = SOURCE_DIRECTORY.relativize(source);
        Path parent = relative.getParent();
        if (parent.startsWith(Paths.get("magma", "java"))) return new None<>();

        return readString(source)
                .mapErr(ThrowableError::new)
                .mapErr(ApplicationError::new)
                .match(input -> runWithSource(input, parent, relative), Some::new);
    }

    private static Option<ApplicationError> runWithSource(String input, Path parent, Path relative) {
        return compile(input)
                .mapErr(ApplicationError::new)
                .match(output -> writeOutputSafe(output, parent, computeName(relative)), Some::new);
    }

    private static Option<ApplicationError> writeOutputSafe(String output, Path parent, String name) {
        return writeOutput(output, parent, name)
                .map(ThrowableError::new)
                .map(ApplicationError::new);
    }

    private static Result<String, IOException> readString(Path source) {
        return JavaResults.wrap(() -> Files.readString(source));
    }

    private static Result<Set<Path>, IOException> collect() {
        try (Stream<Path> stream = Files.walk(SOURCE_DIRECTORY)) {
            return new Ok<>(stream.collect(Collectors.toSet()));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static Option<IOException> writeOutput(String output, Path parent, String name) {
        Path targetParent = Paths.get(".", "src", "windows").resolve(parent);
        if (!Files.exists(targetParent)) {
            try {
                Files.createDirectories(targetParent);
            } catch (IOException e) {
                return new Some<>(e);
            }
        }

        Path target = targetParent.resolve(name + ".c");
        try {
            Files.writeString(target, output);
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e);
        }
    }

    private static String computeName(Path relative) {
        String nameWithExt = relative.getFileName().toString();
        return nameWithExt.substring(0, nameWithExt.lastIndexOf('.'));
    }

    private static Result<String, CompileError> compile(String input) {
        return divideAndCompile(input, Main::compileRootSegment);
    }

    private static Result<String, CompileError> divideAndCompile(String input, Function<String, Result<String, CompileError>> compiler) {
        ArrayList<String> segments = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            buffer.append(c);
            if (c == ';' && depth == 0) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            } else {
                if (c == '{') depth++;
                if (c == '}') depth--;
            }
        }
        segments.add(buffer.toString());

        Result<StringBuilder, CompileError> output = new Ok<>(new StringBuilder());
        for (String segment : segments) {
            output = output.and(() -> compiler.apply(segment))
                    .mapValue(tuple -> tuple.left().append(tuple.right()));
        }

        return output.mapValue(StringBuilder::toString);
    }

    private static Result<String, CompileError> compileRootSegment(String input) {
        return compileDisjunction("root segment", input, List.of(
                Main::compilePackage,
                Main::compileImport,
                Main::compileClass,
                Main::compileInterface,
                Main::compileRecord
        ));
    }

    private static Result<String, CompileError> compileDisjunction(String type, String input, List<Compiler> compilers) {
        List<CompileError> errors = new ArrayList<>();
        for (Compiler compiler : compilers) {
            Result<String, CompileError> compiled = compiler.compile(input);
            if (compiled.isOk()) return compiled;
            compiled.findError().ifPresent(errors::add);
        }

        return new Err<>(new CompileError("Invalid " + type, input, errors));
    }

    private static Result<String, CompileError> compileRecord(String input) {
        int recordKeyword = input.indexOf("record ");
        if (recordKeyword < 0) return createInfixError(input, "record ");

        String right = input.substring(recordKeyword + "record ".length());
        int contentStart = right.indexOf("{");
        if (contentStart < 0) return createInfixError(right, "{");

        String beforeContent = right.substring(0, contentStart).strip();
        if (beforeContent.endsWith(">")) {
            if (beforeContent.contains("<")) {
                return generateWhitespace();
            }
        }
        return generateStruct(beforeContent, "");
    }

    private static Result<String, CompileError> compileInterface(String input) {
        int interfaceIndex = input.indexOf("interface ");
        if (interfaceIndex < 0) return createInfixError(input, "interface ");

        String right = input.substring(interfaceIndex + "interface ".length());
        int contentStart = right.indexOf("{");
        if (contentStart < 0) return createInfixError(right, "{");

        String beforeContent = right.substring(0, contentStart).strip();
        String withEnd = right.substring(contentStart + "{".length()).strip();

        if (beforeContent.contains(">")) {
            if (beforeContent.contains("<")) {
                return generateWhitespace();
            }
        }

        if (!withEnd.endsWith("}")) return createInfixError(input, "interface ");

        String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        return divideAndCompile(inputContent, Main::compileClassSegment)
                .flatMapValue(outputContent -> generateStruct(beforeContent, outputContent));
    }

    private static Result<String, CompileError> compilePackage(String input) {
        if (input.startsWith("package ")) return generateWhitespace();
        return createPrefixError(input, "package ");
    }

    private static Err<String, CompileError> createPrefixError(String input, String prefix) {
        return new Err<>(new CompileError("Prefix '" + prefix + "' not present", input));
    }

    private static Result<String, CompileError> compileImport(String input) {
        String stripped = input.strip();
        if (!stripped.startsWith("import ")) return createPrefixError(input, "import ");

        String right = stripped.substring("import ".length());
        if (!right.endsWith(";")) return createSuffixError(right, ";");

        String content = right.substring(0, right.length() - ";".length());
        List<String> segments = Arrays.asList(content.split(Pattern.quote(".")));
        if (segments.size() >= 3 && segments.subList(0, 3).equals(List.of("java", "util", "function")))
            return generateWhitespace();

        String joined = String.join("/", segments);
        return new Ok<>("#include <" + joined + ".h>\n");
    }

    private static Err<String, CompileError> invalidate(String type, String input) {
        return new Err<>(new CompileError("Invalid " + type, input));
    }

    private static Result<String, CompileError> compileClassSegment(String input) {
        return compileDisjunction("class segment", input, List.of(
                Main::compileWhitespace,
                Main::compileMethod,
                input1 -> compileStatement(input1, Main::compileDefinition)
        ));
    }

    private static Result<String, CompileError> compileStatement(String input1, Compiler content) {
        return truncateRight(input1, ";", content).mapValue(output -> "\t" + output + ";\n");
    }

    private static Result<String, CompileError> compileDefinition(String input) {
        String stripped = input.strip();
        int separator = stripped.lastIndexOf(" ");
        if (separator >= 0) {
            String beforeName = stripped.substring(0, separator);
            int typeSeparator = beforeName.lastIndexOf(" ");
            String type = typeSeparator >= 0
                    ? beforeName.substring(typeSeparator + " ".length())
                    : beforeName;

            String name = stripped.substring(separator + " ".length());
            return new Ok<>(type + " " + name);
        } else {
            return createInfixError(input, " ");
        }
    }

    private static Result<String, CompileError> truncateRight(String input, String suffix, Compiler compiler) {
        if (input.endsWith(suffix)) {
            return compiler.compile(input.substring(0, input.length() - suffix.length()));
        } else {
            return createSuffixError(input, suffix);
        }
    }

    private static Result<String, CompileError> compileWhitespace(String input1) {
        if (input1.isBlank()) return generateWhitespace();
        return new Err<>(new CompileError("Input is not blank", input1));
    }

    private static Result<String, CompileError> compileMethod(String input) {
        int definitionSeparator = input.indexOf("(");
        if (definitionSeparator >= 0) {
            String definition = input.substring(0, definitionSeparator).strip();
            int nameSeparator = definition.lastIndexOf(" ");
            if (nameSeparator >= 0) {
                String type = definition.substring(0, nameSeparator);
                String name = definition.substring(nameSeparator + " ".length()).strip();
                return new Ok<>("\t" +
                        type +
                        " (*" + name + ")();\n");
            }
        }
        return createInfixError(input, "(");
    }

    private static Result<String, CompileError> generateStruct(String name, String content) {
        return new Ok<>("struct " + name + " {\n" + content + "};\n");
    }

    private static Result<String, CompileError> generateWhitespace() {
        return new Ok<>("");
    }

    private static Result<String, CompileError> compileClass(String input) {
        int classIndex = input.indexOf("class ");
        if (classIndex < 0) return createInfixError(input, "class ");

        String right = input.substring(classIndex + "class ".length());
        int contentStart = right.indexOf("{");
        if (contentStart < 0) return createInfixError(right, "{");

        String beforeContent = right.substring(0, contentStart).strip();
        String withEnd = right.substring(contentStart + "{".length()).strip();

        String name;
        int implementsIndex = beforeContent.indexOf(" implements ");
        if (implementsIndex < 0) {
            name = beforeContent;
        } else {
            String beforeImplements = beforeContent.substring(0, implementsIndex).strip();
            if (!beforeImplements.endsWith(">")) {
                name = beforeImplements;
            } else {
                String withoutEnd = beforeImplements.substring(0, beforeImplements.length() - ">".length());
                int typeParamStart = withoutEnd.indexOf("<");
                if (typeParamStart < 0) {
                    name = beforeImplements;
                } else {
                    return new Ok<>("");
                }
            }
        }

        if (!withEnd.endsWith("}")) return createSuffixError(withEnd, "}");

        String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        return divideAndCompile(inputContent, Main::compileClassSegment).flatMapValue(outputContent -> {
            return generateStruct(name, outputContent);
        });
    }

    private static Err<String, CompileError> createSuffixError(String input, String suffix) {
        return new Err<>(new CompileError("Suffix '" + suffix + "' not present", input));
    }

    private static Err<String, CompileError> createInfixError(String input, String infix) {
        return new Err<>(new CompileError("Infix '" + infix + "' not present", input));
    }
}
