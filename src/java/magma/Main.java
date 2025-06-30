package magma;

import magma.lang.Lang;
import magma.node.MapNode;
import magma.node.Node;
import magma.rule.DivideRule;
import magma.rule.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class Main {
    private Main() {}

    public static void main(final String[] args) {
        final var root = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(root)) {
            final Collector<Path, ?, Set<Path>> setCollector = Collectors.toSet();
            final var files = stream.filter(Files::isRegularFile).filter(Main::isJavaFile).collect(setCollector);

            final var outputRootSegments = Main.runWithSources(files);
            final var target = Paths.get(".", "diagram.puml");
            final var joined = String.join(Lang.LINE_SEPARATOR, outputRootSegments);
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static boolean isJavaFile(final Path file) {
        final var asString = file.toString();
        return asString.endsWith(".java");
    }

    private static Collection<String> runWithSources(final Iterable<Path> files) throws IOException {
        final var pre = List.of("@startuml", "skinparam linetype ortho");
        final Collection<String> outputRootSegments = new ArrayList<>(pre);
        for (final var source : files) {
            final var input = Files.readString(source);

            final var fileName = source.getFileName().toString();
            final var separator = fileName.lastIndexOf('.');
            final var parent = fileName.substring(0, separator);

            final var output = Main.compile(input, parent);
            outputRootSegments.add("class " + parent);
            outputRootSegments.add(output);
        }

        outputRootSegments.add("@enduml");
        return outputRootSegments;
    }

    private static String compile(final String input, final String parent) {
        return Main.createJavaRootRule().lex(input).flatMap(root -> {
            final var newChildren = Main.modify(parent, root);
            return Lang.createPlantRootRule().generate(newChildren);
        }).orElse("");
    }

    private static Rule createJavaRootRule() {
        return new DivideRule("children", Lang.createJavaRootSegmentRule());
    }

    private static Node modify(final String parent, final Node root) {
        final var newChildren = root.findNodeList("children")
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .map(child -> Main.modifyRootSegment(parent, child))
                                    .toList();

        return new MapNode().withNodeList("children", newChildren);
    }

    private static Node modifyRootSegment(final String parent, final Node node) {
        if (node.is("import")) return Main.modifyImport(parent, node);
        if (node.is("record")) return Main.modifyStructure(node);
        return node;
    }

    private static Node modifyStructure(final Node header) {
        if (header.is("record")) {
            final var content = header.findString("name").orElse("") + " " + header.findString("more").orElse("");
            return header.retype("class").withString("before-content", content);
        }

        return header;
    }

    private static Node modifyImport(final String parent, final Node child1) {
        return child1.retype("dependency").withString("parent", parent);
    }
}
