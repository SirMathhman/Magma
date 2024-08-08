package magma.app.compile;

import magma.api.Result;
import magma.app.ApplicationException;
import magma.app.Unit;
import magma.app.compile.pass.CompoundModifyingStage;
import magma.app.compile.pass.DefaultPasser;
import magma.app.compile.pass.MagmaFormatter;
import magma.app.compile.pass.MainPasser;
import magma.app.compile.pass.VisitingModifyingStage;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.RuleResult;

import java.util.List;

public class Compiler {
    private final Rule sourceRootRule;
    private final Rule targetRootRule;

    public Compiler(Rule sourceRootRule, Rule targetRootRule) {
        this.sourceRootRule = sourceRootRule;
        this.targetRootRule = targetRootRule;
    }

    static <T, E extends CompileError> ApplicationException wrapErr(Unit unit, RuleResult<T, E> result) {
        return new ApplicationException(unit.format() + ": " + result.format(0));
    }

    private CompoundModifyingStage createModifyingStage() {
        return new CompoundModifyingStage(List.of(
                new VisitingModifyingStage(new DefaultPasser()),
                new VisitingModifyingStage(new MainPasser()),
                new VisitingModifyingStage(new MagmaFormatter())
        ));
    }

    private Result<CompileResult, ApplicationException> modifyAndWrite(Unit unit, Node parsed) {
        var modified = createModifyingStage().modify(parsed, -1);
        var generatedResult = targetRootRule.generate(modified.left());

        return generatedResult.result()
                .replaceErr(() -> wrapErr(unit, generatedResult))
                .mapValue(generated -> new CompileResult(generated, parsed, modified.left()));
    }

    public Result<CompileResult, ApplicationException> compile(Unit unit, String input) {
        var parsedResult = sourceRootRule.parse(input);
        return parsedResult.result()
                .replaceErr(() -> wrapErr(unit, parsedResult))
                .flatMapValue(parsed -> modifyAndWrite(unit, parsed));
    }
}