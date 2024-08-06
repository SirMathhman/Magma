package magma.app.compile;

import magma.api.Tuple;
import magma.app.compile.lang.CommonLang;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.Symbols;
import magma.app.compile.lang.magma.Functions;
import magma.app.compile.lang.magma.MagmaDefinition;
import magma.app.compile.lang.magma.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static magma.app.compile.lang.CommonLang.MODIFIERS;

public class DefaultVisitor implements Visitor {
    public DefaultVisitor() {
    }

    private static Optional<Tuple<Node, Integer>> postVisitArrays(Node node, int state) {
        return node.is(JavaLang.ARRAY)
                ? Optional.of(new Tuple<>(node.retype(MagmaDefinition.SLICE), state))
                : Optional.empty();
    }

    private static Optional<Tuple<Node, Integer>> postVisitDefinitions(Node node, int state) {
        if (!node.is(MagmaDefinition.DEFINITION)) return Optional.empty();
        var typeOptional = node.findNode(MagmaDefinition.TYPE);

        if (typeOptional.isEmpty()) return Optional.empty();
        var type = typeOptional.get();

        if (!type.is(Symbols.SYMBOL)) return Optional.empty();
        var value = type.findString(Symbols.VALUE).orElseThrow();

        if (!value.equals("Void")) return Optional.empty();
        return Optional.of(new Tuple<>(node.removeNode(MagmaDefinition.TYPE), state));
    }

    private static Optional<Tuple<Node, Integer>> postVisitDeclarations(Node node, int state) {
        if (!node.is(CommonLang.DECLARATION_TYPE)) return Optional.empty();

        var definition = node.findNode(CommonLang.DECLARATION_DEFINITION).orElseThrow();
        var oldModifiers = new ArrayList<>(definition.findStringList(MODIFIERS).orElse(Collections.emptyList()));
        oldModifiers.add("let");

        var withModifiers = definition.withStringList(MODIFIERS, oldModifiers);
        return Optional.of(new Tuple<>(node.withNode(CommonLang.DECLARATION_DEFINITION, withModifiers), state));
    }

    private static Optional<Tuple<Node, Integer>> postVisitClasses(Node node, int state) {
        if (!node.is(JavaLang.CLASS_TYPE)) return Optional.empty();

        var name = node.findString(JavaLang.CLASS_NAME).orElseThrow();
        var oldModifiers = node.findStringList(MODIFIERS).orElseThrow();

        var newModifiers = new ArrayList<>(oldModifiers
                .stream()
                .map(modifier -> modifier.equals("public") ? "export" : modifier)
                .toList());

        newModifiers.add("class");
        newModifiers.add("def");

        var definition = new Node()
                .retype(MagmaDefinition.DEFINITION)
                .withString(MagmaDefinition.NAME, name)
                .withStringList(MODIFIERS, newModifiers);

        var function = node.retype(Functions.FUNCTION)
                .removeString(JavaLang.CLASS_NAME)
                .removeStringList("modifiers")
                .withNode("definition", definition);

        return Optional.of(new Tuple<>(function, state));
    }

    private static Optional<Tuple<Node, Integer>> postVisitMethods(Node node, int state) {
        if (!node.is(JavaLang.METHOD_TYPE)) return Optional.empty();

        var params = node.findNodeList(CommonLang.PARAMS).orElse(Collections.emptyList());
        var definition = node.findNode(JavaLang.METHOD_DEFINITION).orElseThrow();
        var withParams = definition.withNodeList(CommonLang.PARAMS, params);
        return Optional.of(new Tuple<>(node.retype("function")
                .removeNodeList(CommonLang.PARAMS)
                .withNode(JavaLang.METHOD_DEFINITION, withParams), state));
    }

    private static Optional<Tuple<Node, Integer>> postVisitBlocks(Node node, int state) {
        if (!node.is(Blocks.BLOCK)) return Optional.empty();

        var childrenOptional = node.findNodeList(Blocks.CHILDREN);
        if (childrenOptional.isEmpty()) return Optional.empty();

        var newChildren = new ArrayList<Node>();
        for (var child : childrenOptional.get()) {
            if (child.is(JavaLang.PACKAGE)) continue;
            newChildren.addAll(flattenClass(child).orElse(Collections.singletonList(child)));
        }

        var newNode = node.withNodeList(Blocks.CHILDREN, newChildren);
        return Optional.of(new Tuple<>(newNode, state - 1));
    }

    private static Optional<List<Node>> flattenClass(Node child) {
        if (!child.is(Functions.FUNCTION)) return Optional.empty();
        var definition = child.findNode(Functions.DEFINITION).orElseThrow();

        var oldModifiers = definition.findStringList(MODIFIERS).orElse(Collections.emptyList());
        if (!oldModifiers.contains("class")) return Optional.empty();

        var name = definition.findString(MagmaDefinition.NAME).orElseThrow();
        var oldValue = child.findNode(Functions.VALUE).orElseThrow();
        var oldChildren = oldValue.findNodeList(Blocks.CHILDREN).orElseThrow();

        var instanceChildren = new ArrayList<Node>();
        var staticChildren = new ArrayList<Node>();

        for (Node oldChild : oldChildren) {
            var definitionOptional = oldChild.findNode(MagmaDefinition.DEFINITION);
            if (definitionOptional.isEmpty()) {
                instanceChildren.add(oldChild);
                continue;
            }

            var memberDefinition = definitionOptional.get();
            var modifiers = memberDefinition.findStringList(MODIFIERS).orElse(Collections.emptyList());
            if (!modifiers.contains("static")) {
                instanceChildren.add(oldChild);
                continue;
            }

            var newDefinition = memberDefinition.mapStringList(MODIFIERS, oldMemberModifiers -> {
                var copy = new ArrayList<>(oldMemberModifiers);
                copy.remove("static");
                return copy;
            });

            var node1 = oldChild.withNode(MagmaDefinition.DEFINITION, newDefinition);
            staticChildren.add(node1);
        }

        var list = new ArrayList<Node>();
        if (!instanceChildren.isEmpty()) {
            list.add(child.withNode(Functions.VALUE, oldValue.withNodeList(Blocks.CHILDREN, instanceChildren)));
        }

        if (!staticChildren.isEmpty()) {
            var block = new Node(Blocks.BLOCK)
                    .withNodeList(Blocks.CHILDREN, staticChildren);

            list.add(new Node(Objects.OBJECT)
                    .withString(MagmaDefinition.NAME, name + "s")
                    .withNode(Objects.VALUE, block));
        }

        return Optional.of(list);

    }

    @Override
    public Optional<Tuple<Node, Integer>> postVisit(Node node, int state) {
        return postVisitArrays(node, state)
                .or(() -> postVisitDefinitions(node, state))
                .or(() -> postVisitDeclarations(node, state))
                .or(() -> postVisitClasses(node, state))
                .or(() -> postVisitBlocks(node, state))
                .or(() -> postVisitMethods(node, state));

    }

    @Override
    public Optional<Tuple<Node, Integer>> preVisit(Node node, int state) {
        if (node.is(Blocks.BLOCK)) {
            return Optional.of(new Tuple<>(node, state + 1));
        }

        if (node.is(MagmaDefinition.DEFINITION)) {
            var typeOptional = node.findNode(MagmaDefinition.TYPE);
            if (typeOptional.isPresent()) {
                var type = typeOptional.get();
                if (type.is(Symbols.SYMBOL)) {
                    var value = type.findString(Symbols.VALUE).orElseThrow();
                    if (value.equals("var")) {
                        return Optional.of(new Tuple<>(node.removeNode(MagmaDefinition.TYPE), state));
                    }
                }
            }
        }

        if (node.is(Symbols.SYMBOL)) {
            var mapped = node.mapString(Symbols.VALUE, value -> {
                if (value.equals("void")) return "Void";
                return value;
            });

            return Optional.of(new Tuple<>(mapped, state));
        }

        return Optional.empty();
    }
}