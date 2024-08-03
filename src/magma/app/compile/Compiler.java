package magma.app.compile;

import magma.api.Result;
import magma.app.ApplicationException;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.lang.CommonLang;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.rule.Rule;

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

    public static Result<String, CompileError> compile(String input) {
        return JavaLang.createRootJavaRule().parse(input).result()
                .<CompileError>mapErr(err -> err)
                .mapValue(Compiler::modify)
                .flatMapValue((Node root) -> {
                    Rule rule1 = MagmaLang.createRootMagmaRule();
                    return rule1.generate(root).result().mapErr(err -> err);
                });
    }

    public static ApplicationException handleError(CompileError err) {
        System.err.println(err.format());
        return new ApplicationException("Compilation error.");
    }

    public static Result<String, ApplicationException> compileAndHandle(String input) {
        return compile(input)
                .mapErr(Compiler::handleError);
    }
}