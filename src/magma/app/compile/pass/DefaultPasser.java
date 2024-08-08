package magma.app.compile.pass;

import magma.api.Tuple;
import magma.app.compile.Node;
import magma.app.compile.lang.java.JavaLang;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.Declarations;
import magma.app.compile.lang.common.Symbols;
import magma.app.compile.lang.magma.Functions;
import magma.app.compile.lang.magma.MagmaDefinition;
import magma.app.compile.lang.magma.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static magma.app.compile.lang.common.Functions.PARAMS;
import static magma.app.compile.lang.common.Operations.INVOCATION;
import static magma.app.compile.lang.common.Modifiers.MODIFIERS;
import static magma.app.compile.lang.java.JavaLang.CONSTRUCTION;
import static magma.app.compile.lang.java.JavaLang.INTERFACE;
import static magma.app.compile.lang.java.JavaLang.LAMBDA;
import static magma.app.compile.lang.magma.MagmaDefinition.DEFINITION;
import static magma.app.compile.lang.magma.MagmaLang.TRAIT;

public class DefaultPasser implements Passer {
    public DefaultPasser() {
    }

    private static Optional<Tuple<Node, Integer>> postVisitArrays(Node node, int state) {
        return node.is(JavaLang.ARRAY)
                ? Optional.of(new Tuple<>(node.retype(MagmaDefinition.SLICE), state))
                : Optional.empty();
    }

    private static Optional<Tuple<Node, Integer>> postVisitDefinitions(Node node, int state) {
        if (!node.is(DEFINITION)) return Optional.empty();

        var oldModifiers = node.findStringList(MODIFIERS);
        Node node0;
        if (oldModifiers.isPresent()) {
            var strings = oldModifiers.get();
            if (strings.contains("private")) {
                var copy = new ArrayList<>(strings);
                copy.remove("private");
                node0 = copy.isEmpty() ? node.removeStringList(MODIFIERS) : node.withStringList(MODIFIERS, copy);
            } else {
                node0 = node;
            }
        } else {
            node0 = node;
        }

        var typeOptional = node0.findNode(MagmaDefinition.TYPE);
        if (typeOptional.isEmpty()) return Optional.of(new Tuple<>(node0, state));
        var type = typeOptional.get();

        if (!type.is(Symbols.SYMBOL)) return Optional.of(new Tuple<>(node0, state));
        var value = type.findString(Symbols.VALUE).orElseThrow();

        if (!value.equals("Void")) return Optional.of(new Tuple<>(node0, state));
        return Optional.of(new Tuple<>(node0.removeNode(MagmaDefinition.TYPE), state));
    }

    private static Optional<Tuple<Node, Integer>> postVisitDeclarations(Node node, int state) {
        if (!node.is(Declarations.DECLARATION)) return Optional.empty();

        var definition = node.findNode(Declarations.DEFINITION).orElseThrow();
        var oldModifiers = new ArrayList<>(definition.findStringList(MODIFIERS).orElse(Collections.emptyList()));
        oldModifiers.add("let");

        var withModifiers = definition.withStringList(MODIFIERS, oldModifiers);
        return Optional.of(new Tuple<>(node.withNode(Declarations.DEFINITION, withModifiers), state));
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
                .retype(DEFINITION)
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

        var params = node.findNodeList(PARAMS).orElse(Collections.emptyList());

        var definition = node.findNode(JavaLang.METHOD_DEFINITION)
                .orElseThrow()
                .mapStringList(MODIFIERS, modifiers -> {
                    var copy = new ArrayList<>(modifiers);
                    copy.add("def");
                    return copy;
                });

        var withParams = definition.withNodeList(PARAMS, params);

        return Optional.of(new Tuple<>(node.retype("function")
                .removeNodeList(PARAMS)
                .withNode(JavaLang.METHOD_DEFINITION, withParams), state));
    }

    private static Optional<Tuple<Node, Integer>> postVisitBlocks(Node node, int state) {
        if (!node.is(Blocks.BLOCK)) return Optional.empty();

        var childrenOptional = node.findNodeList(Blocks.CHILDREN);
        if (childrenOptional.isEmpty()) return Optional.empty();

        var newChildren = new ArrayList<Node>();
        Iterator<Node> iterator = childrenOptional.get().iterator();
        while (iterator.hasNext()) {
            var child = iterator.next();
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

        Iterator<Node> iterator = oldChildren.iterator();
        while (iterator.hasNext()) {
            Node oldChild = iterator.next();
            var definitionOptional = oldChild.findNode(DEFINITION);
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

            Node newDefinition;
            var modifiersOptional = memberDefinition.findStringList(MODIFIERS);
            if (modifiersOptional.isPresent()) {
                var copy = new ArrayList<>(modifiersOptional.get());
                copy.remove("static");
                newDefinition = copy.isEmpty()
                        ? memberDefinition.removeStringList(MODIFIERS)
                        : memberDefinition.withStringList(MODIFIERS, copy);
            } else {
                newDefinition = memberDefinition;
            }

            var node1 = oldChild.withNode(DEFINITION, newDefinition);
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
                .or(() -> postVisitMethods(node, state))
                .or(() -> postVisitConstructions(node, state))
                .or(() -> postVisitInterface(node, state))
                .or(() -> postVisitLambda(node, state));
    }

    private Optional<? extends Tuple<Node, Integer>> postVisitLambda(Node node, int state) {
        if (!node.is(LAMBDA)) return Optional.empty();

        var params = node.findNodeList(PARAMS).orElse(Collections.emptyList());
        var definition = new Node(DEFINITION)
                .withNodeList(MagmaDefinition.PARAMS, params);

        return Optional.of(new Tuple<>(node.retype(Functions.FUNCTION)
                .withNode(DEFINITION, definition), state));
    }

    private Optional<? extends Tuple<Node, Integer>> postVisitInterface(Node node, int state) {
        if(node.is(INTERFACE)) return Optional.of(new Tuple<>(node.retype(TRAIT), state));
        else return Optional.empty();
    }

    private Optional<? extends Tuple<Node, Integer>> postVisitConstructions(Node node, int state) {
        if(node.is(CONSTRUCTION)) return Optional.of(new Tuple<>(node.retype(INVOCATION), state));
        else return Optional.empty();
    }

    @Override
    public Optional<Tuple<Node, Integer>> preVisit(Node node, int state) {
        if (node.is(Blocks.BLOCK)) {
            return Optional.of(new Tuple<>(node, state + 1));
        }

        if (node.is(DEFINITION)) {
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