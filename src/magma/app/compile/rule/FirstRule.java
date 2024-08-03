package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.Optional;
import java.util.function.BiFunction;

public record FirstRule(Rule leftRule, String slice, Rule rightRule) implements Rule {
    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        var startIndex = input.indexOf(this.slice());
        if (startIndex == -1) return new RuleResult<>(new Err<>(new ParseError("Slice '" + slice + "' not present", input)));
        var leftSlice = input.substring(0, startIndex);

        var leftResult = this.leftRule().parse(leftSlice);

        var rightSlice = input.substring(startIndex + slice.length());
        return leftResult.and(() -> this.rightRule().parse(rightSlice), Node::merge);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return leftRule.generate(node).and(() -> rightRule.generate(node), (left, right) -> left + slice + right);
    }
}