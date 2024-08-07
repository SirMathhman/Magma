package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.app.ApplicationException;
import magma.app.Unit;
import magma.app.compile.lang.java.JavaLang;
import magma.app.compile.lang.magma.MagmaLang;
import magma.app.compile.pass.CompoundModifyingStage;
import magma.app.compile.pass.DefaultPasser;
import magma.app.compile.pass.MagmaFormatter;
import magma.app.compile.pass.MainPasser;
import magma.app.compile.pass.VisitingModifyingStage;
import magma.app.compile.rule.RuleResult;

import java.util.List;

public class Compiler {
    public static Result<CompileResult, ApplicationException> compile(Unit unit, String input) {
        var sourceRootRule = JavaLang.createRootJavaRule();
        var targetRootRule = MagmaLang.createRootMagmaRule();

        return Results.$Result(() -> {
            var parsedResult = sourceRootRule.parse(input);
            var parsed = parsedResult.result().replaceErr(() -> wrapErr(unit, parsedResult)).$();
            var modified = createModifyingStage().modify(parsed, -1);
            var generatedResult = targetRootRule.generate(modified.left());
            var generated = generatedResult.result().replaceErr(() -> wrapErr(unit, generatedResult)).$();
            return new CompileResult(generated, parsed, modified.left());
        });
    }

    private static CompoundModifyingStage createModifyingStage() {
        return new CompoundModifyingStage(List.of(
                new VisitingModifyingStage(new DefaultPasser()),
                new VisitingModifyingStage(new MainPasser()),
                new VisitingModifyingStage(new MagmaFormatter())
        ));
    }

    static ApplicationException wrapErr(Unit unit, RuleResult<?, ?> result) {
        return new ApplicationException(unit.format() + ": " + result.format(0));
    }
}