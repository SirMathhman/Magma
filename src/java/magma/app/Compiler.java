package magma.app;

import magma.app.compile.Lang;
import magma.app.compile.Node;
import magma.app.compile.node.MapNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class Compiler {
    private static Optional<String> parseAndGenerate(Node tree, String name) {
        final var children1 = transform(tree, name);
        return Lang.createPlantUMLRootRule()
                .generate(children1)
                .map(joined -> generate(name, joined));
    }

    private static String generate(String name, String joined) {
        return "class " + name + "\n" + joined;
    }

    private static Node transform(Node tree, String name) {
        final var list = tree.nodeLists()
                .find("children")
                .orElse(new ArrayList<>())
                .stream()
                .map(segment -> segment.strings()
                        .with("parent", name))
                .toList();

        return new MapNode().nodeLists()
                .with("children", list);
    }

    public String compile(Map<String, String> inputs) {
        final var buffer = new StringBuilder();
        for (var input : inputs.entrySet()) {
            final var str = Lang.createJavaRootRule()
                    .lex(input.getValue())
                    .flatMap(tree -> parseAndGenerate(tree, input.getKey()))
                    .orElse("");

            buffer.append(str);
        }

        return "@startuml\nskinparam linetype ortho\n" + buffer + "@enduml";
    }
}
