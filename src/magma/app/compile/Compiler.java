package magma.app.compile;

import magma.api.Result;
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

    public static Result<String, ApplicationException> compile(String input) {
        var sourceRootRule = JavaLang.createRootJavaRule();
        var targetRootRule = MagmaLang.createRootMagmaRule();

        var parsed = sourceRootRule.parse(input);
        return parsed.result().mapErr(err -> wrapErr(parsed))
                .mapValue(Compiler::modify)
                .flatMapValue((Node root) -> {
                    var generated = targetRootRule.generate(root);
                    return generated.result().mapErr(err -> wrapErr(generated));
                });
    }

    static ApplicationException wrapErr(RuleResult<?, ?> result) {
        return new ApplicationException(result.format(0));
    }

    public static Result<String, ApplicationException> compileAndHandle(String input) {
        return compile(input);
    }
}