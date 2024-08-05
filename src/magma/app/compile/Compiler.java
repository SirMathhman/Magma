package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.api.Tuple;
import magma.app.ApplicationException;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.lang.common.Symbols;
import magma.app.compile.lang.magma.Functions;
import magma.app.compile.lang.magma.MagmaDefinition;
import magma.app.compile.lang.magma.Objects;
import magma.app.compile.rule.RuleResult;

import java.util.ArrayList;
import java.util.Collections;

import static magma.app.compile.lang.CommonLang.DECLARATION_DEFINITION;
import static magma.app.compile.lang.CommonLang.DECLARATION_TYPE;
import static magma.app.compile.lang.CommonLang.MODIFIERS;
import static magma.app.compile.lang.CommonLang.PARAMS;
import static magma.app.compile.lang.JavaLang.ARRAY;
import static magma.app.compile.lang.JavaLang.CLASS_NAME;
import static magma.app.compile.lang.JavaLang.METHOD_DEFINITION;
import static magma.app.compile.lang.JavaLang.METHOD_TYPE;
import static magma.app.compile.lang.common.Blocks.AFTER_CHILDREN;
import static magma.app.compile.lang.common.Blocks.BEFORE_CHILD;
import static magma.app.compile.lang.common.Blocks.BLOCK;
import static magma.app.compile.lang.common.Blocks.CHILDREN;
import static magma.app.compile.lang.magma.MagmaDefinition.DEFINITION;
import static magma.app.compile.lang.magma.MagmaDefinition.NAME;
import static magma.app.compile.lang.magma.MagmaDefinition.SLICE;
import static magma.app.compile.lang.magma.MagmaDefinition.TYPE;

public class Compiler {
    private static Tuple<Node, Integer> modify(Node node, int depth) {
        var preVisited = preVisit(node, depth);
        var withNodes = modifyNodes(preVisited.left(), preVisited.right());
        var withNodeLists = modifyNodeLists(withNodes.left(), withNodes.right());
        return postVisit(withNodeLists.left(), withNodeLists.right());
    }

    private static Tuple<Node, Integer> modifyNodeLists(Node node, Integer depth) {
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

            withNodeLists = new Tuple<>(withNodeLists.left().withNodeList(key, newValues), current);
        }
        return withNodeLists;
    }

    private static Tuple<Node, Integer> modifyNodes(Node node, int state) {
        var withNodes = new Tuple<>(node, state);
        for (var tuple : node.streamNodes().toList()) {
            var key = tuple.left();
            var value = tuple.right();
            var newValue = modify(value, withNodes.right());
            var newNode = withNodes.left().withNode(key, newValue.left());
            withNodes = new Tuple<>(newNode, newValue.right());
        }
        return withNodes;
    }

    private static Tuple<Node, Integer> preVisit(Node node, int state) {
        if (node.is(BLOCK)) {
            return new Tuple<>(node, state + 1);
        }

        if (node.is(DEFINITION)) {
            var typeOptional = node.findNode(TYPE);
            if (typeOptional.isPresent()) {
                var type = typeOptional.get();
                if (type.is(Symbols.SYMBOL)) {
                    var value = type.findString(Symbols.VALUE).orElseThrow();
                    if (value.equals("var")) {
                        return new Tuple<>(node.removeNode(TYPE), state);
                    }
                }
            }
        }

        if (node.is(Symbols.SYMBOL)) {
            var mapped = node.mapString(Symbols.VALUE, value -> {
                if (value.equals("void")) return "Void";
                return value;
            });

            return new Tuple<>(mapped, state);
        }

        return new Tuple<>(node, state);
    }

    private static Tuple<Node, Integer> postVisit(Node node, int state) {
        if (node.is(ARRAY)) {
            return new Tuple<>(node.retype(SLICE), state);
        }

        if (node.is(DEFINITION)) {
            var typeOptional = node.findNode(TYPE);
            if (typeOptional.isPresent()) {
                var type = typeOptional.get();
                if (type.is(Symbols.SYMBOL)) {
                    var value = type.findString(Symbols.VALUE).orElseThrow();
                    if (value.equals("Void")) {
                        return new Tuple<>(node.removeNode(TYPE), state);
                    }
                }
            }
        }

        if (node.is(DECLARATION_TYPE)) {
            var definition = node.findNode(DECLARATION_DEFINITION).orElseThrow();
            var oldModifiers = new ArrayList<>(definition.findStringList(MODIFIERS).orElse(Collections.emptyList()));
            oldModifiers.add("let");
            var withModifiers = definition.withStringList(MODIFIERS, oldModifiers);
            return new Tuple<>(node.withNode(DECLARATION_DEFINITION, withModifiers), state);
        }

        if (node.is(JavaLang.CLASS_TYPE)) {
            var name = node.findString(CLASS_NAME).orElseThrow();
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
                    .removeString(CLASS_NAME)
                    .removeStringList("modifiers")
                    .withNode("definition", definition);

            return new Tuple<>(function, state);
        }

        if (node.is(BLOCK)) {
            var childrenOptional = node.findNodeList(CHILDREN);
            if (childrenOptional.isPresent()) {
                var newChildren = new ArrayList<Node>();
                for (var child : childrenOptional.get()) {
                    if (child.is(JavaLang.PACKAGE)) continue;
                    if (child.is(Functions.FUNCTION)) {
                        var definition = child.findNode(Functions.DEFINITION).orElseThrow();

                        var oldModifiers = definition.findStringList(MODIFIERS).orElse(Collections.emptyList());
                        if (oldModifiers.contains("class")) {
                            var name = definition.findString(MagmaDefinition.NAME).orElseThrow();

                            var oldValue = child.findNode(Functions.VALUE).orElseThrow();
                            var oldChildren = oldValue.findNodeList(CHILDREN).orElseThrow();

                            var instanceChildren = new ArrayList<Node>();
                            var staticChildren = new ArrayList<Node>();

                            for (Node oldChild : oldChildren) {
                                var definitionOptional = oldChild.findNode(DEFINITION);
                                if (definitionOptional.isPresent()) {
                                    var memberDefinition = definitionOptional.get();
                                    var modifiers = memberDefinition.findStringList(MODIFIERS).orElse(Collections.emptyList());
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
                                newChildren.add(child.withNode(Functions.VALUE, oldValue.withNodeList(CHILDREN, instanceChildren)));
                            }

                            if (!staticChildren.isEmpty()) {
                                var block = new Node(BLOCK)
                                        .withNodeList(CHILDREN, staticChildren);

                                newChildren.add(new Node(Objects.OBJECT)
                                        .withString(NAME, name + "s")
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
                        formatted.add(newChild.withString(BEFORE_CHILD, indent));
                    }
                }

                var blockIndent = state <= 0 ? "" : "\t".repeat(state - 1);
                var newNode = node.withNodeList(CHILDREN, formatted)
                        .withString(AFTER_CHILDREN, "\n" + blockIndent);

                return new Tuple<>(newNode, state - 1);
            }
        }

        if (node.is(METHOD_TYPE)) {
            var params = node.findNodeList(PARAMS).orElse(Collections.emptyList());
            var definition = node.findNode(METHOD_DEFINITION).orElseThrow();
            var withParams = definition.withNodeList(PARAMS, params);
            return new Tuple<>(node.retype("function")
                    .removeNodeList(PARAMS)
                    .withNode(METHOD_DEFINITION, withParams), state);
        }

        return new Tuple<>(node, state);
    }

    public static Result<CompileResult, ApplicationException> compile(String input) {
        var sourceRootRule = JavaLang.createRootJavaRule();
        var targetRootRule = MagmaLang.createRootMagmaRule();

        return Results.$Result(() -> {
            var parsedResult = sourceRootRule.parse(input);
            var parsed = parsedResult.result().replaceErr(() -> wrapErr(parsedResult)).$();
            var modified = modify(parsed, -1);
            var generatedResult = targetRootRule.generate(modified.left());
            var generated = generatedResult.result().replaceErr(() -> wrapErr(generatedResult)).$();
            return new CompileResult(generated, parsed, modified.left());
        });
    }

    static ApplicationException wrapErr(RuleResult<?, ?> result) {
        return new ApplicationException(result.format(0));
    }
}