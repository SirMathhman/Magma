package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.api.Tuple;
import magma.app.ApplicationException;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.rule.RuleResult;

import java.util.ArrayList;

import static magma.app.compile.lang.CommonLang.BEFORE_CHILD;
import static magma.app.compile.lang.CommonLang.BLOCK_TYPE;
import static magma.app.compile.lang.CommonLang.CHILDREN;
import static magma.app.compile.lang.CommonLang.MODIFIERS;
import static magma.app.compile.lang.JavaLang.CLASS_NAME;
import static magma.app.compile.lang.JavaLang.METHOD_TYPE;
import static magma.app.compile.lang.MagmaLang.DEFINITION_NAME;
import static magma.app.compile.lang.MagmaLang.DEFINITION_TYPE;

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

    private static Tuple<Node, Integer> preVisit(Node node, int depth) {
        if (node.is(BLOCK_TYPE)) {
            return new Tuple<>(node, depth + 1);
        }

        return new Tuple<>(node, depth);
    }

    private static Tuple<Node, Integer> postVisit(Node node, int depth) {
        if (node.is(BLOCK_TYPE)) {
            var childrenOptional = node.findNodeList(CHILDREN);
            if (childrenOptional.isPresent()) {
                var newChildren = new ArrayList<Node>();
                for (var child : childrenOptional.get()) {
                    if (child.is(JavaLang.PACKAGE)) continue;
                    if (child.is(JavaLang.CLASS_TYPE)) {
                        var oldModifiers = child.findStringList(MODIFIERS).orElseThrow();
                        var name = child.findString(CLASS_NAME).orElseThrow();

                        var newModifiers = new ArrayList<>(oldModifiers
                                .stream()
                                .map(modifier -> modifier.equals("public") ? "export" : modifier)
                                .toList());

                        newModifiers.add("class");
                        newModifiers.add("def");

                        var definition = new Node()
                                .retype(DEFINITION_TYPE)
                                .withStringList(MODIFIERS, newModifiers)
                                .withString(DEFINITION_NAME, name);

                        var function = child.retype(MagmaLang.FUNCTION_TYPE)
                                .removeString(CLASS_NAME)
                                .removeStringList("modifiers")
                                .withNode("definition", definition);

                        newChildren.add(function);
                    } else {
                        newChildren.add(child);
                    }
                }

                var formatted = new ArrayList<Node>();
                for (int i = 0; i < newChildren.size(); i++) {
                    Node newChild = newChildren.get(i);
                    if (i == 0 && depth == 0) {
                        formatted.add(newChild);
                    } else {
                        String indent;
                        if (depth < 0) {
                            indent = "\n";
                        } else {
                            indent = "\n" + "\t".repeat(depth);
                        }
                        formatted.add(newChild.withString(BEFORE_CHILD, indent));
                    }
                }

                return new Tuple<>(node.withNodeList(CHILDREN, formatted), depth - 1);
            }
        }

        if (node.is(METHOD_TYPE)) {
            return new Tuple<>(node.retype("function"), depth);
        }

        return new Tuple<>(node, depth);
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