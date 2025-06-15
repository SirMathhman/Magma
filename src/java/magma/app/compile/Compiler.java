package magma.app.compile;

import magma.app.compile.lang.Lang;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.RuleResult.RuleResultErr;
import magma.app.compile.rule.result.RuleResult.RuleResultOk;
import magma.app.compile.transform.Transformer;

public class Compiler {
    public static RuleResult<String> compile(String input, String name) {
        return switch (Lang.createJavaRootRule().lex(input)) {
            case RuleResultErr<CompoundNode>(var error) -> new RuleResultErr<>(error);
            case RuleResultOk<CompoundNode>(CompoundNode value) -> {
                final var parsed = Transformer.transform(name, value);
                yield Lang.createPlantRootRule().generate(parsed);
            }
        };
    }
}