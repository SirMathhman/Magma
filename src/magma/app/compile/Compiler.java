package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.app.ApplicationException;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.rule.RuleResult;

import java.util.List;

public class Compiler {
    public static Result<CompileResult, ApplicationException> compile(String input) {
        var sourceRootRule = JavaLang.createRootJavaRule();
        var targetRootRule = MagmaLang.createRootMagmaRule();

        return Results.$Result(() -> {
            var parsedResult = sourceRootRule.parse(input);
            var parsed = parsedResult.result().replaceErr(() -> wrapErr(parsedResult)).$();
            var modified = createModifyingStage().modify(parsed, -1);
            var generatedResult = targetRootRule.generate(modified.left());
            var generated = generatedResult.result().replaceErr(() -> wrapErr(generatedResult)).$();
            return new CompileResult(generated, parsed, modified.left());
        });
    }

    private static CompoundModifyingStage createModifyingStage() {
        return new CompoundModifyingStage(List.of(
                new VisitingModifyingStage(new DefaultVisitor()),
                new VisitingModifyingStage(new Formatter())
        ));
    }

    static ApplicationException wrapErr(RuleResult<?, ?> result) {
        return new ApplicationException(result.format(0));
    }
}