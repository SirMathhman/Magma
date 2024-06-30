package magma.build.compile.parse.rule.text.extract;

import magma.build.compile.error.Error_;
import magma.build.compile.parse.rule.Rule;
import magma.build.compile.parse.result.RuleResult;
import magma.build.java.JavaOptionals;

import java.util.Optional;

public final class QualifiedExtractStringListRule extends ExtractStringListRule {
    private final Rule qualifier;

    public QualifiedExtractStringListRule(String key, String delimiter, Rule qualifier) {
        super(key, delimiter);
        this.qualifier = qualifier;
    }

    @Override
    protected Optional<Error_> qualify(String child) {
        RuleResult ruleResult = qualifier.toNode(child);
        return JavaOptionals.toNative(ruleResult.findError());
    }
}