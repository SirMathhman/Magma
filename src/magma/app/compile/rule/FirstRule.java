package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record FirstRule(Rule leftRule, String slice, Rule rightRule) implements Rule {
    private Optional<Node> parse0(String input) {
        var startIndex = input.indexOf(slice());
        if (startIndex == -1) return Optional.empty();
        var leftSlice = input.substring(0, startIndex);

        var leftResult = this.leftRule().parse(leftSlice).result().findValue();
        if (leftResult.isEmpty()) return Optional.empty();

        var rightSlice = input.substring(startIndex + slice.length());
        var withContentOptional = this.rightRule().parse(rightSlice).result().findValue();

        return withContentOptional.map(node -> leftResult.get().merge(node));
    }


    private Result<Node, ParseError> parse1(String input) {
        return parse0(input)
                .<Result<Node, ParseError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseError("Invalid input", input)));
    }

    private Result<String, GenerateError> generate1(Node node) {
        return leftRule.generate(node).result()
                .and(() -> rightRule.generate(node).result())
                .mapValue(tuple -> tuple.left() + slice + tuple.right());
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return new RuleResult<>(parse1(input));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return new RuleResult<>(generate1(node));
    }
}