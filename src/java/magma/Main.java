package magma;

import magma.app.State;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
        return createImportRule().lex(input).maybeValue().flatMap(node -> {
            return createDependencyRule().generate(node.withString("source", name)).value();
        }).orElse("");
    }

    private static Rule createImportRule() {
        final var parent = new StringRule("parent");
        final var destination = new StringRule("destination");
        final var rule = new PrefixRule("import ", new SuffixRule(new InfixRule(parent, ".", destination), ";"));
        return new StripRule(rule);
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