package magma;

import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.rule.DivideRule;
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
import java.util.ArrayList;
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
        for (var source : sources)
            output1.append(compileSource(source));
        return "@startuml\nskinparam linetype ortho\n" + output1 + "@enduml";
    }

    private static String compileSource(Path source) throws IOException {
        final var input = Files.readString(source);
        final var fileName = source.getFileName().toString();
        final var name = fileName.substring(0, fileName.lastIndexOf("."));
        return "class " + name + "\n" + compileInput(input, name);
    }

    private static String compileInput(String input, String name) {
        return new DivideRule("children", createImportRule()).lex(input).maybeValue().flatMap(root -> {
            final var dependencyRule = createDependencyRule();
            final var parsed = transform(name, root);
            return new DivideRule("children", dependencyRule).generate(parsed).value();
        }).orElse("");
    }

    private static Node transform(String name, Node root) {
        final var children = root.findNodeList("children").orElse(new ArrayList<>());
        return new MapNode().withNodeList("children", children.stream().map(child -> child.withString("source", name)).toList());
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
}