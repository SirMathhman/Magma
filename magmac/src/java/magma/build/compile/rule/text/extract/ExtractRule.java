package magma.build.compile.rule.text.extract;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.build.compile.CompileError;
import magma.build.compile.rule.result.ErrorRuleResult;
import magma.build.compile.rule.result.RuleResult;
import magma.build.compile.rule.result.UntypedRuleResult;
import magma.build.compile.Error_;
import magma.build.compile.attribute.Attribute;
import magma.build.compile.attribute.MapAttributes;
import magma.build.compile.rule.Node;
import magma.build.compile.rule.Rule;

import java.util.Optional;

public abstract class ExtractRule implements Rule {
    protected final String key;

    public ExtractRule(String key) {
        this.key = key;
    }

    protected abstract Optional<String> fromAttribute(Node attribute);

    protected abstract Result<Attribute, Error_> toAttribute(String content);

    @Override
    public RuleResult toNode(String input) {
        return toAttribute(input).match(
                attribute -> new UntypedRuleResult(new MapAttributes().with(key, attribute)),
                ErrorRuleResult::new);
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return fromAttribute(node)
                .map(ExtractRule::getStringErrorOk)
                .orElseGet(() -> createErr(node));
    }

    private static Result<String, Error_> getStringErrorOk(String value) {
        return new Ok<>(value);
    }

    private Err<String, Error_> createErr(Node node) {
        var format = "Property '%s' does not exist.";
        var message = format.formatted(key);
        return new Err<>(new CompileError(message, node.toString()));
    }
}
