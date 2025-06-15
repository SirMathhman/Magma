package magma;

import magma.app.Node;
import magma.app.State;
import magma.app.rule.LastRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            final var output = new StringBuilder();
            for (var source : sources)
                output.append(compileSource(source));

            Files.writeString(Paths.get(".", "diagram.puml"), "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSource(Path source) throws IOException {
        final var fileName = source.getFileName()
                .toString();

        final var extensionSeparator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, extensionSeparator);

        final var output = new StringBuilder();
        final var segments = divide(Files.readString(source));
        for (var segment : segments) {
            final var extracted = createImportRule().lex(segment)
                    .map(node -> generate(node.withString("parent", name)));

            extracted.ifPresent(output::append);
        }

        return output.append("class ")
                .append(name)
                .append("\n")
                .toString();
    }

    private static StripRule createImportRule() {
        return new StripRule(new PrefixRule("import ", new SuffixRule(new LastRule(new StringRule("parent"), ".", new StringRule("child")), ";")));
    }

    private static String generate(Node node) {
        return new SuffixRule(new LastRule(new StringRule("parent"), " --> ", new StringRule("child")), "\n").generate(node)
                .orElse("");
    }

    private static List<String> divide(CharSequence input) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }
}
