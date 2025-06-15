package magma.app.compile;

import magma.app.compile.lang.Lang;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.transform.Transformer;

public class Compiler {
    public static RuleResult<String> compile(String input, String name) {
        RuleResult<CompoundNode> compoundNodeRuleResult = Lang.createJavaRootRule().lex(input);
        return compoundNodeRuleResult.match(root -> {
            final var parsed = Transformer.transform(name, root);
            return Lang.createPlantRootRule().generate(parsed);
        }, RuleResult.Err::new);
    }
}