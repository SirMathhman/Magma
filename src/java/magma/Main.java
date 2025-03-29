package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Options;
import magma.option.Some;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static final Path SOURCE_DIRECTORY = Paths.get(".", "src", "java");
    public static final Path TARGET_DIRECTORY = Paths.get(".", "src", "clang");

    public static void main(String[] args) {
        try (Stream<Path> stream = Files.walk(SOURCE_DIRECTORY)) {
            Set<Path> sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toSet());

            runWithSources(sources);
        } catch (CompileException | IOException | InterruptedException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(Set<Path> sources) throws IOException, CompileException, InterruptedException {
        ArrayList<Path> relatives = new ArrayList<>();
        for (Path source : sources) {
            relatives.add(runWithSource(source));
        }

        Path build = TARGET_DIRECTORY.resolve("build.bat");
        String collect = relatives.stream()
                .map(Path::toString)
                .map(path -> ".\\" + path + "^\n\t")
                .collect(Collectors.joining());

        Files.writeString(build, "clang " +
                collect +
                "-o main.exe");

        new ProcessBuilder("cmd.exe", "/c", "build.bat")
                .directory(TARGET_DIRECTORY.toFile())
                .inheritIO()
                .start()
                .waitFor();
    }

    private static Path runWithSource(Path source) throws IOException, CompileException {
        Path relative = SOURCE_DIRECTORY.relativize(source);
        Path parent = relative.getParent();

        ArrayList<String> namespace = new ArrayList<>();
        for (int i = 0; i < parent.getNameCount(); i++) {
            namespace.add(parent.getName(i).toString());
        }

        String input = Files.readString(source);
        String output = compile(input, namespace);

        String nameWithExt = relative.getFileName().toString();
        String name = nameWithExt.substring(0, nameWithExt.lastIndexOf("."));

        Path targetParent = TARGET_DIRECTORY.resolve(parent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        Path target = targetParent.resolve(name + ".c");
        Files.writeString(target, output);

        Path header = targetParent.resolve(name + ".h");
        Files.createFile(header);

        return TARGET_DIRECTORY.relativize(target);
    }

    private static String compile(String input, ArrayList<String> namespace) throws CompileException {
        List<String> segments = divideStatements(input);

        StringBuilder builder = new StringBuilder();
        for (String segment : segments) {
            builder.append(compileRootSegment(segment, namespace));
        }

        return builder.toString();
    }

    private static List<String> divideStatements(String input) {
        DividingState current = new MutableDividingState();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            current = divideStatementChar(current, c);
        }

        return current.advance().segments();
    }

    private static DividingState divideStatementChar(DividingState current, char c) {
        DividingState appended = current.append(c);
        if (c == ';' && appended.isLevel()) return appended.advance();
        if (c == '{') return appended.enter();
        if (c == '}') return appended.exit();
        return appended;
    }

    private static String compileRootSegment(String input, ArrayList<String> namespace) throws CompileException {
        List<Rule> rules = List.of(
                new Rule() {
                    @Override
                    public Option<String> compile(String input) {
                        return Options.fromNative(compile0(input));
                    }

                    private Optional<String> compile0(String input1) {
                        return compilePackage(input1);
                    }
                },
                new Rule() {
                    @Override
                    public Option<String> compile(String input) {
                        return compileImport(input, namespace);
                    }
                },
                new Rule() {
                    @Override
                    public Option<String> compile(String input) {
                        return compileClass(input);
                    }
                },
                new Rule() {
                    @Override
                    public Option<String> compile(String input) {
                        return compileInterface(input);
                    }

                },
                new Rule() {
                    @Override
                    public Option<String> compile(String input) {
                        if (input.contains("record ")) {
                            return generateStruct();
                        }

                        return new None<>();
                    }
                }
        );

        for (Rule rule : rules) {
            Optional<String> maybe = Options.toNative(rule.compile(input));
            if (maybe.isPresent()) return maybe.get();
        }

        throw new CompileException("Invalid root segment", input);
    }

    private static Optional<String> compilePackage(String input) {
        if (input.startsWith("package ")) return Optional.of("");
        return Optional.empty();
    }

    private static Option<String> compileClass(String input) {
        if (input.contains("class ")) {
            return generateStruct();
        }
        return new None<>();
    }

    private static Option<String> generateStruct() {
        return new Some<>("struct Temp {\n};\n");
    }

    private static Option<String> compileInterface(String input) {
        if (input.contains("interface ")) {
            return generateStruct();
        }
        return new None<>();
    }

    private static Option<String> compileImport(String input, ArrayList<String> thisNamespace) {
        if (!input.strip().startsWith("import ")) return new None<>();

        String right = input.strip().substring("import ".length());
        if (!right.endsWith(";")) return new None<>();

        String namespaceString = right.substring(0, right.length() - ";".length());
        List<String> otherNamespace = Arrays.asList(namespaceString.split(Pattern.quote(".")));

        ArrayList<String> copy = new ArrayList<>();
        for (int i = 0; i < thisNamespace.size(); i++) {
            copy.add("..");
        }
        copy.addAll(otherNamespace);

        String joined = String.join("/", copy);
        return new Some<>("#include \"" +
                joined +
                ".h\"\n");
    }
}
