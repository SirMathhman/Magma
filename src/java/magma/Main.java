package magma;

import magma.lang.Lang;
import magma.node.EverythingNode;
import magma.node.MapNode;
import magma.node.result.NodeResult;
import magma.rule.DivideRule;
import magma.rule.Rule;
import magma.rule.StringOk;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static String runWithSources(final Iterable<Path> files) throws IOException {
        final var pre = Stream.of("@startuml", "skinparam linetype ortho")
                              .map(value -> value + Lang.LINE_SEPARATOR)
                              .collect(Collectors.joining());

        StringResult outputRootSegments = new StringOk(pre);
        for (final var source : files) {
            final var input = Files.readString(source);

            final var fileName = source.getFileName().toString();
            final var separator = fileName.lastIndexOf('.');
            final var parent = fileName.substring(0, separator);

            final var output = Main.compile(input, parent);
            outputRootSegments = outputRootSegments.appendResult(output);
        }

        return outputRootSegments.appendSlice("@enduml");
    }

    private static StringResult compile(final String input, final String parent) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> everythingNodeRule =
                Main.createJavaRootRule();
        return everythingNodeRule.lex(input).match(root -> {
            final var newChildren = Main.modify(parent, root);
            final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> everythingNodeRule1 =
                    Lang.createPlantRootRule();
            return everythingNodeRule1.generate(newChildren);
        }, StringErr::new);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createJavaRootRule() {
        return new DivideRule("children", Lang.createJavaRootSegmentRule());
    }

    private static EverythingNode modify(final String parent, final EverythingNode root) {
        final var newChildren = root.findNodeList("children")
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .map(child -> Main.modifyRootSegment(parent, child))
                                    .toList();

        final EverythingNode node = new MapNode();
        return node.withNodeList("children", newChildren);
    }

    private static EverythingNode modifyRootSegment(final String parent, final EverythingNode node) {
        if (node.is("import")) return Main.modifyImport(parent, node);
        return Main.modifyStructure(node);
    }

    private static EverythingNode modifyStructure(final EverythingNode structure) {
        final var maybeBase = structure.findString("base").map(value -> " implements " + value).orElse("");
        final var name = structure.findString("name").orElse("");
        if (structure.is("record")) return structure.retype("class").withString("content", name + maybeBase);

        return structure.withString("content", name + maybeBase);
    }

    private static EverythingNode modifyImport(final String parent, final EverythingNode child1) {
        return child1.retype("dependency").withString("parent", parent);
    }
}
