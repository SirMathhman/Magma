package magma;

import magma.app.Node;
import magma.app.rule.DivideRule;
import magma.app.rule.LastRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
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

        final var input = Files.readString(source);
        return new DivideRule("children", createImportRule()).lex(input)
                .flatMap(tree -> getString(tree, name))
                .orElse("");
    }

    private static Optional<String> getString(Node tree, String name) {
        final var children1 = transform(tree, name);
        return new DivideRule("children", createDependencyRule()).generate(children1)
                .map(joined -> generate(name, joined));
    }

    private static String generate(String name, String joined) {
        return "class " + name + "\n" + joined;
    }

    private static Node transform(Node tree, String name) {
        final var list = tree.findNodeList("children")
                .orElse(new ArrayList<>())
                .stream()
                .map(segment -> segment.withString("parent", name))
                .toList();

        return new Node().withNodeList("children", list);
    }

    private static SuffixRule createDependencyRule() {
        final var parent = new StringRule("parent");
        final var child = new StringRule("child");
        return new SuffixRule(new LastRule(parent, " --> ", child), "\n");
    }

    private static StripRule createImportRule() {
        return new StripRule(new PrefixRule("import ", new SuffixRule(new LastRule(new StringRule("parent"), ".", new StringRule("child")), ";")));
    }

}
