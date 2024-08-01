package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
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


    private Result<Node, ParseException> parse1(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    private Result<String, GenerateException> generate1(Node node) {
        return leftRule.generate(node).result()
                .and(() -> rightRule.generate(node).result())
                .mapValue(tuple -> tuple.left() + slice + tuple.right());
    }

    @Override
    public RuleResult<Node, ParseException> parse(String input) {
        return new RuleResult<>(parse1(input));
    }

    @Override
    public RuleResult<String, GenerateException> generate(Node node) {
        return new RuleResult<>(generate1(node));
    }
}