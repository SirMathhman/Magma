package magma.app.compile;

import magma.app.compile.lang.Lang;
import magma.app.compile.rule.result.RuleResult;

public class Compiler {
    public static RuleResult<String> compile(String input, String name) {
        return Lang.createJavaRootRule().lex(input).flatMap(root -> {
            final var parsed = Transformer.transform(name, root);
            return Lang.createPlantRootRule().generate(parsed);
        });
    }
}