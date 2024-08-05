package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.app.ApplicationException;
import magma.app.compile.lang.CommonLang;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.rule.RuleResult;

import java.util.ArrayList;
import java.util.List;

import static magma.app.compile.lang.JavaLang.CLASS_NAME;
import static magma.app.compile.lang.JavaLang.CLASS_MODIFIERS;

public class Compiler {
    private static Node modify(Node node) {
        var childrenOptional = node.findNodeList(CommonLang.CHILDREN);
        if (childrenOptional.isEmpty()) return node;

        var copy = new ArrayList<Node>();
        for (var child : childrenOptional.get()) {
            if (child.is(JavaLang.PACKAGE)) continue;

            if (child.is(JavaLang.CLASS)) {
                var oldModifiers = child.findStringList(CLASS_MODIFIERS).orElseThrow();
                var name = child.findString(CLASS_NAME).orElseThrow();

                var newModifiers = oldModifiers
                        .stream()
                        .map(modifier -> modifier.equals("public") ? "export" : modifier)
                        .toList();

                var definition = new Node()
                        .retype("definition")
                        .withStringList("modifiers", newModifiers)
                        .withString("name", name);

                var function = child.retype(MagmaLang.FUNCTION)
                        .removeString(CLASS_NAME)
                        .removeStringList("modifiers")
                        .withNode("child", definition);

                copy.add(function);
            } else {
                copy.add(child);
            }
        }

        return node.withNodeList(CommonLang.CHILDREN, copy);
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