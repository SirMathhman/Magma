package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.app.ApplicationException;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.rule.RuleResult;

public class Compiler {
    public static Result<CompileResult, ApplicationException> compile(String input) {
        var sourceRootRule = JavaLang.createRootJavaRule();
        var targetRootRule = MagmaLang.createRootMagmaRule();

        return Results.$Result(() -> {
            var parsedResult = sourceRootRule.parse(input);
            var parsed = parsedResult.result().replaceErr(() -> wrapErr(parsedResult)).$();
            var modified = new Modifier().modify(parsed, -1);
            var generatedResult = targetRootRule.generate(modified.left());
            var generated = generatedResult.result().replaceErr(() -> wrapErr(generatedResult)).$();
            return new CompileResult(generated, parsed, modified.left());
        });
    }

    static ApplicationException wrapErr(RuleResult<?, ?> result) {
        return new ApplicationException(result.format(0));
    }
}