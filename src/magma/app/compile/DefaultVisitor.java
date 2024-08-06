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
import java.util.Optional;

public class DefaultVisitor implements Visitor {
    public DefaultVisitor() {
    }

    @Override
    public Optional<Tuple<Node, Integer>> postVisit(Node node, int state) {
        if (node.is(JavaLang.ARRAY)) {
            return Optional.of(new Tuple<>(node.retype(MagmaDefinition.SLICE), state));
        }

        if (node.is(MagmaDefinition.DEFINITION)) {
            var typeOptional = node.findNode(MagmaDefinition.TYPE);
            if (typeOptional.isPresent()) {
                var type = typeOptional.get();
                if (type.is(Symbols.SYMBOL)) {
                    var value = type.findString(Symbols.VALUE).orElseThrow();
                    if (value.equals("Void")) {
                        return Optional.of(new Tuple<>(node.removeNode(MagmaDefinition.TYPE), state));
                    }
                }
            }
        }

        if (node.is(CommonLang.DECLARATION_TYPE)) {
            var definition = node.findNode(CommonLang.DECLARATION_DEFINITION).orElseThrow();
            var oldModifiers = new ArrayList<>(definition.findStringList(CommonLang.MODIFIERS).orElse(Collections.emptyList()));
            oldModifiers.add("let");

            var withModifiers = definition.withStringList(CommonLang.MODIFIERS, oldModifiers);
            return Optional.of(new Tuple<>(node.withNode(CommonLang.DECLARATION_DEFINITION, withModifiers), state));
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

            return Optional.of(new Tuple<>(function, state));
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


                var newNode = node.withNodeList(Blocks.CHILDREN, newChildren);

                return Optional.of(new Tuple<>(newNode, state - 1));
            }
        }

        if (node.is(JavaLang.METHOD_TYPE)) {
            var params = node.findNodeList(CommonLang.PARAMS).orElse(Collections.emptyList());
            var definition = node.findNode(JavaLang.METHOD_DEFINITION).orElseThrow();
            var withParams = definition.withNodeList(CommonLang.PARAMS, params);
            return Optional.of(new Tuple<>(node.retype("function")
                    .removeNodeList(CommonLang.PARAMS)
                    .withNode(JavaLang.METHOD_DEFINITION, withParams), state));
        }

        return Optional.empty();
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