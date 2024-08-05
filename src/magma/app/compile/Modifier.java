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

public class Modifier {
    public Modifier() {
    }

    Tuple<Node, Integer> modifyNodeLists(Node node, Integer depth) {
        var withNodeLists = new Tuple<>(node, depth);
        for (var tuple : node.streamNodeLists().toList()) {
            var key = tuple.left();
            var oldValues = tuple.right();
            var newValues = new ArrayList<Node>();
            var current = depth;
            for (Node oldValue : oldValues) {
                var newValue = modify(oldValue, depth);
                newValues.add(newValue.left());
                current = newValue.right();
            }

            withNodeLists = new Tuple<Node, Integer>(withNodeLists.left().withNodeList(key, newValues), current);
        }
        return withNodeLists;
    }

    Tuple<Node, Integer> postVisit(Node node, int state) {
        if (node.is(JavaLang.ARRAY)) {
            return new Tuple<Node, Integer>(node.retype(MagmaDefinition.SLICE), state);
        }

        if (node.is(MagmaDefinition.DEFINITION)) {
            var typeOptional = node.findNode(MagmaDefinition.TYPE);
            if (typeOptional.isPresent()) {
                var type = typeOptional.get();
                if (type.is(Symbols.SYMBOL)) {
                    var value = type.findString(Symbols.VALUE).orElseThrow();
                    if (value.equals("Void")) {
                        return new Tuple<Node, Integer>(node.removeNode(MagmaDefinition.TYPE), state);
                    }
                }
            }
        }

        if (node.is(CommonLang.DECLARATION_TYPE)) {
            var definition = node.findNode(CommonLang.DECLARATION_DEFINITION).orElseThrow();
            var oldModifiers = new ArrayList<String>(definition.findStringList(CommonLang.MODIFIERS).orElse(Collections.emptyList()));
            oldModifiers.add("let");
            var withModifiers = definition.withStringList(CommonLang.MODIFIERS, oldModifiers);
            return new Tuple<Node, Integer>(node.withNode(CommonLang.DECLARATION_DEFINITION, withModifiers), state);
        }

        if (node.is(JavaLang.CLASS_TYPE)) {
            var name = node.findString(JavaLang.CLASS_NAME).orElseThrow();
            var oldModifiers = node.findStringList(CommonLang.MODIFIERS).orElseThrow();

            var newModifiers = new ArrayList<String>(oldModifiers
                    .stream()
                    .map(modifier -> modifier.equals("public") ? "export" : modifier)
                    .toList());

            newModifiers.add("class");
            newModifiers.add("def");

            var definition = new Node()
                    .retype(MagmaDefinition.DEFINITION)
                    .withString(MagmaDefinition.NAME, name)
                    .withStringList(CommonLang.MODIFIERS, newModifiers);

            var function = node.retype(Functions.FUNCTION)
                    .removeString(JavaLang.CLASS_NAME)
                    .removeStringList("modifiers")
                    .withNode("definition", definition);

            return new Tuple<Node, Integer>(function, state);
        }

        if (node.is(Blocks.BLOCK)) {
            var childrenOptional = node.findNodeList(Blocks.CHILDREN);
            if (childrenOptional.isPresent()) {
                var newChildren = new ArrayList<Node>();
                for (var child : childrenOptional.get()) {
                    if (child.is(JavaLang.PACKAGE)) continue;
                    if (child.is(Functions.FUNCTION)) {
                        var definition = child.findNode(Functions.DEFINITION).orElseThrow();

                        var oldModifiers = definition.findStringList(CommonLang.MODIFIERS).orElse(Collections.emptyList());
                        if (oldModifiers.contains("class")) {
                            var name = definition.findString(MagmaDefinition.NAME).orElseThrow();

                            var oldValue = child.findNode(Functions.VALUE).orElseThrow();
                            var oldChildren = oldValue.findNodeList(Blocks.CHILDREN).orElseThrow();

                            var instanceChildren = new ArrayList<Node>();
                            var staticChildren = new ArrayList<Node>();

                            for (Node oldChild : oldChildren) {
                                var definitionOptional = oldChild.findNode(MagmaDefinition.DEFINITION);
                                if (definitionOptional.isPresent()) {
                                    var memberDefinition = definitionOptional.get();
                                    var modifiers = memberDefinition.findStringList(CommonLang.MODIFIERS).orElse(Collections.emptyList());
                                    if (modifiers.contains("static")) {
                                        staticChildren.add(oldChild);
                                    } else {
                                        instanceChildren.add(oldChild);
                                    }
                                } else {
                                    instanceChildren.add(oldChild);
                                }
                            }

                            if (!instanceChildren.isEmpty()) {
                                newChildren.add(child.withNode(Functions.VALUE, oldValue.withNodeList(Blocks.CHILDREN, instanceChildren)));
                            }

                            if (!staticChildren.isEmpty()) {
                                var block = new Node(Blocks.BLOCK)
                                        .withNodeList(Blocks.CHILDREN, staticChildren);

                                newChildren.add(new Node(Objects.OBJECT)
                                        .withString(MagmaDefinition.NAME, name + "s")
                                        .withNode(Objects.VALUE, block));
                            }
                            continue;
                        }
                    }

                    newChildren.add(child);
                }

                var formatted = new ArrayList<Node>();
                for (int i = 0; i < newChildren.size(); i++) {
                    Node newChild = newChildren.get(i);
                    if (i == 0 && state == 0) {
                        formatted.add(newChild);
                    } else {
                        String indent;
                        if (state < 0) {
                            indent = "\n";
                        } else {
                            indent = "\n" + "\t".repeat(state);
                        }
                        formatted.add(newChild.withString(Blocks.BEFORE_CHILD, indent));
                    }
                }

                var blockIndent = state <= 0 ? "" : "\t".repeat(state - 1);
                var newNode = node.withNodeList(Blocks.CHILDREN, formatted)
                        .withString(Blocks.AFTER_CHILDREN, "\n" + blockIndent);

                return new Tuple<Node, Integer>(newNode, state - 1);
            }
        }

        if (node.is(JavaLang.METHOD_TYPE)) {
            var params = node.findNodeList(CommonLang.PARAMS).orElse(Collections.emptyList());
            var definition = node.findNode(JavaLang.METHOD_DEFINITION).orElseThrow();
            var withParams = definition.withNodeList(CommonLang.PARAMS, params);
            return new Tuple<Node, Integer>(node.retype("function")
                    .removeNodeList(CommonLang.PARAMS)
                    .withNode(JavaLang.METHOD_DEFINITION, withParams), state);
        }

        return new Tuple<>(node, state);
    }

    Tuple<Node, Integer> modifyNodes(Node node, int state) {
        var withNodes = new Tuple<Node, Integer>(node, state);
        for (var tuple : node.streamNodes().toList()) {
            var key = tuple.left();
            var value = tuple.right();
            var newValue = modify(value, withNodes.right());
            var newNode = withNodes.left().withNode(key, newValue.left());
            withNodes = new Tuple<Node, Integer>(newNode, newValue.right());
        }
        return withNodes;
    }

    Tuple<Node, Integer> preVisit(Node node, int state) {
        if (node.is(Blocks.BLOCK)) {
            return new Tuple<Node, Integer>(node, state + 1);
        }

        if (node.is(MagmaDefinition.DEFINITION)) {
            var typeOptional = node.findNode(MagmaDefinition.TYPE);
            if (typeOptional.isPresent()) {
                var type = typeOptional.get();
                if (type.is(Symbols.SYMBOL)) {
                    var value = type.findString(Symbols.VALUE).orElseThrow();
                    if (value.equals("var")) {
                        return new Tuple<Node, Integer>(node.removeNode(MagmaDefinition.TYPE), state);
                    }
                }
            }
        }

        if (node.is(Symbols.SYMBOL)) {
            var mapped = node.mapString(Symbols.VALUE, value -> {
                if (value.equals("void")) return "Void";
                return value;
            });

            return new Tuple<Node, Integer>(mapped, state);
        }

        return new Tuple<Node, Integer>(node, state);
    }

    Tuple<Node, Integer> modify(Node node, int depth) {
        var preVisited = preVisit(node, depth);
        var withNodes = modifyNodes(preVisited.left(), preVisited.right());
        var withNodeLists = modifyNodeLists(withNodes.left(), withNodes.right());
        return postVisit(withNodeLists.left(), withNodeLists.right());
    }
}