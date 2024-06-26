package magma.compile.lang.java;

import magma.api.Tuple;
import magma.api.contain.List;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.compile.CompileError;
import magma.compile.Error_;
import magma.compile.annotate.State;
import magma.compile.lang.Visitor;
import magma.compile.rule.Node;
import magma.java.JavaList;

public class TemplateNormalizer implements Visitor {
    private static List<String> computeNewModifiers(Node node) {
        var oldModifiers = node.findStringList("modifiers").orElse(JavaList.empty());
        var newModifiers = JavaList.<String>empty();
        return oldModifiers.contains("public") ? newModifiers.addLast("export") : newModifiers;
    }

    private static Result<Tuple<Node, State>, Error_> generateDefinition(String name, Node node, State state) {
        var classModifiers = JavaList.of("class", "def");
        var stringList = computeNewModifiers(node).addAll(classModifiers);
        var params = node.findNodeList("params").orElse(JavaList.empty());

        var definition = node.clear("definition")
                .withString("name", name)
                .withStringList("modifiers", stringList)
                .withNodeList("params", params);

        var withTypeParams = node.findNodeList("type-params")
                .map(typeParams -> definition.withNodeList("type-params", typeParams))
                .orElse(definition);

        var function = node.retype("function").withNode("definition", withTypeParams);
        var withImplements = function.findNode("interface")
                .map(interfaceType -> moveImplements(function, interfaceType))
                .orElse(function);

        var tuple = new Tuple<>(withImplements, state);
        return new Ok<>(tuple);
    }

    private static Node moveImplements(Node function, Node interfaceType) {
        var implementsStatement = function.clear("implements").withNode("type", interfaceType);

        return function.withNode("child", function.findNode("child")
                .map(child -> child.mapNodes("children", children -> children.addLast(implementsStatement)))
                .orElse(implementsStatement));
    }

    @Override
    public Result<Tuple<Node, State>, Error_> preVisit(Node node, State state) {
        return node.findString("name")
                .map(name -> generateDefinition(name, node, state))
                .orElseGet(() -> new Err<>(new CompileError("No name present.", node.toString())));
    }
}
