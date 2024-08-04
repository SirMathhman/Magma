package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.app.ApplicationException;
import magma.app.compile.lang.CommonLang;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.rule.RuleResult;

import java.util.ArrayList;

public class Compiler {
    private static Node modify(Node node) {
        var childrenOptional = node.findNodeList(CommonLang.CHILDREN);
        if (childrenOptional.isEmpty()) return node;

        var copy = new ArrayList<Node>();
        for (var child : childrenOptional.get()) {
            if (child.is(JavaLang.PACKAGE)) continue;

            if (child.is(JavaLang.CLASS)) {
                copy.add(child.retype(MagmaLang.FUNCTION).mapString(CommonLang.MODIFIERS, oldModifiers -> {
                    var newAccessor = oldModifiers.equals(JavaLang.PUBLIC_KEYWORD_WITH_SPACE) ? MagmaLang.EXPORT_KEYWORD_WITH_SPACE : "";
                    return newAccessor + CommonLang.CLASS_KEYWORD_WITH_SPACE;
                }));
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