package magma;

import magma.app.State;
import magma.app.rule.InfixRule;
import magma.app.rule.result.LexResult;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        try (var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).collect(Collectors.toSet());

            final var output = compileSources(sources);
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSources(Set<Path> sources) throws IOException {
        final var output1 = new StringBuilder();
        for (var source : sources) output1.append(compileSource(source));
        return "@startuml\nskinparam linetype ortho\n" + output1 + "@enduml";
    }

    private static String compileSource(Path source) throws IOException {
        final var input = Files.readString(source);
        final var fileName = source.getFileName().toString();
        final var name = fileName.substring(0, fileName.lastIndexOf("."));
        return "class " + name + "\n" + compileInput(input, name);
    }

    private static String compileInput(String input, String name) {
        final var segments = divide(input);
        final var output = new StringBuilder();
        for (var segment : segments) output.append(compileRootSegment(name, segment));
        return output.toString();
    }

    private static String compileRootSegment(String name, String input) {
        return compileImport(name, input).orElse("");
    }

    private static Optional<String> compileImport(String source, String input) {
        final var stripped = input.strip();
        if (!stripped.startsWith("import ")) return Optional.empty();

        final var substring = stripped.substring("import ".length());
        if (!substring.endsWith(";")) return Optional.empty();

        final var substring1 = substring.substring(0, substring.length() - ";".length());

        return getString(substring1, ".", new StringRule("destination")).maybeValue().flatMap(node -> {
            return createDependencyRule().generate(node.withString("source", source)).value();
        });
    }

    private static LexResult getString(String input, String infix, StringRule childRule) {
        final var index = input.lastIndexOf(infix);
        if (index < 0) return new LexResult(Optional.empty());

        final var leftString = input.substring(0, index);
        final var rightString = input.substring(index + infix.length());

        final var leftRule = new StringRule("parent");
        return leftRule.lex(leftString).merge(() -> childRule.lex(rightString));
    }

    private static Rule createDependencyRule() {
        return new SuffixRule(new InfixRule(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    private static List<String> divide(String input) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments();
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';') return appended.advance();
        return appended;
    }
}