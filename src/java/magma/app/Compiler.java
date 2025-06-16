package magma.app;

import magma.api.Result;
import magma.app.compile.CompileError;
import magma.app.compile.lang.CommonLang;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.node.NodeWithNodeLists;

import java.util.ArrayList;
import java.util.Map;

public class Compiler {
    private static Result<String, CompileError> parseAndGenerate(NodeWithNodeLists<NodeWithEverything> tree, String name) {
        final var children1 = transform(tree, name);
        return CommonLang.createPlantUMLRootRule()
                .generate(children1)
                .map(joined -> generate(name, joined));
    }

    private static String generate(String name, String joined) {
        return "class " + name + "\n" + joined;
    }

    private static NodeWithEverything transform(NodeWithNodeLists<NodeWithEverything> tree, String name) {
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
            final var str = CommonLang.createJavaRootRule()
                    .lex(input.getValue())
                    .flatMap(tree -> parseAndGenerate(tree, input.getKey()))
                    .findValue()
                    .orElse("");

            buffer.append(str);
        }

        return "@startuml\nskinparam linetype ortho\n" + buffer + "@enduml";
    }
}
