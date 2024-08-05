package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.api.Tuple;
import magma.app.ApplicationException;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.rule.RuleResult;

import java.util.ArrayList;
import java.util.List;

import static magma.app.compile.lang.CommonLang.BEFORE_CHILD;
import static magma.app.compile.lang.CommonLang.BLOCK_TYPE;
import static magma.app.compile.lang.CommonLang.CHILDREN;
import static magma.app.compile.lang.CommonLang.MODIFIERS;
import static magma.app.compile.lang.JavaLang.CLASS_TYPE;
import static magma.app.compile.lang.JavaLang.CLASS_NAME;
import static magma.app.compile.lang.JavaLang.METHOD_TYPE;
import static magma.app.compile.lang.MagmaLang.DEFINITION_NAME;
import static magma.app.compile.lang.MagmaLang.DEFINITION_TYPE;

public class Compiler {
    private static Node modify(Node node) {
        var withNodes = node;
        for (Tuple<String, Node> tuple : node.streamNodes().toList()) {
            var key = tuple.left();
            var value = tuple.right();
            var newValue = modify(value);
            withNodes = withNodes.withNode(key, newValue);
        }

        var withNodeLists = withNodes;
        for (Tuple<String, List<Node>> tuple : node.streamNodeLists().toList()) {
            var key = tuple.left();
            var oldValues = tuple.right();
            var newValues = new ArrayList<Node>();
            for (Node oldValue : oldValues) {
                var newValue = modify(oldValue);
                newValues.add(newValue);
            }
            withNodeLists = withNodeLists.withNodeList(key, newValues);
        }

        return postVisit(withNodeLists);
    }

    private static Node postVisit(Node node) {
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
                    formatted.add(i == 0 ? newChild : newChild.withString(BEFORE_CHILD, "\n"));
                }

                return node.withNodeList(CHILDREN, formatted);
            }
        }

        if (node.is(METHOD_TYPE)) {
            return node.retype("function");
        }

        return node;
    }

    public static Result<CompileResult, ApplicationException> compile(String input) {
        var sourceRootRule = JavaLang.createRootJavaRule();
        var targetRootRule = MagmaLang.createRootMagmaRule();

        return Results.$Result(() -> {
            var parsedResult = sourceRootRule.parse(input);
            var parsed = parsedResult.result().replaceErr(() -> wrapErr(parsedResult)).$();
            var modified = modify(parsed);
            var generatedResult = targetRootRule.generate(modified);
            var generated = generatedResult.result().replaceErr(() -> wrapErr(generatedResult)).$();
            return new CompileResult(generated, parsed, modified);
        });
    }

    static ApplicationException wrapErr(RuleResult<?, ?> result) {
        return new ApplicationException(result.format(0));
    }
}