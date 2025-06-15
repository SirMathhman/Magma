package magma.app.compile;

import magma.app.compile.lang.java.JavaLang;
import magma.app.compile.lang.PlantUMLLang;
import magma.app.compile.node.MapNode;

import java.util.Collection;
import java.util.List;

public class Compiler {
    public static StringResult<CompileError> compileRoot(String input, String name) {
        return JavaLang.createJavaRootRule().lex(input)
                .findNodeList("children")
                .transform(children -> transform(name, children))
                .generate(children1 -> PlantUMLLang.createPlantUMLRootRule().generate(new MapNode().withNodeList("children", children1)));
    }

    public static List<Node> transform(String name, Collection<Node> list) {
        return list.stream()
                .map(node -> node.withString("source", name))
                .toList();
    }
}