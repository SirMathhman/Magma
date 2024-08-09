package magma.app.compile.pass;

import magma.api.Optionals;
import magma.api.Tuple;
import magma.app.compile.Node;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.lang.common.Declarations;
import magma.app.compile.lang.common.Symbols;
import magma.app.compile.lang.java.JavaLang;
import magma.app.compile.lang.magma.Functions;
import magma.app.compile.lang.magma.MagmaDefinition;
import magma.app.compile.lang.magma.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static magma.app.compile.lang.common.Functions.PARAMS;
import static magma.app.compile.lang.common.Modifiers.MODIFIERS;
import static magma.app.compile.lang.common.Operations.INVOCATION;
import static magma.app.compile.lang.java.JavaLang.CONSTRUCTION;
import static magma.app.compile.lang.java.JavaLang.INTERFACE;
import static magma.app.compile.lang.java.JavaLang.LAMBDA;
import static magma.app.compile.lang.java.JavaLang.PACKAGE;
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
        var node0 = oldModifiers.map(value -> removePrivateKeyword(node, value)).orElse(node);

        var typeOptional = node0.findNode(MagmaDefinition.TYPE);
        if (typeOptional.isEmpty()) return Optional.of(new Tuple<>(node0, state));
        var type = typeOptional.get();

        if (!type.is(Symbols.SYMBOL)) return Optional.of(new Tuple<>(node0, state));
        var value = type.findString(Symbols.VALUE).orElseThrow();

        if (!value.equals("Void")) return Optional.of(new Tuple<>(node0, state));
        return Optional.of(new Tuple<>(node0.removeNode(MagmaDefinition.TYPE), state));
    }

    private static Node removePrivateKeyword(Node node, List<String> oldModifiers) {
        if (!oldModifiers.contains("private")) return node;

        var copy = new ArrayList<>(oldModifiers);
        copy.remove("private");
        return copy.isEmpty()
                ? node.removeStringList(MODIFIERS)
                : node.withStringList(MODIFIERS, copy);
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
                .mapStringList(MODIFIERS, DefaultPasser::addDefModifier);

        var withParams = definition.withNodeList(PARAMS, params);
        return Optional.of(new Tuple<>(node.retype("function")
                .removeNodeList(PARAMS)
                .withNode(JavaLang.METHOD_DEFINITION, withParams), state));
    }

    private static ArrayList<String> addDefModifier(List<String> modifiers) {
        var copy = new ArrayList<>(modifiers);
        copy.add("def");
        return copy;
    }

    private static Optional<Tuple<Node, Integer>> postVisitBlocks(Node node, int state) {
        if (!node.is(Blocks.BLOCK)) return Optional.empty();

        var childrenOptional = node.findNodeList(Blocks.CHILDREN);
        if (childrenOptional.isEmpty()) return Optional.empty();

        var newChildren = childrenOptional.get()
                .stream()
                .filter(child -> !child.is(PACKAGE))
                .flatMap(child -> flattenClass(child).orElse(Stream.of(child)))
                .toList();

        var newNode = node.withNodeList(Blocks.CHILDREN, newChildren);
        return Optional.of(new Tuple<>(newNode, state - 1));
    }

    private static Optional<Stream<Node>> flattenClass(Node child) {
        if (!child.is(Functions.FUNCTION)) return Optional.empty();
        var definition = child.findNode(Functions.DEFINITION).orElseThrow();

        var oldModifiers = definition.findStringList(MODIFIERS).orElse(Collections.emptyList());
        if (!oldModifiers.contains("class")) return Optional.empty();

        var name = definition.findString(MagmaDefinition.NAME).orElseThrow();
        var oldValue = child.findNode(Functions.VALUE).orElseThrow();
        var oldChildren = oldValue.findNodeList(Blocks.CHILDREN).orElseThrow();
        var flattened = flattenChildren(oldChildren);

        var instanceList = flattened.findInstanceNodes().map(instanceNodes -> attachInstanceChildren(child, oldValue, instanceNodes));
        var staticList = flattened.findStaticNodes().map(staticNodes -> attachStaticChildren(name, staticNodes));

        return Optionals.and(instanceList, staticList).map(tuple -> Stream.concat(tuple.left(), tuple.right()));
    }

    private static State flattenChildren(List<Node> children) {
        return children.stream().reduce(new State(),
                (state, node) -> flattenChild(node, state).orElseGet(() -> state.addInstance(node)),
                (state, state2) -> state2);
    }

    private static Optional<State> flattenChild(Node oldChild, State state) {
        return oldChild.mapNodeOptionally(DEFINITION, DefaultPasser::removeStaticModifierFromDefinition).map(state::addStatic);
    }

    private static Optional<Node> removeStaticModifierFromDefinition(Node definition) {
        var modifiers = definition.findStringList(MODIFIERS)
                .orElse(Collections.emptyList());

        if (!modifiers.contains("static")) return Optional.empty();

        var newDefinition = definition.findStringList(MODIFIERS)
                .map(oldModifiers -> removeStaticModifierFromList(oldModifiers, definition))
                .orElse(definition);

        return Optional.of(newDefinition);
    }

    private static Node removeStaticModifierFromList(List<String> oldModifiers, Node memberDefinition) {
        var copy = new ArrayList<>(oldModifiers);
        copy.remove("static");

        return copy.isEmpty()
                ? memberDefinition.removeStringList(MODIFIERS)
                : memberDefinition.withStringList(MODIFIERS, copy);
    }

    private static Stream<Node> attachStaticChildren(String name, List<Node> children) {
        var block = new Node(Blocks.BLOCK)
                .withNodeList(Blocks.CHILDREN, children);

        return Stream.of(new Node(Objects.OBJECT)
                .withString(MagmaDefinition.NAME, name + "s")
                .withNode(Objects.VALUE, block));
    }

    private static Stream<Node> attachInstanceChildren(Node parent, Node child, List<Node> children) {
        var withChildren = child.withNodeList(Blocks.CHILDREN, children);
        var withValue = parent.withNode(Functions.VALUE, withChildren);
        return Stream.of(withValue);
    }

    private static Optional<Tuple<Node, Integer>> preVisitSymbol(Node node, int state) {
        if (!node.is(Symbols.SYMBOL)) return Optional.empty();

        var mapped = node.mapString(Symbols.VALUE, value -> value.equals("void") ? "Void" : value);
        return Optional.of(new Tuple<>(mapped, state));
    }

    private static Optional<Tuple<Node, Integer>> preVisitDefinition(Node node, int state) {
        if (!node.is(DEFINITION)) return Optional.empty();

        var typeOptional = node.findNode(MagmaDefinition.TYPE);
        if (typeOptional.isEmpty()) return Optional.empty();

        var type = typeOptional.get();
        if (!type.is(Symbols.SYMBOL)) return Optional.empty();

        var value = type.findString(Symbols.VALUE).orElseThrow();
        return value.equals("var")
                ? Optional.of(new Tuple<>(node.removeNode(MagmaDefinition.TYPE), state))
                : Optional.empty();
    }

    private static Optional<Tuple<Node, Integer>> preVisitBlock(Node node, int state) {
        return node.is(Blocks.BLOCK) ? Optional.of(new Tuple<>(node, state + 1)) : Optional.empty();
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
        if (node.is(INTERFACE)) return Optional.of(new Tuple<>(node.retype(TRAIT), state));
        else return Optional.empty();
    }

    private Optional<? extends Tuple<Node, Integer>> postVisitConstructions(Node node, int state) {
        if (node.is(CONSTRUCTION)) return Optional.of(new Tuple<>(node.retype(INVOCATION), state));
        else return Optional.empty();
    }

    @Override
    public Optional<Tuple<Node, Integer>> preVisit(Node node, int state) {
        return preVisitBlock(node, state)
                .or(() -> preVisitDefinition(node, state))
                .or(() -> preVisitSymbol(node, state));
    }

    record State(List<Node> staticChildren, List<Node> instanceChildren) {
        public State() {
            this(Collections.emptyList(), Collections.emptyList());
        }

        public State addInstance(Node instanceNode) {
            var copy = new ArrayList<>(instanceChildren);
            copy.add(instanceNode);
            return new State(staticChildren, copy);
        }

        public State addStatic(Node staticNode) {
            var copy = new ArrayList<>(staticChildren);
            copy.add(staticNode);
            return new State(copy, instanceChildren);
        }

        public Optional<List<Node>> findInstanceNodes() {
            return instanceChildren.isEmpty()
                    ? Optional.empty()
                    : Optional.of(instanceChildren);
        }

        public Optional<List<Node>> findStaticNodes() {
            return staticChildren.isEmpty()
                    ? Optional.empty()
                    : Optional.of(staticChildren);
        }
    }
}