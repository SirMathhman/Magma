package magma;

import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;
import magma.app.rule.divide.DivideRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (var files = Files.walk(sourceDirectory)) {
            final var sources = files.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith("java"))
                    .toList();

            final var stringBuilder = new StringBuilder();
            for (var source : sources)
                stringBuilder.append(compileSource(sourceDirectory, source));

            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\n" + stringBuilder + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSource(Path sourceDirectory, Path source) throws IOException {
        final var relativeParent = sourceDirectory.relativize(source)
                .getParent();

        final Collection<String> namespace = new ArrayList<>();
        for (var i = 0; i < relativeParent.getNameCount(); i++)
            namespace.add(relativeParent.getName(i)
                    .toString());

        final var fileName = source.getFileName()
                .toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        final var joined = String.join(".", namespace);

        final var input = Files.readString(source);
        final var joinedName = joined + "." + name;

        final var output = compileRoot(input, joinedName);
        return "class " + joinedName + "\n" + output;
    }

    private static String compileRoot(String input, String source) {
        return createJavaRootRule().lex(input)
                .map(children -> transform(source, children))
                .flatMap(createPlantRootRule()::generate)
                .orElse("");
    }

    private static Rule createJavaRootRule() {
        return new DivideRule("children", createImportRule());
    }

    private static Rule createPlantRootRule() {
        return new DivideRule("children", createDependencyRule());
    }

    private static Node transform(String source, Node children) {
        final var transformed = children.findNodeList("children")
                .orElse(new ArrayList<>())
                .stream()
                .map(node -> node.withString("source", source))
                .toList();

        return new MapNode().withNodeList("children", transformed);
    }

    private static Rule createImportRule() {
        return new StripRule(new PrefixRule("import ", new SuffixRule(new StringRule("destination"), ";")));
    }

    private static Rule createDependencyRule() {
        return new SuffixRule(new InfixRule(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }
}
