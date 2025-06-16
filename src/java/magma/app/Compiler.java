package magma.app;

import magma.api.Ok;
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
                .mapValue(joined -> generate(name, joined));
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

    public Result<String, CompileError> compile(Map<String, String> inputs) {
        Result<StringBuilder, CompileError> buffer = new Ok<>(new StringBuilder());
        for (var input : inputs.entrySet())
            buffer = buffer.flatMap(inner -> {
                final var result = CommonLang.createJavaRootRule()
                        .lex(input.getValue())
                        .flatMap(tree -> parseAndGenerate(tree, input.getKey()));

                return result.mapValue(inner::append);
            });

        return buffer.mapValue(inner -> "@startuml\nskinparam linetype ortho\n" + inner + "@enduml");
    }
}
